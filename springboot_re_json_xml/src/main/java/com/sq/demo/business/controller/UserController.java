package com.sq.demo.business.controller;

import com.sq.demo.business.domain.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @Description: 用户视图层  // 类说明，在创建类时要填写
 * @ClassName: UserController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 11:06   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping(value = "/xml/{userName}",produces = MediaType.APPLICATION_XML_VALUE)
    public User getXml(@PathVariable(value = "userName") String userName){
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(UUID.randomUUID().toString());
        user.setAge((int)(Math.random()*100));
        return user;
    }
    @GetMapping(value = "/json/{userName}",produces = MediaType.APPLICATION_JSON_VALUE)
    public User getJson(@PathVariable(value = "userName") String userName){
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(UUID.randomUUID().toString());
        user.setAge((int)(Math.random()*100));
        return user;
    }
}
