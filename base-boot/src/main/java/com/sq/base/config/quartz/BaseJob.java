package com.sq.base.config.quartz;

import com.sq.base.util.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: BaseJob    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 15:16   // 时间
 * @Version: 1.0     // 版本
 */
@Slf4j
public class BaseJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("任务开始执行了");
        try {
            String targetStr = jobExecutionContext.getMergedJobDataMap().getString("targetStr");
            executeTask(targetStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("任务执行结束了");
    }

    private void executeTask(String targetStr) throws SchedulerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (StringUtils.isNotBlank(targetStr)){
            String[] targets = targetStr.split("\\.");
            if (targets.length>1&&SpringUtils.containsBean(targets[0])){
                Object bean = SpringUtils.getBean(targets[0]);
                Class<?> aClass = bean.getClass();
                Method method = aClass.getMethod(targets[1]);
                method.invoke(bean);
            }
        }else{
            log.info(String.valueOf(new Date().getTime()));
        }
    }

}
