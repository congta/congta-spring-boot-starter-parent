package com.congta.spring.boot.mongodb.mapper;

import com.congta.spring.boot.mongodb.converter.GSONExposeAdapter;
import com.congta.spring.boot.mongodb.converter.GSONObjectIdAdapter;
import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 个人觉得 mongodb 本身的 sdk 足够简单了，引入 spring-data-mongodb 必要性不大，还会带来额外的学习成本，
 * ORM 的工作由 GSON 来做
 * Created by zhangfucheng on 2021/6/25.
 */
public abstract class MongoMapper<T extends MongoBean> implements CrudRepository<T> {

    protected static final int LIMIT = 50;
    protected static final String F_ID = "_id";
    protected static final String F_STATUS = "status";

    @Autowired
    protected MongoDatabase mongoDatabase;

    protected String defaultCollectionName;
    protected Class<T> defaultClass;

    public MongoMapper(String defaultCollectionName) {
        this.defaultCollectionName = defaultCollectionName;

        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass)
                    .getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                defaultClass = (Class<T>) actualTypeArguments[0];
            }
        }
        Validate.notNull(defaultClass);
        gsonSnaked = supplyGsonBuilder(gsonBuilder).create();
    }

    protected GsonBuilder supplyGsonBuilder(GsonBuilder gsonBuilder) {
        return gsonBuilder;
    }

    protected MongoCollection<Document> getTable() {
        return getTable(defaultCollectionName);
    }

    protected MongoCollection<Document> getTable(String collectionName) {
        return mongoDatabase.getCollection(collectionName);
    }

    public static String toString(ObjectId value) {
        return Base64.encodeBase64URLSafeString(value.toByteArray());
    }

    public static ObjectId objectId(String id) {
        if (id.length() == 24) {
            return new ObjectId(id);
        } else if (id.length() == 16) {
            // short id
            return new ObjectId(Base64.decodeBase64(id));
        }
        throw ExceptionHelper.build(OpCode.PARAM_ERROR, "invalid id " + id, null, false);
    }

    protected static ObjectId objectId(InsertOneResult result) {
        if (result.getInsertedId() == null) {
            throw ExceptionHelper.build(OpCode.STORAGE_ERROR, "no id generated");
        }
        return result.getInsertedId().asObjectId().getValue();
    }

    // region Common DAO Method


    @Override
    public ObjectId save(T model) {
        ObjectId oid = model.get_id();
        if (oid != null) {
            updateOne(oid, model);
            return oid;
        }
        Document document = toDocument(model);
        oid = objectId(getTable().insertOne(document));
        model.set_id(oid);
        return oid;
    }

    @Override
    public boolean updateOne(String id, T model) {
        return updateOne(objectId(id), model);
    }

    @Override
    public boolean updateOne(ObjectId id, T model) {
        return updateOne(id, toDocument(model));
    }

    public boolean updateOne(String id, Document update) {
        return updateOne(objectId(id), update);
    }

    public boolean updateOne(ObjectId id, Document update) {
        update.remove(F_ID);
        return updateOne(
                Filters.eq(id),
                new BasicDBObject("$set", update)
        );
    }

    protected boolean updateOne(Bson filter, Bson update) {
        return getTable().updateOne(
                filter,
                update
        ).getMatchedCount() > 0;
    }

    @Override
    public T findOneAndUpdate(String id, T model) {
        return findOneAndUpdate(objectId(id), model);
    }

    public <S extends T> S findOneAndUpdate(String id, T model, Class<S> returnClass) {
        return findOneAndUpdate(objectId(id), model, returnClass);
    }

    @Override
    public T findOneAndUpdate(ObjectId id, T model) {
        return findOneAndUpdate(id, toDocument(model));
    }

    public <S extends T> S findOneAndUpdate(ObjectId id, T model, Class<S> returnClass) {
        return findOneAndUpdate(id, toDocument(model), returnClass);
    }

    public T findOneAndUpdate(String id, Document update) {
        return findOneAndUpdate(objectId(id), update);
    }

    public <S extends T> S findOneAndUpdate(String id, Document update, Class<S> returnClass) {
        return findOneAndUpdate(objectId(id), update, returnClass);
    }

    public T findOneAndUpdate(ObjectId id, Document update) {
        update.remove(F_ID);
        return findOneAndUpdate(
                Filters.eq(id),
                new BasicDBObject("$set", update)
        );
    }

    public <S extends T> S findOneAndUpdate(ObjectId id, Document update, Class<S> returnClass) {
        update.remove(F_ID);
        return findOneAndUpdate(
                Filters.eq(id),
                new BasicDBObject("$set", update),
                returnClass
        );
    }

    protected T findOneAndUpdate(Bson filter, Bson update) {
        return findOneAndUpdate(filter, update, defaultClass);
    }

    protected <S extends T> S findOneAndUpdate(Bson filter, Bson update, Class<S> returnClass) {
        return toCommonObject(getTable().findOneAndUpdate(
                filter,
                update,
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        ), returnClass);
    }

    @Override
    public Optional<T> findById(String id) {
        return findById(objectId(id));
    }

    @Override
    public Optional<T> findById(ObjectId id) {
        Document document = getTable().find(Filters.eq(id)).limit(1).first();
        return Optional.ofNullable(toObject(document));
    }

    @Override
    public List<T> findAll() {
        return toList(getTable().find().cursor());
    }

    @Override
    public List<T> findAllById(Iterable<ObjectId> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return toList(getTable().find(Filters.in(F_ID, ids)).cursor());
    }

    @Override
    public long count() {
        return getTable().countDocuments();
    }

    /**
     * 主要用于离线修复及数据统计，同步所有记录
     */
    public List<T> findByPage(String token, int limit) {
        FindIterable<Document> iterable;
        if (StringUtils.isNotBlank(token)) {
            iterable = getTable()
                    .find(Filters.gt(F_ID, new ObjectId(token)));
        } else {
            iterable = getTable()
                    .find(Filters.gt(F_ID, new ObjectId(new byte[12])));
        }
        MongoCursor<Document> cursor = iterable
                .sort(Sorts.ascending(F_ID))
                .limit(limit).cursor();
        return toList(cursor);
    }

    @Override
    public boolean deleteById(String id) {
        return deleteById(objectId(id));
    }

    @Override
    public boolean deleteById(ObjectId id) {
        return null != getTable().findOneAndUpdate(
                Filters.eq(id),
                Updates.set("status", 1)
        );
    }

    public boolean recover(String id) {
        return recover(objectId(id));
    }

    public boolean recover(ObjectId id) {
        return null != getTable().findOneAndUpdate(
                Filters.eq(id),
                Updates.set("status", 0)
        );
    }

    /**
     * test only
     */
    @Override
    public void purgeById(String id) {
        purgeById(objectId(id));
    }

    @Override
    public void purgeById(ObjectId id) {
        getTable().findOneAndDelete(Filters.eq(id));
    }

    // endregion

    // region ORM utils

    /**
     * 不要在这里删除 _id 字段，主记录不允许更新 _id，但子 array 里是可以的
     * @param t
     * @return
     */
    protected Document toDocument(Object t) {
        return Document.parse(toSnakeJson(t));
    }

    protected <T> BsonArray toDocument(List<T> list) {
        return BsonArray.parse(toSnakeJson(list));
    }

    protected T toObject(Document document) {
        return toCommonObject(document, defaultClass);
    }

    /**
     * 这个方法会受到子类覆写 toObject 的影响
     */
    protected List<T> toList(MongoCursor<Document> cursor) {
        List<T> list = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                list.add(toObject(cursor.next()));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    protected <O> O toCommonObject(Document document, Class<O> clazz) {
        if (document == null) {
            return null;
        }
        return fromSnakeJson(document.toJson(), clazz);
    }

    /**
     * 转化后的 list 是原始 list，子类重写 toCommonObject 无效
     */
    protected <O> List<O> toCommonList(MongoCursor<Document> cursor, Class<O> clazz) {
        List<O> list = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                list.add(toCommonObject(cursor.next(), clazz));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    protected List<Bson> buildUpdatesForEmbedded(String setKey, JSONObject json) {
        if (!setKey.endsWith(".")) {
            setKey += ".";
        }
        List<Bson> updates = new ArrayList<>();
        for (String k : IteratorUtils.asIterable(json.keys())) {
            Object value = json.get(k);
            if (value instanceof JSONObject) {
                value = BsonDocument.parse(value.toString());
            }
            if (value instanceof JSONArray) {
                value = BsonArray.parse(value.toString());
            }
            updates.add(Updates.set(setKey + k, value));
        }
        return updates;
    }

    // endregion

    private GsonBuilder gsonBuilder = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            // Expose 默认的行为，需要全部要序列化的字段都加上改注解才行，太二，所以自定义排除策略
            .addSerializationExclusionStrategy((GSONExposeAdapter) expose -> !expose.serialize())
            .addDeserializationExclusionStrategy((GSONExposeAdapter) expose -> !expose.deserialize())
            // 一些特殊字段的处理逻辑
            .registerTypeAdapter(ObjectId.class, new GSONObjectIdAdapter());

    private Gson gsonSnaked;

    public <S> S fromSnakeJson(String s, Class<S> tClass) {
        return gsonSnaked.fromJson(s, tClass);
    }

    public String toSnakeJson(Object object) {
        return gsonSnaked.toJson(object);
    }

}
