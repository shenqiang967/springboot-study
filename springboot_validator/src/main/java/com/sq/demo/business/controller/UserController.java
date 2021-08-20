package com.sq.demo.business.controller;

import com.sq.demo.business.domain.UserDo;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 13:07   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping(value = "/addUser" , produces = MediaType.APPLICATION_JSON_VALUE)
    public String add(@Valid UserDo user, BindingResult bindingResult){
        StringBuilder msgBuilder = new StringBuilder();
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(err->{
                msgBuilder.append(err.getDefaultMessage()).append("\n");
            });
        }
        return msgBuilder.toString();
    }
}
