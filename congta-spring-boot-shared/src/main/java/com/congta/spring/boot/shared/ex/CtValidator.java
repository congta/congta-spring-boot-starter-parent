package com.congta.spring.boot.shared.ex;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by zhangfucheng on 2021/6/30.
 */
public class CtValidator {

    public static void exist(Object obj, String msg) {
        if (obj == null) {
            throw ExceptionHelper.build(OpCode.ITEM_NOT_EXIST, msg);
        }
    }

    public static void arg(boolean expression, String msg) {
        if (!expression) {
            throw ExceptionHelper.build(OpCode.PARAM_ERROR, msg);
        }
    }

    public static void arg(boolean expression, OpCode errorCode, String msg) {
        if (!expression) {
            throw ExceptionHelper.build(errorCode, msg);
        }
    }

    public static void argEquals(Object expect, Object value, OpCode errorCode, String msg) {
        if (!Objects.equals(expect, value)) {
            throw ExceptionHelper.build(errorCode, msg);
        }
    }

    public static <T> T notNull(T obj, String msg) {
        if (obj == null) {
            throw ExceptionHelper.build(OpCode.PARAM_ERROR, msg);
        }
        return obj;
    }

    public static void state(boolean expression, String msg) {
        if (!expression) {
            throw ExceptionHelper.build(OpCode.UNKNOWN_ERROR, msg);
        }
    }

    public static void stateNotNull(Object obj, String msg) {
        if (obj == null) {
            throw ExceptionHelper.build(OpCode.UNKNOWN_ERROR, msg);
        }
    }

    public static void isLogin(String userId, String msg) {
        if (!StringUtils.hasText(userId)) {
            throw ExceptionHelper.build(OpCode.PERMISSION_DENIED, msg);
        }
    }

    public static void hasPermission(String userId, String expected, String msg) {
        if (!Objects.equals(expected, userId)) {
            throw ExceptionHelper.build(OpCode.PERMISSION_DENIED, msg);
        }
    }

    public static void hasPermission(String userId, Collection<String> collection, String msg) {
        if (notContains(collection, userId)) {
            throw ExceptionHelper.build(OpCode.PERMISSION_DENIED, msg);
        }
    }

    public static void hasPermission(String userId, Collection<String> collection1,
                                     Collection<String> collection2, String msg) {
        if (notContains(collection1, userId) && notContains(collection2, userId)) {
            throw ExceptionHelper.build(OpCode.PERMISSION_DENIED, msg);
        }
    }

    private static <T> boolean notContains(Collection<T> collection, T target) {
        return collection == null || !collection.contains(target);
    }

}
