# springboot 多数据源

很多业务场景都需要使用到多数据库，本文介绍springboot对多数据源的使用。

添加依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```



这次先说一下application.properties文件，分别连接了2个数据库master和slave。完整代码如下：

```properties
##端口号
server.port=8888


##数据库url
spring.datasource.master.url=jdbc:mysql://192.168.208.23:3308/ry-master?characterEncoding=utf8&useSSL=false
##数据库用户名
spring.datasource.master.username=root
##数据库密码
spring.datasource.master.password=abc123!@#
##数据库驱动
spring.datasource.master.driver-class-name=com.mysql.jdbc.Driver

##数据库url
spring.datasource.slave.url=jdbc:mysql://192.168.208.23:3306/test2?characterEncoding=utf8&useSSL=false
##数据库用户名
spring.datasource.slave.username=root
##数据库密码
spring.datasource.slave.password=abc123
##数据库驱动
spring.datasource.slave.driver-class-name=com.mysql.jdbc.Driver


spring.jpa.hibernate.ddl-auto=create
##控制台打印sql
spring.jpa.show-sql=true

```

