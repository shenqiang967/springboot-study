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

## springboot整合redis

### 1.pom配置

```xml
<!--redis-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2.yaml配置

```yaml
spring:
  redis:
    host: www.nuomi.cloud
    port: 6379
    password:
    database: 0
    jedis:
      pool:
        min-idle: 0 # 连接池中的最小空闲连接
        max-idle: 8 # 连接池中的最大空闲连接
        max-active: 8 #连接池最大连接数（使用负值表示没有限制） # 连接池最大阻塞等待时间（使用负值表示没有限制）
        max-wait: -1ms # 连接池最大阻塞等待时间（使用负值表示没有限制）
    timeout: 12000ms # 连接超时时间（毫秒）
```

### 3.核心配置类

```java
package com.sq.base.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

@Configuration
@EnableCaching //开启缓存
public class RedisConfiguration extends CachingConfigurerSupport {
    @Bean
    public CacheManager cacheManager(RedisTemplate<?,?> redisTemplate) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig(this.getClass().getClassLoader());
        redisCacheConfiguration
                //设置缓存保存时间
                .entryTtl(Duration.ofMinutes(1))
                //设置缓存的name(key)前缀
                .prefixCacheNameWith("test:cache:")
                //.prefixKeysWith() 等同于prefixCacheNameWith，该方法被废弃了
        ;
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return new RedisCacheManager(redisCacheWriter,redisCacheConfiguration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);
        //key值使用Utf-8String
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        //使用自定义的fastJson2JsonRedisSerializer 也可以使用官方的fastjson序列化类
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        fastJson2JsonRedisSerializer.setObjectMapper(mapper);
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        return redisTemplate;
    }
}
```

> 注意一点如果要使用redis缓存的话一定要@EnableCaching 开启缓存

### 4.RedisTemplate增强类

```java
package com.sq.base.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: RedisService    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 11:15   // 时间
 * @Version: 1.0     // 版本
 */
@Service
public class RedisService {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value)
    {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit)
    {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout)
    {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key)
    {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList)
    {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key)
    {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet)
    {
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key)
    {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value)
    {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }

}
```

## 关于spring缓存那些事儿

```text
一开始写增删改查基本都是直接对库操作，接口调用量少的时候，性能问题几乎不存在，但是数据量级上升之后，这些增删改查接口的压力也会大大上升，甚至会出现慢查询的情况，出现较大的延迟。这时候机智的小伙伴会使用索引，没错，索引可以解决一部分查询造成的性能问题。那么如何才能进一步提升查询的性能呢？对于读多写少的表可以使用缓存，那么将大大减少读取数据库的次数。
```
### 一.Redis(缓存)常见的问题

1.缓存穿透
        缓存穿透指查询一个一定不存在的数据，由于缓存不命中就会从数据库查询，查不到数据则不写入缓存，这将导致这个不存在的数据都要到数据库查询，造成缓存穿透。这种情况其实就是因为数据库不存在的数据无法写入缓存，解决这个问题的方法，就是把这种数据库查不到值的情况也考虑进去。

解决方案：
缓存空值，将数据库查询不到的则作为空值存入，那么下次可以从缓存中获取key值是空值可以判断出数据库不存在这个值。
使用布隆过滤器，布隆过滤器能判断一个 key 一定不存在（不保证一定存在，因为布隆过滤器结构原因，不能删除，但是旧值可能被新值替换，而将旧值删除后它可能依旧判断其可能存在），在缓存的基础上，构建布隆过滤器数据结构，在布隆过滤器中存储对应的 key，如果存在，则说明 key 对应的值为空。
2.缓存击穿
       缓存击穿针对某些高频访问的key，当这个key失效的瞬间，大量的请求击穿了缓存，直接请求数据库。

解决方案:
设置二级缓存
设置高频key缓存永不过期
使用互斥锁，在执行过程中，如果缓存过期，那么先获取分布式锁，在执行从数据库中加载数据，如果找到数据就加入缓存，没有就继续该有的动作，在这个过程中能保证只有一个线程操作数据库，避免了对数据库的大量请求。
3.缓存雪崩
        缓存雪崩指缓存服务器重启、或者大量缓存集中在某一个时间段失效，这样失效的时候，会给后端系统带来很大压力，造成数据库的故障。

解决方法：
缓存高可用设计，Redis sentinel和Redis Cluster等
请求限流与服务熔断降级机制，限制服务请求次数，当服务不可用时快速熔断降级。
设置缓存过期时间一定的随机分布，避免集中在同一时间缓存失效，可以在设计时将时间定为一个固定值+随机值。
定时更新缓存策略，对于实时性要求不高的数据，定时进行更新。
4.分布式锁
    我们知道目前常见的分布式锁有基于zookeeper和基于redis的，基于zookeeper是一个持久节点下的临时顺序节点的抢占，是一个队列机制。而基于redis则是对某个redis缓存key的抢占。两者优缺点如下：





    可以看出，redis实现分布式锁需要设置超时时间，如果不设置超时时间会出现什么问题呢？如果获取锁之后在解锁过程中出现宕机，则会导致死锁现象。因此需要设置超时时间来避免死锁现象。在redis2.8版本之前获取锁及设置超时时间分为setnx和expire两个步骤完成，如果这两个步骤之间出现宕机现象，依然会存在死锁现象。因此，redis2.8版本做了修改，将setnx和expire两个作为一个方法实现，避免了宕机引起的死锁现象。
|           | 优点                                                         | 缺点                                                         |
| --------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| zookeeper | 1.有封装好的框架，容易实现2.有等待锁的队列，大大提升抢锁效率。 | 添加和删除节点性能较低                                       |
| redis     | Set和Del指令性能较高                                         | 1.实现复杂，需要考虑超时，原子性，误删等情形。2.没有等待锁的队列，只能在客户端自旋来等待，效率低下。 |

其中获取记录需要添加注解:@Cache(value="xxx",key="xxxx",unless="#result==null")

删除则用@CacheEvict(value="xxx",key="xxxxx")

更新则用 @CachePut(value="xxx",key="xxxx")

| @Cacheable  | 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存 |
| ----------- | -------------------------------------------------------- |
| @CacheEvict | 清空缓存                                                 |
| @CachePut   | 保证方法被调用，又希望结果被缓存。                       |

@Service
public class UserManageServiceImpl implements UserManageService{

    @Autowired
    UserManageMapper userManageMapper;

 


```java
/**
 * 根据用户id获取用户信息
 * @param userid
 * @return
 */
