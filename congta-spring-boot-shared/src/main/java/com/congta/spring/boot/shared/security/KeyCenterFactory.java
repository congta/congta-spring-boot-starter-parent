package com.congta.spring.boot.shared.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fucheng
 * created in 2021/10/16
 */
public class KeyCenterFactory {

    private static final Map<String, SimpleKeyCenter> INSTANCES = new ConcurrentHashMap<>();

    public static SimpleKeyCenter get(String serviceId) {
        return INSTANCES.computeIfAbsent(serviceId, SimpleKeyCenter::new);
    }

}
