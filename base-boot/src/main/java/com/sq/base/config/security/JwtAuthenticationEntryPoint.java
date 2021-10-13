package com.sq.base.config.security;

import com.sq.base.common.constants.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用户访问资源没有携带正确的token处理  // 类说明，在创建类时要填写
 * @ClassName: JwtAuthenticationEntryPoint    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 13:48   // 时间
 * @Version: 1.0     // 版本
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.error("用户访问资源没有携带正确的token");
        httpServletResponse.sendError(HttpStatus.ILLEGAL_TOKEN.getCode(),HttpStatus.ILLEGAL_TOKEN.getMsg());
    }
}
