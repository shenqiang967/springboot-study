# 定时任务

springboot整合定时任务其实就两点：

+ 创建一个能被定时任务类，方法上加入@Scheduled注解
+ 在启动类application上加入@EnableScheduling注解

```java
@SpringBootApplication
//需要开启定时任务注解
@EnableScheduling
public class ScheduledApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduledApplication.class, args);
    }

}

```

定时任务类

```java

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
```

