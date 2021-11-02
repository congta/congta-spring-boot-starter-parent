package com.congta.spring.boot.mongodb.mapper;

import org.bson.types.ObjectId;

/**
 * @author Fucheng
 * created in 2021/10/21
 */
public interface MongoBean {

    ObjectId get_id();

    void set_id(ObjectId _id);

}
