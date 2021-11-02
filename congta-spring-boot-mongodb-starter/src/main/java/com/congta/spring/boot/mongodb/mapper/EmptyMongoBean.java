package com.congta.spring.boot.mongodb.mapper;

import org.bson.types.ObjectId;

/**
 * @author Fucheng
 * created in 2021/10/21
 */
public class EmptyMongoBean implements MongoBean {

    @Override
    public ObjectId get_id() {
        return null;
    }

    @Override
    public void set_id(ObjectId _id) {

    }
}
