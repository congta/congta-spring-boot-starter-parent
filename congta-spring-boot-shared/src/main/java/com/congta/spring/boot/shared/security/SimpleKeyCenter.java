package com.congta.spring.boot.shared.security;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import com.google.common.collect.Iterators;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.Validate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * id = 0，密文结构： id~encryptedData
 * id > 0，密文结构： id~encrypt(jsonHeader~data)
 * @author Fucheng
 * created in 2021/2/18
 */
public class SimpleKeyCenter implements KeyCenter {

    private static final byte SPLITTER = '~';
    private static Logger log = LoggerFactory.getLogger(SimpleKeyCenter.class);

    private Map<Integer, Holder> holders = new HashMap<>();
    private Iterator<Holder> iterator;

    public SimpleKeyCenter(@Value("${app.kc.sid}") String sid) {
        File secretFile;
        if (SystemUtils.IS_OS_WINDOWS) {
            secretFile = new File(SystemUtils.USER_HOME + "/.congta/key/" + sid + ".sec");
        } else {
            secretFile = new File("/etc/conf/congta/key/" + sid + ".sec");
        }
        if (secretFile.exists()) {
            try {
                List<String> keys = FileUtils.readLines(secretFile, StandardCharsets.UTF_8);
                for (String keyStr : keys) {
                    if (!keyStr.contains("~")) {
                        continue;
                    }
                    String[] ki = keyStr.split("~");
                    Holder holder = new Holder();
                    holder.id = ki.length > 2 ? Integer.parseInt(ki[2]) : 0;
                    holder.key = Base64.decodeBase64(ki[0]);
                    holder.iv = Base64.decodeBase64(ki[1]);
                    if (holders.containsKey(holder.id)) {
                        log.warn("duplicate secret id: {}, use the first one", holder.id);
                        continue;
                    }
                    holders.put(holder.id, holder);
                    iterator = Iterators.cycle(holders.values());
                }

            } catch (IOException e) {
                throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "read key center secret error", e);
            }
        } else {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "key center secret not ready");
        }
    }

    @Override
    public byte[] encrypt(byte[] raw) {
        Holder holder = this.iterator.next();
        try {
            if (holder.id == 0) {
                return ArrayUtils.addAll(
                        ByteBuffer.allocate(5).putInt(holder.id).put(SPLITTER).array(),
                        createCipher(Cipher.ENCRYPT_MODE, holder.key, holder.iv).doFinal(raw)
                );
            } else if (holder.id > 0) {
                JSONObject header = new JSONObject();
                header.put("t", System.currentTimeMillis() / 1000);
                byte[] headerBytes = ArrayUtils.addAll(header.toString().getBytes(StandardCharsets.UTF_8), SPLITTER);
                return ArrayUtils.addAll(
                        ByteBuffer.allocate(5).putInt(holder.id).put(SPLITTER).array(),
                        createCipher(Cipher.ENCRYPT_MODE, holder.key, holder.iv).doFinal(ArrayUtils.addAll(
                                headerBytes,
                                raw
                        ))
                );
            }
        } catch (Exception e) {
            // 加密错误是系统错误
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "encrypt data error", e);
        }
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "data handle type not supported " + holder.id);
    }

    @Override
    public String encrypt(String raw) {
        return Base64.encodeBase64URLSafeString(encrypt(raw.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public byte[] decrypt(byte[] raw) {
        int idx = ArrayUtils.indexOf(raw, SPLITTER);
        int id = ByteBuffer.wrap(ArrayUtils.subarray(raw, 0, idx)).getInt();
        try {
            Holder holder = this.holders.get(id);
            raw = ArrayUtils.subarray(raw, idx + 1, raw.length);
            if (id == 0) {
                return createCipher(Cipher.DECRYPT_MODE, holder.key, holder.iv).doFinal(raw);
            } else if (id > 0) {
                raw = createCipher(Cipher.DECRYPT_MODE, holder.key, holder.iv).doFinal(raw);
                idx = ArrayUtils.indexOf(raw, SPLITTER);
                String headerStr = new String(ArrayUtils.subarray(raw, 0, idx), StandardCharsets.UTF_8);
                return ArrayUtils.subarray(raw, idx + 1, raw.length);
            }
        } catch (Exception e) {
            // 先按参数错误算，实际应该是有脏数据
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "decrypt data error", e);
        }
        throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "data handle type not supported " + id);
    }

    @Override
    public String decrypt(String raw) {
        return new String(decrypt(Base64.decodeBase64(raw)), StandardCharsets.UTF_8);
    }

    /**
     * 调用方保证ID不重复
     * @param id id < 0 不合法
     *           id == 0，不再支持创建这种密文，密文中仅包含密钥ID和密文
     *           id > 0，密文中除包含密钥ID和密文外，还可以包含时间戳等附加信息
     * @return 密钥串
     */
    public static Holder createKey(int id) {
        try {
            Validate.isTrue(id > 0);
            Holder holder = new Holder();
            KeyGenerator kGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = new SecureRandom();
            kGen.init(256, secureRandom);
            SecretKey key = kGen.generateKey();

            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);

            holder.id = id;
            holder.key = key.getEncoded();
            holder.iv = iv;

            return holder;
        } catch (NoSuchAlgorithmException e) {
            log.error("create key or iv error", e);
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "create key error");
        }
    }

    private Cipher createCipher(int mode, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

        cipher.init(mode, keySpec, ivSpec);
        return cipher;
    }

    public static class Holder {
        int id;
        byte[] key;
        byte[] iv;

        @Override
        public String toString() {
            return Base64.encodeBase64String(key) + "~" + Base64.encodeBase64String(iv) + "~" + id;
        }
    }
}
