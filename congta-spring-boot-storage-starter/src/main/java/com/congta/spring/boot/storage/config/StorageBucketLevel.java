package com.congta.spring.boot.storage.config;


/**
 * @author Fucheng
 * created in 2021/6/23
 */
public enum StorageBucketLevel {

    /**
     * 私有，必须通过接口生成临时加密链接
     */
    PRIVATE,

    /**
     * 公开，无原图保护
     */
    PUBLIC,

    /**
     * 受限制访问，不用生成加密链接，但必须使用 style 访问
     */
    LIMITED
    ;

}
