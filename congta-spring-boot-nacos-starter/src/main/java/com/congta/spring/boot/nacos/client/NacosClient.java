package com.congta.spring.boot.nacos.client;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.congta.spring.boot.shared.security.KeyCenterFactory;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangfucheng on 2021/11/1.
 */
public class NacosClient implements ConfigService {

    private static Logger log = LoggerFactory.getLogger(NacosClient.class);

    private NacosEnv env;

    private int timeout;

    private ConfigService configService;

    public NacosClient(NacosEnv env, int timeout) {
        this.env = env;
        this.timeout = timeout;
    }

    public ConfigService getConfigService() throws NacosException {
        // 允许并发冲突
        ConfigService localService = configService;
        if (localService == null) {
            Properties properties = new Properties();
            properties.put("serverAddr", env.uri);
            if (StringUtils.isNotBlank(env.username)) {
                if (StringUtils.isNotBlank(env.kcSid)) {
                    properties.put("username", KeyCenterFactory.get(env.kcSid).decrypt(env.username));
                    properties.put("password", KeyCenterFactory.get(env.kcSid).decrypt(env.password));
                } else {
                    properties.put("username", env.username);
                    properties.put("password", env.password);
                }
            }
            properties.put("namespace", env.namespace);
            log.warn("create new config service instance for {}", env.name());
            localService = NacosFactory.createConfigService(properties);
            configService = localService;
        }
        return localService;
    }

    @Override
    public String getConfig(String dataId, String group, long timeoutMs) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().getConfig(dataId, group, timeoutMs));
    }

    public String getConfig(String dataId, String group) {
        return supplyWithAutoRefreshToken(() ->
                getConfig(dataId, group, timeout));
    }

    @Override
    public String getConfigAndSignListener(String dataId, String group, long timeoutMs, Listener listener) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().getConfigAndSignListener(dataId, group, timeoutMs, listener));
    }

    public String getConfigAndSignListener(String dataId, String group, NacosListener listener) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().getConfigAndSignListener(dataId, group, timeout, listener));
    }

    public void signListenerAndCallOnInit(String dataId, String group, NacosListener listener) {
        String data = getConfigAndSignListener(dataId, group, listener);
        listener.receiveConfigInfo(data);
    }

    @Override
    public void addListener(String dataId, String group, Listener listener) {
        runWithAutoRefreshToken(() ->
                getConfigService().addListener(dataId, group, listener));
    }

    @Override
    public boolean publishConfig(String dataId, String group, String content) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().publishConfig(dataId, group, content));
    }

    @Override
    public boolean publishConfig(String dataId, String group, String content, String type) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().publishConfig(dataId, group, content, type));
    }

    @Override
    public boolean publishConfigCas(String dataId, String group, String content, String casMd5) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().publishConfigCas(dataId, group, content, casMd5));
    }

    @Override
    public boolean publishConfigCas(String dataId, String group, String content, String casMd5, String type) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().publishConfigCas(dataId, group, content, casMd5, type));
    }

    @Override
    public boolean removeConfig(String dataId, String group) {
        return supplyWithAutoRefreshToken(() ->
                getConfigService().removeConfig(dataId, group));
    }

    @Override
    public void removeListener(String dataId, String group, Listener listener) {
        runWithAutoRefreshToken(() -> getConfigService().removeListener(dataId, group, listener));
    }

    @Override
    public String getServerStatus() {
        return supplyWithAutoRefreshToken(() -> getConfigService().getServerStatus());
    }

    @Override
    public void shutDown() {
        try {
            // 失败就失败了，无需刷新重试
            getConfigService().shutDown();
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    /**
     * configService 好像不会自动刷新 token，但是也没找到有刷新 token 的方法，所以重新创建一个，
     * 在网上竟然没搜到有人问这个问题，我的打开方式不对？？？
     *
     * 服务报错： Caused by: com.alibaba.nacos.api.exception.NacosException: http error, code=403,dataId=xxx,group=yyy,tenant=zzz
     * nacos日志：[http-nio-8090-exec-2:c.a.n.c.c.i.ClientWorker] [config_rpc_client] [sub-server-error]
     *           dataId=xxx, group=yyy, tenant=zzz, code=Response{resultCode=500, errorCode=403, message='token expired!', requestId='null'}
     */
    private <T> T supplyWithAutoRefreshToken(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NacosException e) {
            if (e.getErrCode() != 403) {
                throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "put/get config error", e);
            }
        }
        this.configService = null;
        try {
            return supplier.get();
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "put/get config error after refresh", e);
        }
    }

    private void runWithAutoRefreshToken(Runner runner) {
        try {
            runner.run();
            return;
        } catch (NacosException e) {
            if (e.getErrCode() != 403) {
                throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "put/get config error", e);
            }
        }
        this.configService = null;
        try {
            runner.run();
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "put/get config error after refresh", e);
        }
    }

    interface Supplier<T> {
        T get() throws NacosException;
    }

    interface Runner {
        void run() throws NacosException;
    }
}
