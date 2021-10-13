package com.sq.base.config.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: ApplicationStartQuartzJobListener    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 15:47   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
@Slf4j
public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private QuartzManager quartzManager;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("任务已经启动......");
        // try {
        //     quartzManager.startJob();
        //     log.info("任务已经启动......");
        // } catch (SchedulerException e) {
        //     e.printStackTrace();
        // }

    }
    /**
     * 初始注入scheduler
     */
    @Bean
    public Scheduler scheduler() throws SchedulerException{
        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
        return schedulerFactoryBean.getScheduler();
    }

}
