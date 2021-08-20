package com.sq.demo.business.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 16:58   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping(value = "error")
    public HashMap<String,Object> error(){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("error","123213");
        System.out.println(resultMap.toString());
        return resultMap;
    }
    @GetMapping(value = "exception")
    public HashMap<String,Object> exception(){
        HashMap<String, Object> resultMap = new HashMap<>();
        System.out.println(1/0);
        return resultMap;
    }
}
