package com.hc.ticket.framework.web;

import com.hc.ticket.module.tkt.config.TktProperties;
import com.hc.ticket.module.tkt.constants.ApiConstants;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.hc.ticket.framework.common.exception.ServiceExceptionUtil.exception;
import static com.hc.ticket.module.tkt.constants.ErrorCodeConstants.ADMIN_UNAUTHORIZED;

/**
 * 管理端简单 Token：请求头 X-Admin-Token
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    public static final String HEADER_ADMIN_TOKEN = "X-Admin-Token";

    @Resource
    private TktProperties tktProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!Boolean.TRUE.equals(tktProperties.getAdmin().getAuthEnabled())) {
            return true;
        }
        // 探活放行
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith(ApiConstants.ADMIN_API_PREFIX + "/ping")) {
            return true;
        }
        String token = request.getHeader(HEADER_ADMIN_TOKEN);
        String expected = tktProperties.getAdmin().getToken();
        if (!StringUtils.hasText(expected) || !expected.equals(token)) {
            throw exception(ADMIN_UNAUTHORIZED);
        }
        return true;
    }
}
