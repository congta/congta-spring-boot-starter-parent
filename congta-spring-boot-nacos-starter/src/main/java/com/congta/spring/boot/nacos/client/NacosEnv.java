package com.congta.spring.boot.nacos.client;

import com.congta.spring.boot.nacos.CongtaNacosProperties;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Created by zhangfucheng on 2021/11/1.
 */
public class NacosEnv {

    String name;
    String uri;
    String namespace;
    String kcSid;
    String username;
    String password;

    private NacosEnv() { }

    static NacosEnv create(String name, String uri, String namespace,
                           String kcSid, String username, String password) {
        Validate.notBlank(name, "nacos env can not be blank");
        name = name.toUpperCase();
        Validate.isTrue(!ENV_MAP.containsKey(name), "nacos env is already defined");
        NacosEnv env = new NacosEnv();
        env.name = name;
        env.uri = uri;
        env.namespace = namespace;
        env.kcSid = kcSid;
        env.username = username;
        env.password = password;
        ENV_MAP.put(name, env);
        return env;
    }

    public String name() {
        return name;
    }

    private static final String CN_URL = "http://cn.nacos.congta.com:8848";

    private static final Map<String, NacosEnv> ENV_MAP = new ConcurrentHashMap<>();

    public static final NacosEnv PUBLIC = create(
            "public", CN_URL, "", "", "", "");

    public static final NacosEnv MTCN = create(
            "mtcn", CN_URL,
            "37cc9a94-8541-4bc0-b93d-0a0d674984d6",
            "maptime-prod",
            "AAAAAX6mPFbZu9_Lep0Lm1GUHcCBaKZk_AiOfQ",
            "AAAAAn744y3BEo9djYB2X9SzFYdH7PlhsJrnDLnTv0sdBLffYcY");

    public static final NacosEnv MTUS = create(
            "mtus", CN_URL,
            "7bfbd3ad-b98c-44a9-a4ac-eb46b7ac9bc1",
            "maptime",
            "AAAAAH6fZE58YoU",
            "AAAAAH6kIkN1XIc-Z4rBzc8DKCpO");

    public static NacosEnv DEFAULT = null;

    public static NacosEnv valueOf(String name) {
        NacosEnv env = ENV_MAP.get(StringUtils.upperCase(name));
        Validate.notNull(env, "unknown nacos env " + name);
        return env;
    }

    public static NacosEnv valueOf(CongtaNacosProperties properties) {
        String name = properties.getName();
        NacosEnv env = ENV_MAP.get(StringUtils.upperCase(name));
        if (env == null) {
            env = create(name, properties.getUri(),
                    properties.getNamespace(), properties.getKcSid(),
                    properties.getUsername(), properties.getPassword());
        }
        return env;
    }

}
