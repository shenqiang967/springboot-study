package com.sq.base.config.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sq.base.common.exception.CustomException;
import com.sq.base.util.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 定义全局异常类  // 类说明，在创建类时要填写
 * @ClassName: GlobalExceptionHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 11:35   // 时间
 * @Version: 1.0     // 版本
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public BaseResult defaultHandler(HttpServletResponse response, CustomException e) throws JsonProcessingException {
        log.error(e.getMessage(), e);
        response.setContentType("application/json;charset=UTF-8");
        return BaseResult.fail(e.getMessage(),500);
    }


}
