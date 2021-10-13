package com.sq.base.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description: 校验是否重复提交
 * Todo 校验逻辑：当进入curd操作页面时，向服务器请求即时token，服务器生成后返回给客户端，
 * Todo 客户端在提交时request header携带上token，服务器验证token正确性后，通过并删除token，若redis中不存在则打回请求
 * @ClassName: AutoIdempotent
 * @Author: sq
 * @Date: 2021/10/13 15:22
 * @Version: 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoIdempotent {
}