@Override
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
@Cacheable(value = "user",key = "#userid",unless = "#result==null")
public User findUser(int userid) {
    return userManageMapper.findByUserid(userid);
}
 
/**
 * 删除的时候需要把好友关系表中的关系也删除。
 * @param userid
 * @return
 */
@Override
@Transactional(propagation = Propagation.REQUIRED,readOnly = false)
@CacheEvict(value = "user",key = "#userid")
public int deleteUser(int userid) {
 
    int res2 = userManageMapper.deleteFriendByUserid(userid);
    int res3= userManageMapper.deleteFriendByFriendid(userid);
    int res1= userManageMapper.deleteByUserid(userid);
    return res1;
}
```



## springsecurity+jwt

pom

```xml
<!--spring security-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!--jwt 相关生成类-->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

ymal

```yaml
#暂无
```

核心配置类 SpringSecurityConfig 继承自 WebSecurityConfigurerAdapter

> ```
> @EnableWebSecurity 
> ```

```java

@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AuthenticationFailureHandler jwtAuthenticationFailureHandler;
    @Autowired
    AuthenticationSuccessHandler jwtAuthenticationSuccessHandler;


    /**
     * Todo configure(AuthenticationManagerBuilder)用于通过允许AuthenticationProvider容易地添加来建立认证机制。
     *      也就是说用来记录账号，密码，角色信息。下方代码不从数据库读取，直接手动赋予
     * @author sq
     * @param auth
     * @return void
     * @date 2021/10/14 14:10
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth
        //         .inMemoryAuthentication()
        //         .withUser("user")
        //         .password("password")
        //         .roles("USER")
        //         .and()
        //         .withUser("admin")
        //         .password("password")
        //         .roles("ADMIN","USER");
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * password 密码模式要使用此认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Todo configure(HttpSecurity)允许基于选择匹配在资源级配置基于网络的安全性。以下示例将以/ admin /开头的网址限制为具有ADMIN角色的用户，并声明任何其他网址需要成功验证。
     *  也就是对角色的权限——所能访问的路径做出限制
     * @desc
     * @author sq
     * @param http
     * @return void
     * @date 2021/10/14 14:09
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**","/").permitAll()
                //login 不拦截
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                //授权
                .and()
                .formLogin()
                .failureHandler(jwtAuthenticationFailureHandler)
                .successHandler(jwtAuthenticationSuccessHandler)
                .and()
                // 禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 使用自己定义的拦截机制，拦截jwt
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                //授权错误信息处理
                .exceptionHandling()
                //用户访问资源没有携带正确的token
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //用户访问没有授权资源
                .accessDeniedHandler(jwtAccessDeniedHandler)
        ;
    }
    /**
     * Todo configure(WebSecurity)用于影响全局安全性(配置资源，设置调试模式，通过实现自定义防火墙定义拒绝请求)的配置设置。
     * @desc 一般用于配置全局的某些通用事物，例如静态资源等
     * @author sq
     * @param web web控制器
     * @return void
     * @date 2021/10/14 11:30
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/repeat/*");
    }
    // @Bean
    // public PasswordEncoder getEncoder(){
    //     return new BCryptPasswordEncoder();
    // }

}
```

