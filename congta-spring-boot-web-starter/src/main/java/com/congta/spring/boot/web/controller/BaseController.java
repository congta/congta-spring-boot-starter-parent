package com.congta.spring.boot.web.controller;

import com.congta.spring.boot.shared.ex.OpCode;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

/**
 * @author Fucheng
 * created in 2021/1/23
 */
public class BaseController {

    protected ResultModel<String> genSuccessResult() {
        return ResultModel.success("");
    }

    protected ResultModel<String> genSuccessResult(String message) {
        return ResultModel.success(message);
    }

    protected ResultModel<JSONObject> genSuccessResult(JSONObject data) {
        return ResultModel.success(data);
    }

    protected ResultModel<Map<String, Object>> genSuccessResult(JSONArray array) {
        return ResultModel.success(ImmutableMap.of(
                "list", array
        ));
    }

    protected ResultModel<Object> genSuccessResult(Object data) {
        return ResultModel.success(data);
    }

    protected ResultModel<Map<String, Object>> genSuccessResult(Map<String, Object> data) {
        return ResultModel.success(data);
    }

    protected <T> ResultModel<Map<String, Object>> genSuccessResult(Collection<T> collection) {
        return ResultModel.success(ImmutableMap.of(
                "list", collection
        ));
    }

    protected <T> ResultModel<Map<String, Object>> genSuccessResult(T[] array) {
        return ResultModel.success(ImmutableMap.of(
                "list", array
        ));
    }

    protected ResultModel<Object> genFailureResult(String s) {
        return genFailureResult(OpCode.UNKNOWN_ERROR, s);
    }

    protected ResultModel<Object> genFailureResult(OpCode code, String s) {
        return ResultModel.failure(code, s);
    }
}

