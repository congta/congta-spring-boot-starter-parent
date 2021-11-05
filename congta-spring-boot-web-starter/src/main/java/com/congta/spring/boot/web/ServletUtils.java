package com.congta.spring.boot.web;

import com.congta.spring.boot.shared.ex.ExceptionHelper;
import com.congta.spring.boot.shared.ex.OpCode;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;

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

    public static void response(HttpServletResponse response, byte[] bytes, String contentType) {
        try {
            response.addHeader("Content-Length", "" + bytes.length);
            //永久缓存图片
            if ("image/jpg".equals(contentType) || "image/png".equals(contentType)) {
                response.setHeader("Cache-Control", "max-age=31536000, public");
            } else {
                response.setHeader("Cache-Control", "No-cache");
                response.setDateHeader("Expires", 0);
            }
            response.setContentType(contentType);

            IOUtils.write(bytes, response.getOutputStream());
        } catch (IOException e) {
            throw ExceptionHelper.build(OpCode.SYSTEM_ERROR, "write file to client error", e);
        }
    }

}
