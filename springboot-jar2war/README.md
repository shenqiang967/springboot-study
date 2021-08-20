# jar包转war包

> 注意：必须是web环境下才可以jar转war

## 修改pom

```xml
<!--<packaging>jar</packaging>-->
<packaging>war</packaging>
```

## 添加依赖

```xml
<!--添加tomcat-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
            <scope>provided</scope>
        </dependency>
<!--添加servlet依赖-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.0.1</version>
            <scope>provided</scope>
        </dependency>
<!--添加web依赖-->
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
```

## 实现SpringBootServletInitializer

```java
//新增DemoInitializer类
public class DemoInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringbootJarToWarApplication.class);
    }
}
```

