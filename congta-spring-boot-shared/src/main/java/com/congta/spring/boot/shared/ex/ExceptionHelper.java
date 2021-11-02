package com.congta.spring.boot.shared.ex;

import org.springframework.util.StringUtils;

/**
 * exception builder or thrower
 * @author Fucheng
 * created in 2021/1/23
 */
public class ExceptionHelper {

    public static OpFailureException silent(OpCode errorCode, String verbose) {
        return silent(errorCode, verbose, null);
    }

    public static OpFailureException silent(OpCode errorCode, String verbose, Throwable cause) {
        return build(errorCode, verbose, cause, true);
    }

    public static OpFailureException build(OpCode errorCode, String verbose) {
        return build(errorCode, verbose, null);
    }

    public static OpFailureException build(OpCode errorCode, String verbose, Throwable cause) {
        return build(errorCode, verbose, cause, false);
    }

    /**
     * @param silent 是否在后续逻辑中静默输出，如果为 true，后续逻辑不会再打印异常详情
     */
    public static OpFailureException build(OpCode errorCode, String verbose, Throwable cause, boolean silent) {
        if (errorCode == null) {
            errorCode = OpCode.SUCCESS;
        }
        if (!StringUtils.hasText(verbose)) {
            verbose = errorCode.getVerbose();
        }
        OpFailureException ex = new OpFailureException(verbose);
        ex.setCode(errorCode.getValue());
        if (cause != null) {
            ex.initCause(cause);
        }
        ex.setPrinted(silent);
        return ex;
    }
}
