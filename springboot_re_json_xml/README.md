# SpringBoot返回json和xml

## 添加依赖

```xml
<!--web-->	
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
<!--jackson-->
        <dependency>
            <groupId>com.fasterxml.jackson.dataformat</groupId>
            <artifactId>jackson-dataformat-xml</artifactId>
        </dependency>
 <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
```

实体类

```java
/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: User    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 11:03   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@XmlRootElement
public class User {
    @XmlElement
    private String userName;
    @XmlElement
    private String passWord;
    @XmlElement
    private Integer age;
}
```

视图层

```java
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping(value = "/xml/{userName}",produces = MediaType.APPLICATION_XML_VALUE)
    public User getXml(@PathVariable(value = "userName") String userName){
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(UUID.randomUUID().toString());
        user.setAge((int)(Math.random()*100));
        return user;
    }
    @GetMapping(value = "/json/{userName}",produces = MediaType.APPLICATION_JSON_VALUE)
    public User getJson(@PathVariable(value = "userName") String userName){
        User user = new User();
        user.setUserName(userName);
        user.setPassWord(UUID.randomUUID().toString());
        user.setAge((int)(Math.random()*100));
        return user;
    }
}
```

配置文件

```properties
spring.application.name= reJsonXml
server.port= 8079
```

返回结果

http://localhost:8079/user/xml/324

```xml
<User>
    <userName>324</userName>
    <passWord>32356715-f8a4-4fd3-9e2b-822a4207e31a</passWord>
    <age>0</age>
</User>
```

http://localhost:8079/user/json/324

```json
{"userName":"324","passWord":"28167d5b-1873-4011-be50-927be4bce2ff","age":0}
```
