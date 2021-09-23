package com.sq.base.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: SwaggerConfiguration    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/16 14:04   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sq.base.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //标题
                .title("使用Swagger2构建RESTful APIs")
                //介绍
                .description("测试Swagger2")
                //服务条款
                .termsOfServiceUrl("https://www.baidu.com/")
                .contact("dalaoyang")
                .version("1.0")
                .build();
    }

}
