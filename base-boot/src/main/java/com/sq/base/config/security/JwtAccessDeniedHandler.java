package com.sq.base.config.security;

import com.sq.base.common.constants.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: jwt没有权限时返回的内容  // 类说明，在创建类时要填写
 * @ClassName: JwtAccessDeniedHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 13:35   // 时间
 * @Version: 1.0     // 版本
 */
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        log.error("用户访问没有授权资源:",e);
        httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.getCode(),HttpStatus.UNAUTHORIZED.getMsg());
    }
}
