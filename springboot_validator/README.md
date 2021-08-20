# SpringBoot使用validator校验

在前台表单验证的时候，通常会校验一些数据的可行性，比如是否为空，长度，身份证，邮箱等等，那么这样是否是安全的呢，答案是否定的。因为也可以通过模拟前台请求等工具来直接提交到后台，比如postman这样的工具，那么遇到这样的问题怎么办呢，我们可以在后台也做相应的校验。

## 添加依赖

```xml
 		<!--web-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!--lombok-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
		 <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>5.4.3.Final</version>
        </dependency>
```

实体类代码

```java
@Data
public class UserDo implements Serializable {
    @NotEmpty(message="用户名不能为空")
    @Length(min=6,max = 12,message="用户名长度必须位于6到12之间")
    private String userName;


    @NotEmpty(message="密码不能为空")
    @Length(min=6,message="密码长度不能小于6位")
    private String passWord;

    @Email(message="请输入正确的邮箱")
    private String email;

    @Pattern(regexp = "^(\\d{18,18}|\\d{15,15}|(\\d{17,17}[x|X]))$", message = "身份证格式错误")
    private String idCard;

}

```

视图层代码

```java
@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping(value = "/addUser" , produces = MediaType.APPLICATION_JSON_VALUE)
    public String add(@Valid UserDo user, BindingResult bindingResult){
        StringBuilder msgBuilder = new StringBuilder();
        if (bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(err->{
                msgBuilder.append(err.getDefaultMessage()).append("\n");
            });
        }
        return msgBuilder.toString();
    }
}
```