UserDetailsService 配置用户信息来源

```java

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        Example<SysUser> userExample = Example.of(sysUser);
        Optional<SysUser> userOptional = userDao.findOne(userExample);
        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(userOptional.get().getUserName()).password(userOptional.get().getPassword()).roles("admin").authorities("user:reg","user:resetPwd").build();
    }
    /**
     * User user = new User();
     *     user.setUsername("y");
     *     user.setAddress("sh");
     *     user.setPassword("admin");
     *     ExampleMatcher matcher = ExampleMatcher.matching()
     *             .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.startsWith())//模糊查询匹配开头，即{username}%
     *             .withMatcher("address" ,ExampleMatcher.GenericPropertyMatchers.contains())//全部模糊查询，即%{address}%
     *             .withIgnorePaths("password");//忽略字段，即不管password是什么值都不加入查询条件
     *     Example<User> example = Example.of(user ,matcher);
     *     List<User> list = userRepository.findAll(example);
     *     System.out.println(list);
     */
}
```

BaseTokenInfo  JWT信息存储实体

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseTokenInfo {
    private String userName;
    private HashMap<String,Object> extraMap;
    //小程序openId 存放到extraMap
    //private String openId;
    //小程序sessionKey 存放到extraMap
    //private String sessionKey;
    //过期时间
    private Long expiration;

}
```

自定义校验auth token 的过滤器 JwtAuthenticationFilter

```java
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        //如果请求头中没有Authorization信息则直接放行了
        if(tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)){
            chain.doFilter(request, response);
            return;
        }
        //如果请求头中有token,则进行解析，并且设置认证信息
        if(!JwtTokenUtils.isExpiration(tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX,""))){
            //设置上下文
            UsernamePasswordAuthenticationToken authentication = getAuthentication(tokenHeader);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        super.doFilterInternal(request, response, chain);
    }

    //获取用户信息
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader){
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        String username = JwtTokenUtils.getUserName(token);
        // 获得权限 添加到权限上去
        String role = JwtTokenUtils.getUserRole(token);
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;
            }
        });
        if(username != null){
            return new UsernamePasswordAuthenticationToken(username, null,roles);
        }
        return null;
    }


}
```

jwt生成工具类

```java
public class JwtTokenUtils {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SECRET = "jwtsecret";
    public static final String ISS = "echisan";

    private static final Long EXPIRATION = 60 * 60 * 3L; //过期时间3小时

    private static final String ROLE = "role";

    //创建token
    public static String createToken(BaseTokenInfo baseTokenInfo){
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setClaims(baseTokenInfo.getExtraMap())
                .setIssuer(ISS)
                //这里也可以直接传tokenInfo的json串
                .setSubject(baseTokenInfo.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + baseTokenInfo.getExpiration()))
                .compact();
    }

    //从token中获取用户名(此处的token是指去掉前缀之后的)
    public static String getUserName(String token){
        String username;
        try {
            username = getTokenBody(token).getSubject();
        } catch (    Exception e){
            username = null;
        }
        return username;
    }

    public static String getUserRole(String token){
        return (String) getTokenBody(token).get(ROLE);
    }

    private static Claims getTokenBody(String token){
        Claims claims = null;
        try{
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            e.printStackTrace();
        }
        return claims;
    }

    //是否已过期
    public static boolean isExpiration(String token){
        try{
            return getTokenBody(token).getExpiration().before(new Date());
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }
}
```

权限校验失败回调以及成功回调

```java
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(BaseResult.fail(exception.getMessage(),500)));
    }
}


@Component
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authenticationb
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        User user = (User) authentication.getPrincipal();

        HashMap<String, Object> extraMap = new HashMap<>();
        extraMap.put("permission",user.getAuthorities());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().print(JSON.toJSONString(BaseResult.success(JwtTokenUtils.createToken(BaseTokenInfo.builder().expiration(60*60*1000L).extraMap(extraMap).userName(user.getUsername()).build()))));
    }
}

