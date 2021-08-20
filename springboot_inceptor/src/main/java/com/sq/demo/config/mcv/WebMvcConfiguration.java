package com.sq.demo.config.mcv;

import com.sq.demo.config.mcv.interceptor.CommonInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: WebMvcConfiguration    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 11:24   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    CommonInterceptor commonInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //这里的拦截器prehandle的执行顺序按照加入时的顺序，postHandle是加入时的倒叙
        registry.addInterceptor(commonInterceptor);
        super.addInterceptors(registry);
    }
}
