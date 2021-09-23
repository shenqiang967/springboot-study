package com.sq.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: TestController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/16 14:25   // 时间
 * @Version: 1.0     // 版本
 */
@RequestMapping("/test")
@RestController
@Api(value="test操作接口",tags={"test"})
public class TestController {
    @ApiOperation(value="获取用户详细信息", notes="根据用户的id来获取用户详细信息")
    //Integer 会导致页面校验不通过
    // @ApiImplicitParam(name = "userId", value = "用户ID", required = true,paramType = "query", dataType = "Integer")
    @ApiImplicitParam(name = "userId", value = "用户ID", required = true,paramType = "query", dataType = "Integer")
    @GetMapping(value="/findById")
    public HashMap findById(@RequestParam(value = "userId")Integer userId){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("userId",userId);
        return resultMap;
    }
}