```

jwt没有权限时返回的内容

```java
@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        log.error("用户访问没有授权资源:",e);
        httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.getCode(),HttpStatus.UNAUTHORIZED.getMsg());
    }
}
```

用户访问资源没有携带正确的token处理

```java
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.error("用户访问资源没有携带正确的token");
        httpServletResponse.sendError(HttpStatus.ILLEGAL_TOKEN.getCode(),HttpStatus.ILLEGAL_TOKEN.getMsg());
    }
}
```

## 整合elasticsearch

### 初步整合

pom

```xml
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>7.13.2</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
    <version>7.13.2</version>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-high-level-client</artifactId>
    <version>7.13.2</version>
    <exclusions>
        <exclusion>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

yaml

```yaml
elasticsearch:
  cluster-nodes:
    - www.nuomi.cloud:9200
```

ElasticsearchProperties

```java
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticsearchProperties {
    /**
     * 请求协议
     */
    private String schema = "http";

    /**
     * 集群名称
     */
    private String clusterName = "elasticsearch";

    /**
     * 集群节点
     */
    private List<String> clusterNodes = new ArrayList<>();

    /**
     * 连接超时时间(毫秒)
     */
    private Integer connectTimeout = 1000;

    /**
     * socket 超时时间
     */
    private Integer socketTimeout = 30000;

    /**
     * 连接请求超时时间
     */
    private Integer connectionRequestTimeout = 500;

    /**
     * 每个路由的最大连接数量
     */
    private Integer maxConnectPerRoute = 10;

    /**
     * 最大连接总数量
     */
    private Integer maxConnectTotal = 30;


    /**
     * 索引配置信息
     */
    private Index index = new Index();

    /**
     * 认证账户
     */
    private Account account = new Account();


    /**
     * 索引配置信息
     */
    @Data
    public static class Index {

        /**
         * 分片数量
         */
        private Integer numberOfShards = 3;

        /**
         * 副本数量
         */
        private Integer numberOfReplicas = 0;

    }

    /**
     * 认证账户
     */
    @Data
    public static class Account {

        /**
         * 认证用户
         */
        private String username;

        /**
         * 认证密码
         */
        private String password;

    }
}
```

核心配置类ElasticsearchConfig

```java
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {
    @Autowired
    private ElasticsearchProperties elasticsearchProperties;

    private List<HttpHost> httpHosts = new ArrayList<>();

    @Bean
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient() {
        List<String> clusterNodes = elasticsearchProperties.getClusterNodes();
        if (clusterNodes.isEmpty()) {
            throw new RuntimeException("集群节点不允许为空");
        }
        clusterNodes.forEach(node -> {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "Must defined");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), elasticsearchProperties.getSchema()));
            } catch (Exception e) {
                throw new IllegalStateException("Invalid ES nodes " + "property '" + node + "'", e);
            }
        });
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));
        return getRestHighLevelClient(builder, elasticsearchProperties);
    }

    /**
     * get restHistLevelClient
     * @return
     */
    private static RestHighLevelClient getRestHighLevelClient(RestClientBuilder builder, ElasticsearchProperties elasticsearchProperties) {
        // Callback used the default {@link RequestConfig} being set to the {@link CloseableHttpClient}
        builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(elasticsearchProperties.getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(elasticsearchProperties.getSocketTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(elasticsearchProperties.getConnectionRequestTimeout());
            return requestConfigBuilder;
        });
        // Callback used to customize the {@link CloseableHttpClient} instance used by a {@link RestClient} instance.
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(elasticsearchProperties.getMaxConnectTotal());
            httpClientBuilder.setMaxConnPerRoute(elasticsearchProperties.getMaxConnectPerRoute());
            return httpClientBuilder;
        });
        // Callback used the basic credential auth
        ElasticsearchProperties.Account account = elasticsearchProperties.getAccount();
        if (!StringUtils.isEmpty(account.getUsername()) && !StringUtils.isEmpty(account.getUsername())) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));
        }
        return new RestHighLevelClient(builder);
    }
}
```

操作接口IEsService

