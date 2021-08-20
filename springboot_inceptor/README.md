# springboot 拦截器

> 在实际开发中，总存在着这样的场景，比如拦截请求的ip地址，或者在所有的请求都返回相同的数据，如果每一个方法都写出相同数据固然可以实现，但是随着项目的变大，重复的代码会越来越多，所以在这种情况我们可以用拦截器来实现。

添加依赖

```xml
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

新建一个拦截器CommonInterceptor，继承HandlerInterceptorAdapter。给大家说一下，在继承HandlerInterceptorAdapter有三个拦截器是经常使用的：

+ preHandle在业务处理器处理请求之前被调用
+ postHandle在业务处理器处理请求执行完成后,生成视图之前执行
+ afterCompletion在DispatcherServlet完全处理完请求后被调用

```java

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
```

使用mvc的通用配置类将拦截器配置进去

```java
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
```

