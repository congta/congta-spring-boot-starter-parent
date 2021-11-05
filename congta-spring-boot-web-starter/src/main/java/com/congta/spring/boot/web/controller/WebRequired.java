package com.congta.spring.boot.web.controller;

import com.congta.spring.boot.web.ServletUtils;
import com.congta.spring.boot.shared.ex.CtValidator;
import com.congta.spring.boot.web.security.JwtEntry;
import com.congta.spring.boot.web.security.JwtUtils;
import com.congta.spring.boot.web.security.SessionContext;
import com.congta.spring.boot.web.security.SessionId;
import com.congta.spring.boot.web.security.SessionMeta;
import com.congta.spring.boot.web.security.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fucheng
 * created in 2021/2/18
 */
@Component
public class WebRequired implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(WebRequired.class);

    @Autowired
    private SessionContext session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = ServletUtils.getCookie(request, ServletUtils.COOKIE_KEY_TOKEN);
        log.debug("request session: {}, uri: {}", token, request.getRequestURI());
        JwtEntry jwt = JwtUtils.decode(token);
        if (jwt == null || !StringUtils.hasText(jwt.getSessionId())) {
            jwt = new JwtEntry(new SessionId().toBase64String());
        }
        SessionMeta ctx = session.create(jwt.getSessionId());

        String secret = JwtUtils.getSecret(session);
        if (JwtUtils.verifyToken(token, secret)) {
            ctx.setUserId(jwt.getUserId());
        } else {
            jwt.setUserId(null);
            if (secret == null) {
                secret = JwtUtils.createSecret(session);
            }
            token = JwtUtils.createToken(jwt, secret);
            ServletUtils.addCookie(response, ServletUtils.COOKIE_KEY_TOKEN, token);
        }

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Role role = handlerMethod.getBeanType().getAnnotation(Role.class);
            if (handlerMethod.hasMethodAnnotation(Role.class)) {
                role = handlerMethod.getMethodAnnotation(Role.class);
            }
            if (role != null) {
                UserRole userRole = UserRole.findByValue(role.value());

                if (userRole == UserRole.USER) {
                    CtValidator.isLogin(ctx.getUserId(), "必须先登录");
                }
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        session.clear();
    }
}
