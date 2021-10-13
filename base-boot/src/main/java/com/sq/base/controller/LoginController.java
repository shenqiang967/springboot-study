package com.sq.base.controller;

import com.sq.base.util.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: LoginController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/26 14:29   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@Slf4j
public class LoginController {
    // @RequestMapping("login")
    // public BaseResult login(String username,String password){
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     log.info(authentication.toString());
    //     BaseTokenInfo baseTokenInfo = new BaseTokenInfo().setUserName(username).setExpiration(60000L);
    //     String token = JwtTokenUtils.createToken(baseTokenInfo);
    //     System.out.println(token);
    //     return BaseResult.success(token);
    // }

    @RequestMapping("resetPwd")
    @PreAuthorize("hasRole('admin')")
    public BaseResult resetPwd(String username,String newPwd){
        return BaseResult.success(1111);
    }


    // public static void main(String[] args) {
    //     String token = JwtTokenUtils.createToken(new BaseTokenInfo().setUserName("张三").setExpiration(60000L));
    //     System.out.println(token);
    //     String userName = JwtTokenUtils.getUserName("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJlY2hpc2FuIiwiZXhwIjoxNjMyNzExMTQ5LCJpYXQiOjE2MzI3MTEwODl9.__ar69Kkyq9U3ujXoILCb4OfMucWrkgPOm-GTIbSjqBYvh8M0_Rf5_H4uw8J0gtMEVGVKi_XNDF4D-H52WVELQ");
    //     System.out.println(userName);
    // }
}
