package com.sq.demo.config.error;

import com.sq.demo.config.result.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @Description: 对视图层切面，异常统一处理// 类说明，在创建类时要填写
 * @ClassName: CommonExceptionHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 9:46   // 时间
 * @Version: 1.0     // 版本
 */

@ControllerAdvice
public class CommonExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);
    //拦截未授权页面
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    //这里简单的捕获一下异常超类，真正业务逻辑定义一个自定义的运行时异常捕获
    @ExceptionHandler(Exception.class)
    //以json方式返回出去
    @ResponseBody
    public R handleUnauthorizedException(Exception e) {
        logger.error(e.getMessage(),e);
        return R.fail(e.getMessage());
    }

}
