package com.congta.spring.boot.storage;

import com.congta.spring.boot.shared.ex.CtValidator;
import com.congta.spring.boot.storage.bucket.OperableBucket;
import com.congta.spring.boot.storage.bucket.QiNiuBucket;
import com.congta.spring.boot.storage.config.StorageBucketConfig;
import com.congta.spring.boot.storage.config.StorageConfig;
import com.congta.spring.boot.storage.config.StorageNsConfig;
import com.qiniu.util.Auth;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Fucheng
 * created in 2021/11/13
 */
public class StorageService {

    private StorageConfig storageConfig;

    private Auth authQiNiu;

    private Map<String, OperableBucket> buckets;

    StorageService(Auth authQiNiu, StorageConfig storageConfig) {
        this.authQiNiu = authQiNiu;
        updateConfig(storageConfig);
    }

    void updateConfig(StorageConfig storageConfig) {
        this.storageConfig = storageConfig;
        buckets = new ConcurrentHashMap<>();
    }

    public String upload(byte[] file, String ns) {
        return loadBucket(ns).upload(file, ns, null);
    }

    public String replace(byte[] file, String ns, String key) {
        StorageNsConfig nsConfig = getNsConfig(ns);
        CtValidator.arg(nsConfig.isReplaceable(), "ns is not replaceable " + ns);
        return loadBucket(ns).upload(file, ns, key);
    }

    public String getUrl(String key) {
        return getUrl(key, -1, -1);
    }

    public String getUrl(String key, int width, int height) {
        String url = loadBucket(key).getUrl(key, width, height);
        String ns = getNamespace(key);
        StorageNsConfig nsConfig = storageConfig.getNamespaces().get(ns);
        if (StringUtils.isNotBlank(nsConfig.getStyle())) {
            url += nsConfig.getStyle();
        }
        return url;
    }

    public StorageNsConfig getNsConfig(String ns) {
        ns = getNamespace(ns);
        return storageConfig.getNamespaces().get(ns);
    }

    private OperableBucket loadBucket(String key) {
        StorageNsConfig nsConfig = getNsConfig(key);
        CtValidator.notNull(nsConfig, "storage namespace not found for " + key);
        String bucketName = nsConfig.getBucket();
        StorageBucketConfig bkConfig = storageConfig.getBuckets().get(bucketName);
        CtValidator.notNull(bkConfig, "storage bucket not found for " + bucketName);

        return buckets.computeIfAbsent(bucketName, bn -> {
            String provider = StringUtils.defaultString(bkConfig.getProvider()).toUpperCase();
            switch (provider) {
                case "QINIU":
                default:
                    CtValidator.stateNotNull(authQiNiu, "storage config missing for qn");
                    return new QiNiuBucket(bn, authQiNiu, bkConfig);
            }
        });
    }

    private String getNamespace(String key) {
        key = StringUtils.removeStart(key, "/");
        return StringUtils.substringBefore(key, "/");
    }
}
