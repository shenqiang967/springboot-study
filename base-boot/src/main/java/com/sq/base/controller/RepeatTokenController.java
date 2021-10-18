package com.sq.base.controller;

import com.sq.base.aspect.AutoIdempotent;
import com.sq.base.aspect.service.impl.RepeatTokenService;
import com.sq.base.domain.baseuser.BaseUser;
import com.sq.base.util.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 分发重复校验的token  // 类说明，在创建类时要填写
 * @ClassName: RepeatTokenController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/13 15:58   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("repeat")
@Slf4j
public class RepeatTokenController {
    @Autowired
    private RepeatTokenService repeatTokenService;
    @GetMapping("dispatch")
    public BaseResult dispatch(){
        return BaseResult.success(repeatTokenService.createToken());
    }
    //测试重复提交
    @PutMapping("test")
    @AutoIdempotent
    public void test(BaseUser baseUser){
        log.info(baseUser.toString());
    }
}
