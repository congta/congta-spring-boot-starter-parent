package com.congta.spring.boot.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.watch.WatchEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by zhfch on 2022/9/12.
 */
public class CdClientImpl extends CdClient {

    private static final Logger log = LoggerFactory.getLogger(CdClientImpl.class);

    private final Client client;
    private final String prefix;

    private final ConcurrentMap<String, Watch.Watcher> watchers = new ConcurrentHashMap<>();

    public CdClientImpl(String uri, byte[] username, byte[] password, String prefix) {
        client = Client.builder().endpoints(uri)
                .user(ByteSequence.from(username))
                .password(ByteSequence.from(password))
                .build();
        this.prefix = prefix;
    }

    @Override
    public void getAndWatchString(String path, Consumer<String> consumer) {
        ByteSequence key = ByteSequence.from(prefix + path, StandardCharsets.UTF_8);
        CompletableFuture<GetResponse> getFuture = client.getKVClient().get(key);
        try {
            GetResponse response = getFuture.get(3, TimeUnit.SECONDS);
            if (CollectionUtils.size(response.getKvs()) > 0) {
                if (CollectionUtils.size(response.getKvs()) > 1) {
                    log.warn("more than 1 path on key [{}]", path);
                }
                consumer.accept(response.getKvs().get(0).getValue().toString(StandardCharsets.UTF_8));
            } else {
                throw new CdException("node [" + path + "] not exist").withCode(CdErrorCode.NODE_NOT_EXIST);
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("get and watch etcd error for path: {}, reason: {}", key.toString(StandardCharsets.UTF_8), String.valueOf(e));
            throw new RuntimeException(e);
        }
        if (watchers.containsKey(path)) {
            log.warn("ignore register watcher on duplicate path {}", path);
            return;
        }
        // TODO(zhangfucheng) 支持同一个 path 注册多个 watcher
        Watch.Watcher watcher = client.getWatchClient().watch(key, watchResponse -> {
            for (WatchEvent event : watchResponse.getEvents()) {
                log.info("etcd changed on key [{}], type: {}", event.getKeyValue().getKey(), event.getEventType().name());
                consumer.accept(event.getKeyValue().getValue().toString(StandardCharsets.UTF_8));
            }
        });
        if (watchers.putIfAbsent(path, watcher) != null) {
            watcher.close();
        }
    }

    @Override
    public void put(String path, String value) {
        ByteSequence key = ByteSequence.from(prefix + path, StandardCharsets.UTF_8);
        ByteSequence v = ByteSequence.from(value, StandardCharsets.UTF_8);
        CompletableFuture<PutResponse> getFuture = client.getKVClient().put(key, v);
        try {
            getFuture.get(3, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("get etcd error for path: {}, reason: {}", key.toString(StandardCharsets.UTF_8), String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        watchers.forEach((k, w) -> w.close());
    }
}
