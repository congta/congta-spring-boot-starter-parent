package com.congta.spring.boot.web.security;

/**
 * Created by zhangfucheng on 2021/10/15.
 */
public enum UserRole {

    /**
     * user not login
     */
    GUEST(0),

    /**
     * normal user
     */
    USER(1),

    /**
     * administrator
     */
    ADMIN(2)
    ;

    int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static UserRole findByValue(int value) {
        for (UserRole r : values()) {
            if (r.value == value) {
                return r;
            }
        }
        return null;
    }
}
