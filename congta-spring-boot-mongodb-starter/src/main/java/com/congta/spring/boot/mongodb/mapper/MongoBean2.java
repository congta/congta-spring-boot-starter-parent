package com.congta.spring.boot.mongodb.mapper;

import org.bson.types.ObjectId;

/**
 * 允许 id 字段不以下划线开头，需要加上 @SerializeName("_id") 修饰
 * @author Fucheng
 * created in 2021/10/21
 */
public interface MongoBean2 extends MongoBean {

    ObjectId getId();

    void setId(ObjectId _id);

    @Override
    default ObjectId get_id() {
        return getId();
    }

    @Override
    default void set_id(ObjectId _id) {
        setId(_id);
    }
}
