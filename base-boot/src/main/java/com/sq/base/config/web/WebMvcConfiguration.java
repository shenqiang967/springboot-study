package com.sq.base.config.web;

import com.sq.base.config.web.interceptor.RequestHeaderInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: WebConvertConfiguration    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 10:28   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    RequestHeaderInterceptor requestHeaderInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //这里的拦截器prehandle的执行顺序按照加入时的顺序，postHandle是加入时的倒叙
        registry.addInterceptor(requestHeaderInterceptor);
        super.addInterceptors(registry);
    }

}
