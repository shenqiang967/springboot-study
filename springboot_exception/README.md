# 统一异常处理

添加依赖

```xml
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

```

视图层代码

```java
@RestController
@RequestMapping("test")
public class TestController {
    @GetMapping(value = "error")
    public HashMap<String,Object> error(){
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("error","123213");
        System.out.println(resultMap.toString());
        return resultMap;
    }
    @GetMapping(value = "exception")
    public HashMap<String,Object> exception(){
        HashMap<String, Object> resultMap = new HashMap<>();
        System.out.println(1/0);
        return resultMap;
    }
}
```

## ErrorController

> 当前系统发现未知错误时，系统会跳到/error接口，若未定义ErrorController的实现类，则自定调用系统默认的BasicErrorController实现。

```java
/**
 * @Description: 统一异常处理类  // 类说明，在创建类时要填写
 * @ClassName: CommonErrorController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 17:02   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("/error")
public class CommonErrorController extends AbstractErrorController {


    public CommonErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
    @Autowired
    public CommonErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }
    /**
     * @desc 页面访问时错误跳转逻辑
     * @author sq
     * @param null 
     * @return 
     * @date 2021/8/20 10:04
     */
    @RequestMapping(produces = {MediaType.TEXT_HTML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public String errorHtml(HttpServletRequest request, HttpServletResponse response) {

        return "/base/error";
    }
    /**
     * @desc 接口访问时错误跳转返回时结果
     * @author sq
     * @return 
     * @date 2021/8/20 10:03
     */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return R.ok(status);
    }

}
```

## @ControllerAdvice

> 统一controller层切面，用于统一捕获controller层异常

```java
/**
 * @Description: 对视图层切面，异常统一处理// 类说明，在创建类时要填写
 * @ClassName: CommonExceptionHandler    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 9:46   // 时间
 * @Version: 1.0     // 版本
 */

@ControllerAdvice
public class CommonExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);
    //拦截未授权页面
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    //这里简单的捕获一下异常超类，真正业务逻辑定义一个自定义的运行时异常捕获
    @ExceptionHandler(Exception.class)
    //以json方式返回出去
    @ResponseBody
    public R handleUnauthorizedException(Exception e) {
        logger.error(e.getMessage(),e);
        return R.fail(e.getMessage());
    }

}
```

## R 统一结果返回

> 通常配合枚举以及自定义异常来使用

```java
public class R<T> {
    private String msg;
    private Integer code;
    private T data;

    public R(String msg, Integer code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public R() {
    }

    public static R ok(Object data){
       return new R<>("成功！",200,data);
    }

    public static R fail(String message) {
        return new R(message,500,null);
    }

    @Override
    public String toString() {
        return "R{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
```

