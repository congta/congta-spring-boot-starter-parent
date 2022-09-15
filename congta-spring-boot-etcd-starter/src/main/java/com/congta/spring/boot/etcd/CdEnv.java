package com.congta.spring.boot.etcd;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhfch on 2022/9/13.
 */
public class CdEnv {

    ETCDConfig config;

    private CdEnv(ETCDConfig config) {
        this.config = config;
    }

    private static final Map<String, CdEnv> ENV_MAP = new ConcurrentHashMap<>();

    public static CdEnv valueOf(String name) {
        name = StringUtils.lowerCase(name);
        return ENV_MAP.get(name);
    }

    public static CdEnv create(String name, String uri, String username, String password, String kcSid, String pathPrefix) {
        Validate.notBlank(name, "nacos env can not be blank");
        name = name.toUpperCase();
        Validate.isTrue(!ENV_MAP.containsKey(name), "nacos env is already defined");

        ETCDConfig etcdConfig = new ETCDConfig();
        etcdConfig.name = name;
        etcdConfig.uri = uri;
        etcdConfig.kcSid = kcSid;
        etcdConfig.username = username;
        etcdConfig.password = password;
        etcdConfig.pathPrefix = pathPrefix;

        CdEnv env = new CdEnv(etcdConfig);
        ENV_MAP.put(name, env);
        return env;
    }

    public static final CdEnv MTCN_INTER = create(
            "mtcn-inter",
            "http://172.20.160.1:2379",
            "AAAAAX6mPFbZu9_Lf5oFnFGdHcGBaKZx7xK4aJE",
            "AAAAAn744y3BEo9diId4WNS6FYFH7OAA5I_BRIPMhnQsGPXNSsg",
            "maptime-prod",
            "/mtcn");

    public static final CdEnv MTUS = create(
            "mtus",
            "http://8.142.79.224:2379",
            "AAAAAH6fcUt7VJA8VKfFig",
            "AAAAAH6-VFhcb6UgTIbRrchiBxRa",
            "maptime",
            "/mtus");

    public static final CdEnv MTCN = create(
            "mtcn",
            MTUS.config.uri, // different from MTCN_INTER
            MTCN_INTER.config.username,
            MTCN_INTER.config.password,
            MTCN_INTER.config.kcSid,
            MTCN_INTER.config.pathPrefix);
}
