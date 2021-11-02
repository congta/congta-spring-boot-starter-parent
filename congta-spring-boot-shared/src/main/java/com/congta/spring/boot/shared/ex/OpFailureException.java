package com.congta.spring.boot.shared.ex;

/**
 * Created by zhangfucheng on 2021/01/23.
 */
public class OpFailureException extends RuntimeException {

    /**
     * {@link OpCode}
     */
    private int code;

    /**
     * 异常信息是否已经打印到日志系统
     */
    private transient boolean printed;

    public OpFailureException() {
    }

    public OpFailureException(String message) {
        super(message);
    }

    public OpFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpFailureException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }

    @Override
    public String toString() {
        return "OpFailureException{" +
                "code=" + code +
                ", msg=" + super.toString() +
                '}';
    }
}
