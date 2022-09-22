package com.congta.spring.boot.shared.util;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author Fucheng
 * created in 2021/11/13
 */
public class YamlMapper {

    public static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    public static <T> T readValue(String data, Class<T> tClass) {
        try {
            return MAPPER.readValue(data, tClass);
        } catch (JsonProcessingException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "parse yaml error", e);
        }
    }
}
