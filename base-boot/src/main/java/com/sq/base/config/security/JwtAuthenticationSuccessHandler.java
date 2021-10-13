package com.sq.base.config.security;

import com.alibaba.fastjson.JSON;
import com.sq.base.util.result.BaseResult;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @Description: 登录成功处理函数  // 类说明，在创建类时要填写
 * @ClassName: JwtAuthenticationSuccessHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/27 10:59   // 时间
 * @Version: 1.0     // 版本
 */
@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authenticationb
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        User user = (User) authentication.getPrincipal();

        HashMap<String, Object> extraMap = new HashMap<>();
        extraMap.put("permission",user.getAuthorities());
        response.getWriter().print(JSON.toJSONString(BaseResult.success(JwtTokenUtils.createToken(BaseTokenInfo.builder().expiration(60000L).extraMap(extraMap).userName(user.getUsername()).build()))));
    }
}
