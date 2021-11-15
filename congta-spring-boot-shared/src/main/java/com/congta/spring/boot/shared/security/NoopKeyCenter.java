package com.congta.spring.boot.shared.security;

/**
 * @author Fucheng
 * created in 2021/2/18
 */
public class NoopKeyCenter implements KeyCenter {

    public static final NoopKeyCenter INSTANCE = new NoopKeyCenter();

    private NoopKeyCenter() {};

    @Override
    public byte[] encrypt(byte[] raw) {
        return raw;
    }

    @Override
    public String encrypt(String raw) {
        return raw;
    }

    @Override
    public byte[] decrypt(byte[] raw) {
        return raw;
    }

    @Override
    public String decrypt(String raw) {
        return raw;
    }

}
