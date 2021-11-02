package com.congta.spring.boot.mongodb.mapper;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.stereotype.Service;

/**
 * 目前项目没有用 mySql，使用mongo来生成自增主键来作为userId
 * Created by zhangfucheng on 2021/6/21.
 */
@Service
public class IdGenerator extends MongoMapper<EmptyMongoBean> {

    private static final String F_KEY = "k";
    private static final String F_SEQ = "s";

    private static final int START_ID = 100;

    // 如果对应集合还没有自增纪录，则创建一个初始值
    private volatile LoadingCache<String, Boolean> loadedCollections = CacheBuilder.newBuilder()
            .maximumSize(10000).build(new CacheLoader<String, Boolean>() {
                @Override
                public Boolean load(String key) {
                    String ns = StringUtils.substringBefore(key, ":");
                    FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
                    options.upsert(true);
                    getTable().findOneAndUpdate(
                            Filters.eq(F_KEY, key),
                            Updates.setOnInsert(F_SEQ, initialIds.getOrDefault(ns, START_ID)),
                            options
                    );
                    return true;
                }
            });

    // 保留一些特别小的数字，显得好看点
    private final Map<String, Integer> initialIds = new HashMap<>();

    public IdGenerator() {
        super("id_worker");
    }

    public IdGenerator addInitialId(String name, int initId) {
        initialIds.put(name, initId);
        return this;
    }

    /**
     * 获取下一个自增ID
     *
     * @param collName 集合（这里用类名，就唯一性来说最好还是存放长类名）名称
     * @return 序列值
     */
    public int getNextId(String collName) {
        try {
            loadedCollections.get(collName);
        } catch (ExecutionException e) {
            throw ExceptionHelper.build(OpCode.UNKNOWN_ERROR, "create id error", e);
        }
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        options.returnDocument(ReturnDocument.AFTER);
        Document newDocument = getTable().findOneAndUpdate(
                Filters.eq(F_KEY, collName),
                Updates.inc(F_SEQ, 1),
                options);
        if (newDocument == null) {
            throw ExceptionHelper.build(OpCode.UNKNOWN_ERROR, "create id error");
        }
        return newDocument.getInteger(F_SEQ);
    }


}
