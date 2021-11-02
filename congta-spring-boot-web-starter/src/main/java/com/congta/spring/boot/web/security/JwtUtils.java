package com.congta.spring.boot.web.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Fucheng
 * created in 2021/8/7
 */
public class JwtUtils {

    public static final String JWT_KEY_SESSION_ID = "sid";

    private static Logger log = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * 过期时间为30分钟后，密钥是userId加上一串字符串
     * @param jwt userId + sessionId
     * @return 加密后的 token
     */
    public static String createToken(JwtEntry jwt, String secret) {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR,24);
        Date expiresDate = nowTime.getTime();

        return JWT.create().withAudience(jwt.getUserId())
                .withExpiresAt(expiresDate)
                .withClaim(JWT_KEY_SESSION_ID, jwt.getSessionId())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 检验合法性
     * @param token 用户 token
     */
    public static boolean verifyToken(String token, String secret) {
        try {
            if (StringUtils.hasText(token) && StringUtils.hasText(secret)) {
                JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
                verifier.verify(token);
                return true;
            }
        } catch (Exception e) {
            log.warn("verify token error, token: {}, {}", token, String.valueOf(e));
        }
        return false;
    }

    /**
     * 获取签发对象
     */
    public static JwtEntry decode(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            DecodedJWT jwt = JWT.decode(token);
            String user = CollectionUtils.isEmpty(jwt.getAudience()) ? null : jwt.getAudience().get(0);
            JwtEntry entry = new JwtEntry(jwt.getClaim(JWT_KEY_SESSION_ID).asString());
            entry.setUserId(user);
            return entry;
        } catch (JWTDecodeException e) {
            log.warn("get audience error, token: {}", token, e);
            return null;
        }
    }

}
