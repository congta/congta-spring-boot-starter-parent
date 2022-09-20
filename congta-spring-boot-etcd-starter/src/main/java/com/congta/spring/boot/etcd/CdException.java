package com.congta.spring.boot.etcd;

public class CdException extends RuntimeException {

    /* do not be shown in toString() */
    private CdErrorCode code;

    public CdException() {
    }

    public CdException(String message) {
        super(message);
    }

    public CdException(String message, Throwable cause) {
        super(message, cause);
    }

    public CdException(Throwable cause) {
        super(cause);
    }

    public CdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CdException withCode(CdErrorCode code) {
        this.code = code;
        return this;
    }

    public CdErrorCode getCode() {
        return code;
    }
}
