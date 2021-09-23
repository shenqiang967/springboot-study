# base-boot

## springboot整合jpa

### 1.pom文件

```xml
<!--mysql-->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<!--jpa-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 2.yaml

```yaml
spring:
  datasource:
    url: jdbc:mysql://rm-2zezarc99437hpp1alo.mysql.rds.aliyuncs.com:3306/base-boot?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    hikari:
      username: baseboot
      password: Sq123456
  jpa:
    show-sql: true #默認false 开启sql展示
    database: mysql
    #create 启动时删数据库中的表，然后创建，退出时不删除数据表
    #create-drop 启动时删数据库中的表，然后创建，退出时删除数据表 如果表不存在报错
    #update 如果启动时表格式不一致则更新表，原有数据保留
    #validate 项目启动表结构进行校验 如果不一致则报错
    hibernate.ddl-auto: update
    properties.hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
    database-platform: org.hibernate.dialect.MySQL5Dialect
    hibernate:
      naming:
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl #指定jpa的自动表生成策略，驼峰自动映射为下划线格式

```

### 3.entity

```java
package com.sq.base.domain.task;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Description: 定时任务  // 类说明，在创建类时要填写
 * @ClassName: ScheduledTask    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 13:55   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@Entity
public class ScheduleJob  {
    /** 任务id */
    @Id
    @GeneratedValue
    private String jobId;
    /** 任务名称 */
    @Column(name = "job_name")
    private String jobName;
    /** 任务分组 */
    @Column(name = "job_group")
    private String jobGroup;
    /** 任务状态 0禁用 1启用 2删除*/
    @Column(name = "job_status")
    private String jobStatus;
    /** 任务运行时间表达式 */
    @Column(name = "cron_expression")
    private String cronExpression;
    /** 任务描述 */
    @Column(name = "desc")
    private String desc;
}

```

### 4.dao

```java
package com.sq.base.dao;

import com.sq.base.domain.task.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserDAO    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 13:46   // 时间
 * @Version: 1.0     // 版本
 */
@Repository
public interface JobDAO extends JpaRepository<ScheduleJob,Long> {

}
```

# springboot 整合 quartz动态定时任务

### 1.首先需要一个quartz的job实现类

```java
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

