package com.sq.base.config.security;

import com.alibaba.fastjson.JSON;
import com.sq.base.util.result.BaseResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 登录失败后的处理逻辑  // 类说明，在创建类时要填写
 * @ClassName: JwtAuthenticationFailureHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/27 10:54   // 时间
 * @Version: 1.0     // 版本
 */
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(BaseResult.fail(exception.getMessage(),500)));
    }
}
