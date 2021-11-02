package com.congta.spring.boot.mongodb.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import org.bson.types.ObjectId;

/**
 * Created by zhangfucheng on 2021/7/22.
 */
public class GSONObjectIdAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
    }

    @Override
    public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        } else {
            JsonObject jo = new JsonObject();
            jo.addProperty("$oid", src.toString());
            return jo;
        }
    }
}
