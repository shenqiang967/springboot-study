package com.sq.base.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum HttpStatus {

    ILLEGAL_TOKEN(4001,"不合法的token"),
    ILLEGAL_REPEAT_TOKEN(4002,"不合法的RToken"),
    UNAUTHORIZED(4003,"用户访问没有授权资源");

    private int code;

    private String msg;

}
