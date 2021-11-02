package com.congta.spring.boot.mongodb.converter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.annotations.Expose;

/**
 * Created by zhangfucheng on 2021/7/5.
 */
public interface GSONExposeAdapter extends ExclusionStrategy {

    boolean shouldSkipField(Expose expose);

    @Override
    default boolean shouldSkipField(FieldAttributes f) {
        Expose expose = f.getAnnotation(Expose.class);
        return expose != null && shouldSkipField(expose);
    }

    @Override
    default boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
