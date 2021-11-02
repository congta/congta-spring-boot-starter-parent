package com.congta.spring.boot.web.controller;

import com.congta.spring.boot.shared.ex.OpCode;

/**
 * @author Fucheng
 * created in 2021/1/23
 */
public class ResultModel<T> {

    private int code;
    private String message;
    private long next;
    private T data;

    public static <T> ResultModel<T> success(T data) {
        return success(data, "");
    }

    public static <T> ResultModel<T> success(T data, String message) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.code = 0;
        resultModel.message = message;
        resultModel.data = data;
        resultModel.next = -1;
        return resultModel;
    }

    public static <T> ResultModel<T> failure(OpCode errorCode) {
        return failure(errorCode.getValue(), errorCode.getVerbose());
    }

    public static <T> ResultModel<T> failure(OpCode errorCode, String message) {
        return failure(errorCode.getValue(), message);
    }

    public static <T> ResultModel<T> failure(int code, String message) {
        return failure(code, message, 0);
    }

    public static <T> ResultModel<T> failure(int code, String message, long next) {
        ResultModel<T> resultModel = new ResultModel<>();
        resultModel.code = code;
        resultModel.message = message;
        resultModel.next = next;
        return resultModel;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getNext() {
        return next;
    }

    public void setNext(long next) {
        this.next = next;
    }
}
