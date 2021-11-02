package com.congta.spring.boot.mongodb.mapper;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

/**
 * 参照 org.springframework.data:spring-data-jpa:2.5.5
 * @author Fucheng
 * created in 2021/10/16
 */
public interface CrudRepository<T> {

    ObjectId save(T var1);

    default <S extends T> List<ObjectId> saveAll(Iterable<S> var1) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "saveAll is not supported");
    }

    boolean updateOne(String id, T model);

    boolean updateOne(ObjectId id, T model);

    T findOneAndUpdate(String id, T model);

    T findOneAndUpdate(ObjectId id, T model);

    Optional<T> findById(ObjectId var1);

    Optional<T> findById(String var1);

    default boolean existsById(ObjectId var1) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "existsById is not supported");
    }

    default boolean existsById(String var1) {
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "existsById is not supported");
    }

    List<T> findAll();

    List<T> findAllById(Iterable<ObjectId> var1);

    long count();

    boolean deleteById(ObjectId var1);

    boolean deleteById(String var1);

    void purgeById(ObjectId var1);

    void purgeById(String var1);
}
