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

/**
 * Created by zhangfucheng on 2021/11/1.
 */
public class NacosClient implements ConfigService {

    private NacosEnv env;

    private int timeout;

    private ConfigService configService;

    public NacosClient(NacosEnv env, int timeout) {
        this.env = env;
        this.timeout = timeout;
    }

    public ConfigService getConfigService() throws NacosException {
        // 允许并发冲突
        if (configService == null) {
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
            properties.put("namespace", env.name);
            configService = NacosFactory.createConfigService(properties);
        }
        return configService;
    }

    @Override
    public String getConfig(String dataId, String group, long timeoutMs) {
        try {
            return getConfigService().getConfig(dataId, group, timeoutMs);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    public String getConfig(String dataId, String group) {
        return getConfig(dataId, group, timeout);
    }

    @Override
    public String getConfigAndSignListener(String dataId, String group, long timeoutMs, Listener listener) {
        try {
            return getConfigService().getConfigAndSignListener(dataId, group, timeoutMs, listener);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    public String getConfigAndSignListener(String dataId, String group, NacosListener listener) {
        try {
            return getConfigService().getConfigAndSignListener(dataId, group, timeout, listener);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR,
                    "get config and register nacos listener error", e);
        }
    }

    @Override
    public void addListener(String dataId, String group, Listener listener) {
        try {
            getConfigService().addListener(dataId, group, listener);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public boolean publishConfig(String dataId, String group, String content) {
        try {
            return getConfigService().publishConfig(dataId, group, content);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public boolean publishConfig(String dataId, String group, String content, String type) {
        try {
            return getConfigService().publishConfig(dataId, group, content, type);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public boolean publishConfigCas(String dataId, String group, String content, String casMd5) {
        try {
            return getConfigService().publishConfigCas(dataId, group, content, casMd5);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public boolean publishConfigCas(String dataId, String group, String content, String casMd5, String type) {
        try {
            return getConfigService().publishConfigCas(dataId, group, content, casMd5, type);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public boolean removeConfig(String dataId, String group) {
        try {
            return getConfigService().removeConfig(dataId, group);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public void removeListener(String dataId, String group, Listener listener) {
        try {
            getConfigService().removeListener(dataId, group, listener);
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public String getServerStatus() {
        try {
            return getConfigService().getServerStatus();
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }

    @Override
    public void shutDown() {
        try {
            getConfigService().shutDown();
        } catch (NacosException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "get config from nacos error", e);
        }
    }
}
