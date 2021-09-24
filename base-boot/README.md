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
    timeout: 0 # 连接超时时间（毫秒）
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



## springsecurity

