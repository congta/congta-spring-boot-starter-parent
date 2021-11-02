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
    String kcSid;
    String username;
    String password;

    private NacosEnv(String name, String uri, String kcSid, String username, String password) {
        this.name = name;
        this.uri = uri;
        this.kcSid = kcSid;
        this.username = username;
        this.password = password;
    }

    public static NacosEnv create(String name, String url, String kcSid, String username, String password) {
        Validate.notBlank(name, "nacos env can not be blank");
        Validate.isTrue(!ENV_MAP.containsKey(name), "nacos env is already defined");
        NacosEnv env = new NacosEnv(name, url, kcSid, username, password);
        ENV_MAP.put(name.toUpperCase(), env);
        return env;
    }

    private static final String CN_URL = "http://cn.nacos.congta.com:8848";

    private static final Map<String, NacosEnv> ENV_MAP = new ConcurrentHashMap<>();

    public static final NacosEnv PUBLIC = create(
            "public", CN_URL, "", "", "");

    public static final NacosEnv MTCN = create(
            "mtcn", CN_URL,
            "maptime-prod",
            "AAAAAX6mPFbZu9_Lep0Lm1GUHcCBaKZk_AiOfQ",
            "AAAAAn744y3BEo9djYB2X9SzFYdH7PlhsJrnDLnTv0sdBLffYcY");

    public static final NacosEnv MTUS = create(
            "mtus", CN_URL, "maptime",
            "AAAAAH6fZE58YoU",
            "AAAAAH6kIkN1XIc-Z4rBzc8DKCpO");

    public static NacosEnv DEFAULT = PUBLIC;

    public static NacosEnv valueOf(String name) {
        NacosEnv env = ENV_MAP.get(StringUtils.upperCase(name));
        Validate.notNull(env, "unknown nacos env " + name);
        return env;
    }

    public static NacosEnv valueOf(CongtaNacosProperties properties) {
        String name = properties.getName();
        NacosEnv env = ENV_MAP.get(StringUtils.upperCase(name));
        if (env == null) {
            env = create(name, properties.getUri(), properties.getKcSid(),
                    properties.getUsername(), properties.getPassword());
        }
        return env;
    }

}
