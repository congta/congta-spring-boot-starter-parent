package com.congta.spring.boot.mongodb.converter;

import com.congta.spring.boot.mongodb.mapper.MongoMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsonorg.PackageVersion;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.bson.types.ObjectId;

/**
 * 对于 objectId 转化为 base64 返回，清爽 1/4，也起一定混淆作用
 * Created by zhangfucheng on 2021/7/6.
 */
public class ObjectIdModule extends SimpleModule {

    private static final long serialVersionUID = 1L;
    private static final String NAME = "ObjectIdModule";

    public ObjectIdModule() {
        super(NAME, PackageVersion.VERSION);
        this.addKeyDeserializer(ObjectId.class, ObjectIdKeyDeserializer.instance);
        this.addDeserializer(ObjectId.class, ObjectIdDeserializer.instance);
        this.addKeySerializer(ObjectId.class, ObjectIdKeySerializer.instance);
        this.addSerializer(ObjectIdSerializer.instance);
    }

    static class ObjectIdSerializer extends StdSerializer<ObjectId> {

        public final static ObjectIdSerializer instance = new ObjectIdSerializer();

        public ObjectIdSerializer() { super(ObjectId.class); }

        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(Base64.encodeBase64URLSafeString(value.toByteArray()));
        }

    }

    static class ObjectIdKeySerializer extends StdSerializer<ObjectId> {

        public final static ObjectIdKeySerializer instance = new ObjectIdKeySerializer();

        public ObjectIdKeySerializer() { super(ObjectId.class); }

        @Override
        public void serialize(ObjectId value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeFieldName(Base64.encodeBase64URLSafeString(value.toByteArray()));
        }

    }

    static class ObjectIdDeserializer extends StdDeserializer<ObjectId> {

        public final static ObjectIdDeserializer instance = new ObjectIdDeserializer();

        public ObjectIdDeserializer() { super(ObjectId.class); }

        @Override
        public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String id = p.getValueAsString();
            return MongoMapper.objectId(id);
        }

    }

    static class ObjectIdKeyDeserializer extends KeyDeserializer {

        public final static ObjectIdKeyDeserializer instance = new ObjectIdKeyDeserializer();

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return MongoMapper.objectId(key);
        }

    }
}

