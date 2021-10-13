package com.sq.base.aspect.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 重复提交token校验  // 类说明，在创建类时要填写
 * @ClassName: RepeatTokenService    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/13 15:25   // 时间
 * @Version: 1.0     // 版本
 */
public interface IRepeatTokenService {
    /**
     * 创建token
     * @return
     */
    public  String createToken();
    /**
     * 检验token
     * @param request
     * @return
     */
    public boolean checkToken(HttpServletRequest request) throws Exception;

}