```

### 2.springutils源码

```java
package com.sq.base.util.spring;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring工具类 方便在非spring管理环境中获取bean
 * 
 * @author ruoyi
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware
{
    /** Spring应用上下文环境 */
    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        SpringUtils.applicationContext = applicationContext;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws org.springframework.beans.BeansException
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException
    {
        return (T) beanFactory.getBean(name);
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws org.springframework.beans.BeansException
     *
     */
    public static <T> T getBean(Class<T> clz) throws BeansException
    {
        T result = (T) beanFactory.getBean(clz);
        return result;
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name)
    {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
     *
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException
    {
        return beanFactory.getAliases(name);
    }

    /**
     * 获取aop代理对象
     * 
     * @param invoker
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker)
    {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles()
    {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getActiveProfile()
    {
        final String[] activeProfiles = getActiveProfiles();
        return activeProfiles.length>0 ? activeProfiles[0] : null;
    }
}

```

### 3.任务的操作实现类quartzmanager也可以事service名字都无所谓

```java
package com.sq.base.config.quartz;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: QuartzManager    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 15:42   // 时间
 * @Version: 1.0     // 版本
 */
@Component
public class QuartzManager {

        public static final String JOB1="job1";
        public static final String GROUP1="group1";
        /**默认每个星期凌晨一点执行*/
        //public static final String DEFAULT_CRON="0 0 1 ? * L";
        /**默认5秒执行一次*/
        public static final String DEFAULT_CRON="*/5 * * * * ?";

        /**
         * 任务调度
         */
        @Autowired
        private Scheduler scheduler;

        /**
         * 开始执行定时任务
         */
        public void startJob() throws SchedulerException {
            startJobTask(scheduler);
            scheduler.start();
        }

        /**
         * 启动定时任务
         * @param scheduler
         */
        private void startJobTask(Scheduler scheduler) throws SchedulerException {
            JobDetail jobDetail= JobBuilder.newJob(BaseJob.class).withIdentity(JOB1,GROUP1).usingJobData("targetStr","jobService.say").build();
            CronScheduleBuilder cronScheduleBuilder=CronScheduleBuilder.cronSchedule(DEFAULT_CRON);
            CronTrigger cronTrigger=TriggerBuilder.newTrigger().withIdentity(JOB1,GROUP1)
                    .withSchedule(cronScheduleBuilder).build();
            scheduler.scheduleJob(jobDetail,cronTrigger);

        }
        /**
         * 获取Job信息
         * @param name
         * @param group
         */
        public String getjobInfo(String name,String group) throws SchedulerException {
            TriggerKey triggerKey=new TriggerKey(name,group);
            CronTrigger cronTrigger= (CronTrigger) scheduler.getTrigger(triggerKey);
            return String.format("time:%s,state:%s",cronTrigger.getCronExpression(),
                    scheduler.getTriggerState(triggerKey).name());
        }

        /**
         * 修改任务的执行时间
         * @param name
         * @param group
         * @param cron cron表达式
         * @return
         * @throws SchedulerException
         */
        public boolean modifyJob(String name,String group,String cron) throws SchedulerException{
            Date date=null;
            TriggerKey triggerKey=new TriggerKey(name, group);
            CronTrigger cronTrigger= (CronTrigger) scheduler.getTrigger(triggerKey);
            String oldTime=cronTrigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)){
                CronScheduleBuilder cronScheduleBuilder=CronScheduleBuilder.cronSchedule(cron);
                CronTrigger trigger=TriggerBuilder.newTrigger().withIdentity(name,group)
                        .withSchedule(cronScheduleBuilder).build();
                date=scheduler.rescheduleJob(triggerKey,trigger);
            }
            return date !=null;
        }

        /**
         * 暂停所有任务
         * @throws SchedulerException
         */
        public void pauseAllJob()throws SchedulerException{
            scheduler.pauseAll();
        }

        /**
         * 暂停某个任务
         * @param name
         * @param group
         * @throws SchedulerException
         */
        public void pauseJob(String name,String group)throws SchedulerException{
            JobKey jobKey=new JobKey(name,group);
            JobDetail jobDetail=scheduler.getJobDetail(jobKey);
            if (jobDetail==null)
                return;
            scheduler.pauseJob(jobKey);
        }

        /**
         * 恢复所有任务
         * @throws SchedulerException
         */
        public void resumeAllJob()throws SchedulerException{
            scheduler.resumeAll();
        }
        /**
         * 恢复某个任务
         */
        public void resumeJob(String name,String group)throws SchedulerException{
            JobKey jobKey=new JobKey(name,group);
            JobDetail jobDetail=scheduler.getJobDetail(jobKey);
            if (jobDetail==null)
                return;
            scheduler.resumeJob(jobKey);
        }

        /**
         * 删除某个任务
         * @param name
         * @param group
         * @throws SchedulerException
         */
        public void deleteJob(String name,String group)throws SchedulerException {
            JobKey jobKey=new JobKey(name, group);
            JobDetail jobDetail=scheduler.getJobDetail(jobKey);
            if (jobDetail==null)
                return;
            scheduler.deleteJob(jobKey);
        }


}
```

### 4.springboot上下文监听器在其启动后执行quartz任务

```java
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
        try {
            quartzManager.startJob();
            log.info("任务已经启动......");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

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
```

### 5.任务表结构sql

```sql
CREATE TABLE `schedule_job` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(128) NOT NULL COMMENT '任务名称',
  `job_group` varchar(128) DEFAULT NULL COMMENT '任务分组',
  `job_status` varchar(128) NOT NULL COMMENT '任务状态 0禁用 1启用 2删除',
  `cron_expression` int(2) NOT NULL DEFAULT '1' COMMENT '任务运行时间表达式',
  `desc` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务描述',
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `uniqu_job_group` (`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

```

> 值得注意的是，在这里我的动态定时任务并没有写完，在这里我需要在表结构中加入一项执行字符串，在manger类中startJob的时候从数据库中查出所有的启用的任务将其执行并传入tartgetStr也就是目标执行字符串，通过targetStr中的记录执行具体的某一bean中的方法，当然这种执行也是简单和粗暴的，后期值得代码重构以及优化

## springsecurity

