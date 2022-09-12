package com.congta.spring.boot.etcd;

import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangfucheng on 2021/11/5.
 */
public class ETCDConfig {

    private String name;

    private String uri;

    private String username;

    private String password;

    private String kcSid;

    private String pathPrefix;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKcSid() {
        return kcSid;
    }

    public void setKcSid(String kcSid) {
        this.kcSid = kcSid;
    }

    public String getPathPrefix() {
        return pathPrefix;
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    private static final Map<String, ETCDConfig> ENV_MAP = new ConcurrentHashMap<>();

    public static ETCDConfig create(String name, String uri, String username, String password, String kcSid, String pathPrefix) {
        Validate.notBlank(name, "nacos env can not be blank");
        name = name.toUpperCase();
        Validate.isTrue(!ENV_MAP.containsKey(name), "nacos env is already defined");

        ETCDConfig env = new ETCDConfig();
        env.name = name;
        env.uri = uri;
        env.kcSid = kcSid;
        env.username = username;
        env.password = password;
        env.pathPrefix = pathPrefix;
        ENV_MAP.put(name, env);
        return env;
    }

    public static final ETCDConfig MTCN = create(
            "mtcn",
            "http://172.20.160.1:2379",
            "AAAAAX6mPFbZu9_Lf5oFnFGdHcGBaKZx7xK4aJE",
            "AAAAAn744y3BEo9diId4WNS6FYFH7OAA5I_BRIPMhnQsGPXNSsg",
            "maptime-prod",
            "/mtcn");

    public static final ETCDConfig MTUS = create(
            "mtus",
            "http://8.142.79.224:2379",
            "AAAAAH6fcUt7VJA8VKfFig",
            "AAAAAH6-VFhcb6UgTIbRrchiBxRa",
            "maptime",
            "/mtus");

    public static final ETCDConfig MTCN_PREVIEW = create(
            "mtcn-preview",
            MTUS.uri, // different from MTCN
            MTCN.username,
            MTCN.password,
            MTCN.kcSid,
            MTCN.pathPrefix);

}
