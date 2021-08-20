package com.sq.demo;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Description: springboot初始化入口  // 类说明，在创建类时要填写
 * @ClassName: DemoInitializer    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 10:15   // 时间
 * @Version: 1.0     // 版本
 */
public class DemoInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringbootJarToWarApplication.class);
    }

}