```java
public interface IEsService {

    /**
     * 创建索引库
     */
    void createIndexRequest(String index);

    /**
     * 删除索引库
     */
    void deleteIndexRequest(String index);

    /**
     * 更新索引文档
     */
    void updateRequest(String index, String id, Object object);

    /**
     * 新增索引文档
     */
    void insertRequest(String index, String id, Object object);

    /**
     * 删除索引文档
     */
    void deleteRequest(String index, String id);
}


@Slf4j
public class EsServiceImpl implements IEsService {
    @Autowired
    public RestHighLevelClient restHighLevelClient;

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Override
    public void createIndexRequest(String index) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index)
                .settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 0));
        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, COMMON_OPTIONS);
            log.info(" 所有节点确认响应 : {}", response.isAcknowledged());
            log.info(" 所有分片的复制未超时 :{}", response.isShardsAcknowledged());
        } catch (IOException e) {
            log.error("创建索引库【{}】失败", index, e);
        }
    }

    @Override
    public void deleteIndexRequest(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, COMMON_OPTIONS);
            System.out.println(response.isAcknowledged());
        } catch (IOException e) {
            log.error("删除索引库【{}】失败", index, e);
        }
    }

    @Override
    public void updateRequest(String index, String id, Object object) {
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("更新索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }
    }

    @Override
    public void insertRequest(String index, String id, Object object) {
        IndexRequest indexRequest = new IndexRequest(index).id(id).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("创建索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }

    }

    @Override
    public void deleteRequest(String index, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        try {
            restHighLevelClient.delete(deleteRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("删除索引文档 {" + index + "} 数据id {" + id + "} 失败", e);
        }
    }
}

```

测试索引

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lol implements Serializable {
    private Long id;
    /**
     * 英雄游戏名字
     */
    private String name;
    /**
     * 英雄名字
     */
    private String realName;
    /**
     * 英雄描述信息
     */
    private String desc;
}
```

测试索引操作实现类

```java
@Service
@Slf4j
public class LolServiceImpl extends EsServiceImpl {

    public void insertBach(String index, List<Lol> list) {
        if (list.isEmpty()) {
            log.warn("bach insert index but list is empty ...");
            return;
        }
        list.forEach((lol)->{
            super.insertRequest(index, lol.getId().toString(), lol);
        });
    }

    public List<Lol> searchList(String index) {
        SearchResponse searchResponse = search(index);
        SearchHit[] hits = searchResponse.getHits().getHits();
        List<Lol> lolList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Lol lol = BeanUtil.mapToBean(sourceAsMap, Lol.class, true);
            lolList.add(lol);
        });
        return lolList;
    }

    protected SearchResponse search(String index) {

        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //bool符合查询
        //BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
        //        .filter(QueryBuilders.matchQuery("name", "盖伦"))
        //        .must(QueryBuilders.matchQuery("desc", "部落"))
        //        .should(QueryBuilders.matchQuery("realName", "光辉"));

        //分页
        //searchSourceBuilder.from(1).size(2);
        // 排序
        //searchSourceBuilder.sort("", SortOrder.DESC);

        ////误拼写时的fuzzy模糊搜索方法 2表示允许的误差字符数
        //QueryBuilders.fuzzyQuery("title", "ceshi").fuzziness(Fuzziness.build("2"));
        searchRequest.source(searchSourceBuilder);
        System.out.println(searchSourceBuilder.toString());
        System.out.println(JSONUtil.parseObj(searchSourceBuilder.toString()).toStringPretty());
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResponse;
    }
}
```

测试类

```java
@SpringBootTest
@RunWith(SpringRunner.class)
class LolServiceImplTest {


    public static final String INDEX_NAME = "lol";

    @Autowired
    private LolServiceImpl lolService;

    @Test
    public void createIndex() {
        lolService.createIndexRequest(INDEX_NAME);
    }

    @Test
    public void deleteIndex() {
        lolService.deleteIndexRequest(INDEX_NAME);
    }


