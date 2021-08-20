package com.sq.demo.config.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * @Description: spring 内 定时任务  // 类说明，在创建类时要填写
 * @ClassName: SpringbootScheduled    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 10:46   // 时间
 * @Version: 1.0     // 版本
 */
@Component
public class SpringbootScheduled {
    @Scheduled(cron = "0/1 * * * * ?")
    private void test(){
        System.out.println("执行任务的时间："+ LocalDateTime.now());
    }
}
