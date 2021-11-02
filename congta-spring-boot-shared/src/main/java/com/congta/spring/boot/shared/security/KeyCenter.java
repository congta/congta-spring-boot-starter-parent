package com.congta.spring.boot.shared.security;

/**
 * @author Fucheng
 * created in 2021/2/18
 */
public interface KeyCenter {

    byte[] encrypt(byte[] raw);

    String encrypt(String raw);

    byte[] decrypt(byte[] raw);

    String decrypt(String raw);
}
