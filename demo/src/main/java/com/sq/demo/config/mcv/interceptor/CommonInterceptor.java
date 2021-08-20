package com.sq.demo.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 通用拦截器  // 类说明，在创建类时要填写
 * @ClassName: CommonInterceptor    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 11:09   // 时间
 * @Version: 1.0     // 版本
 */
@Component
public class CommonInterceptor extends HandlerInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }
    /**
     * preHandle在业务处理器处理请求之前被调用，
     * postHandle在业务处理器处理请求执行完成后,生成视图之前执行
     * afterCompletion在DispatcherServlet完全处理完请求后被调用
    */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("请求ip："+request.getRemoteAddr());
        logger.info("请求的方法："+request.getMethod());
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

}
