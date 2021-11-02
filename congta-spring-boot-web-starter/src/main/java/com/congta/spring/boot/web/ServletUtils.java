package com.congta.spring.boot.web;

import com.congta.spring.boot.web.security.SessionContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangfucheng on 2021/10/15.
 */
public class ServletUtils {

    public static final String COOKIE_KEY_TOKEN = "Z_TOK";
    public static final String SESSION_KEY_SECRET = "secret";

    private static Random random = new Random();

    public static String getCookie(HttpServletRequest request, String name) {
        List<String> values = getCookieList(request, name);
        return CollectionUtils.isEmpty(values) ? null : values.get(0);
    }

    public static void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static List<String> getCookieList(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .collect(Collectors.toList());
    }

    public static String createSecret(SessionContext session) {
        byte[] data = new byte[9];
        random.nextBytes(data);
        String secret = Base64Utils.encodeToUrlSafeString(data);
        session.setSession(SESSION_KEY_SECRET, secret);
        return secret;
    }

    public static String getSecret(SessionContext session) {
        return session.getSessionAsString(SESSION_KEY_SECRET);
    }


    public static String getOrCreateSecret(SessionContext session) {
        String secret = getSecret(session);
        if (StringUtils.isNotBlank(secret)) {
            return secret;
        }
        return createSecret(session);
    }
}