    @Test
    public void insertTest() {
        List<Lol> list = new ArrayList<>();
        list.add(Lol.builder().id(1L).name("德玛西亚之力").realName("盖伦").desc("作为一名自豪而高贵的勇士，盖伦将自己当做无畏先锋中的普通一员参与战斗。他既受到同袍手足的爱戴，也受到敌人对手的尊敬--尤其作为尊贵的冕卫家族的子嗣，他被委以重任，守卫德玛西亚的疆土和理想。他身披抵御魔法的重甲，手持阔剑，时刻准备着用正义的钢铁风暴在战场上正面迎战一切操纵魔法的狂人。").build());
        list.add(Lol.builder().id(2L).name("疾风剑豪").realName("亚索(快乐风男)").desc("亚索是一个百折不屈的艾欧尼亚人，也是一名身手敏捷的御风剑客。这位生性自负的年轻人，被误认为杀害长老的凶手--由于无法证明自己的清白，他出于自卫而杀死了自己的哥哥。虽然长老死亡的真相已然大白，亚索还是无法原谅自己的所作所为。他在家园的土地上流浪，只有疾风指引着他的剑刃。").build());
        list.add(Lol.builder().id(3L).name("魂锁典狱长").realName("锤石").desc("暴虐又狡猾的锤石是一个来自暗影岛的亡灵，野心勃勃、不知疲倦。他曾经是无数奥秘的看守，寻找着超越生死的力量，而现在他则使用自己独创的钻心痛苦缓慢地折磨并击溃其他人，以此作为自己存在下去的手段。被他迫害的人需要承受远超死亡的痛苦，因为锤石会让他们的灵魂也饱尝剧痛，将他们的灵魂囚禁在自己的灯笼中，经受永世的折磨。").build());
        list.add(Lol.builder().id(4L).name("圣枪游侠").realName("卢锡安").desc("曾担光明哨兵的卢锡安是一位冷酷的死灵猎人。他用一对圣物手枪无情地追猎并灭绝不死亡灵。为亡妻复仇的意念吞噬了他，让他无止无休。除非消灭锤石，那个手握她灵魂的恶鬼。冷酷而且决绝的卢锡安不允许任何东西挡住自己的复仇之路。如果有什么人或者什么东西愚蠢到敢挑衅他的原则，就必将接受压倒性的神圣枪火狂轰滥炸。").build());
        list.add(Lol.builder().id(5L).name("法外狂徒格雷福斯").realName("格雷福斯").desc("马尔科姆.格雷福斯是有名的佣兵、赌徒和窃贼，凡是他到过的城邦或帝国，都在通缉悬赏他的人头。虽然他脾气暴躁，但却非常讲究黑道的义气，他的双管散弹枪“命运”就经常用来纠正背信弃义之事。几年前他和老搭档崔斯特.菲特冰释前嫌，如今二人一同在比尔吉沃特的地下黑道纷争中再次如鱼得水。").build());
        list.add(Lol.builder().id(6L).name("光辉女郎").realName("拉克丝").desc("拉克珊娜.冕卫出身自德玛西亚，一个将魔法视为禁忌的封闭国度。只要一提起魔法，人们总是带着恐惧和怀疑。所以拥有折光之力的她，在童年的成长过程中始终担心被人发现进而遭到放逐，一直强迫自己隐瞒力量，以此保住家族的贵族地位。虽然如此，拉克丝的乐观和顽强让她学会拥抱自己独特的天赋，现在的她正在秘密地运用自己的能力为祖国效力。").build());
        list.add(Lol.builder().id(7L).name("发条魔灵").realName("奥莉安娜").desc("奥莉安娜曾是一个拥有血肉之躯的好奇女孩，而现在则是全身上下部由发条和齿轮构成的科技奇观。祖安下层地区的一次事故间接导致了她身染重病，日渐衰竭的身体必须替换为精密的人造器官，一个接一个，直到全身上下再也没有人类的肉体。她给自己制作了一枚奇妙的黄铜球体，既是伙伴，也是保镖。如今她已经可以自由地探索壮观的皮尔特沃夫，以及更遥远的地方。").build());

        lolService.insertBach(INDEX_NAME, list);
    }

    @Test
    public void updateTest() {
        Lol lol = Lol.builder().id(6L).name("殇之木乃伊").realName("阿木木").desc("或许阿木木是英雄联盟世界里最古老的保卫者英雄之一，他对加入联盟前的生活仍一无所知。阿木木唯一记得的是自己在Shuima沙漠的一座金字塔内独自醒来。他全身缠着裹尸布，感受不到自己的心跳。此外，他感到一股强大而莫名的悲伤；他知道他失去了亲人，虽然他已不记得他们是谁。阿木木跪下来，在绷带内哭泣。不论做什么，似乎他都无法阻止眼泪或悲伤。最后他站起来在这个世界上游荡，并进入了联盟").build();
        lolService.updateRequest(INDEX_NAME, lol.getId().toString(), lol);
    }

    @Test
    public void deleteTest() {
        lolService.deleteRequest(INDEX_NAME, "1");
    }

    /**
     * 测试查询
     */
    @Test
    public void searchListTest() {
        List<Lol> personList = lolService.searchList(INDEX_NAME);
        System.out.println(personList);
    }
}
```

### 全文检索

