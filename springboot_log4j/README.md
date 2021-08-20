# 使用Log4j日志处理

pom修改

```xml
		<!--排除包内自带的log插件-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <!--新增新的log插件-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-log4j</artifactId>
            <version>1.3.6.RELEASE</version>
            <type>pom</type>
        </dependency>
```

resource下新建log4j.properties

```properties
### 设置 [ level ] , appenderName, appenderName, … ###
#示例 输出info以上级别日志 
log4j.rootLogger = info,stdout,D,E
### 输出信息到控制抬 ConsoleAppender ###
log4j.appender.stdout = org.apache.log4j.ConsoleAppender
### ConsoleAppender输出对象 ###
log4j.appender.stdout.Target = System.out
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern = [%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n
### 输出DEBUG 级别以上的日志到=E://logs/log.log ###
log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = ./logs/log.log
log4j.appender.D.Append = true
log4j.appender.D.Threshold = DEBUG
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
### 输出ERROR 级别以上的日志到=E://logs/error.log ###
log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
log4j.appender.E.File =./logs/error.log
log4j.appender.E.Append = true
log4j.appender.E.Threshold = ERROR
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n
```

## 定义配置文件

其实您也可以完全不使用配置文件，而是在代码中配置Log4j环境。但是，使用配置文件将使您的应用程序更加灵活。Log4j支持两种配置文件格式，一种是XML格式的文件，一种是Java特性文件（键=值）。下面我们介绍使用Java特性文件做为配置文件的方法：
1.配置根Logger，其语法为：

```properties
#log4j.rootLogger = [ level ] , appenderName, appenderName, …
log4j.rootLogger = info,stdout,D,E
```

其中，level 是日志记录的优先级，分为OFF、FATAL、ERROR、WARN、INFO、DEBUG、ALL或者您定义的级别。Log4j建议只使用四个级别，优 先级从高到低分别是ERROR、WARN、INFO、DEBUG。通过在这里定义的级别，您可以控制到应用程序中相应级别的日志信息的开关。比如在这里定 义了INFO级别，则应用程序中所有DEBUG级别的日志信息将不被打印出来。 appenderName就是指B日志信息输出到哪个地方。您可以同时指定多个输出目的地。

2.配置日志信息输出目的地Appender，其语法为：

```properties
log4j.appender.appenderName = fully.qualified.name.of.appender.class  
log4j.appender.appenderName.option1 = value1  
…  
log4j.appender.appenderName.option = valueN
```

其中，Log4j提供的appender有以下几种：

> org.apache.log4j.ConsoleAppender（控制台），  
> org.apache.log4j.FileAppender（文件），  
> org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件），  
> org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件），  
> org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）

```properties

log4j.appender.appenderName.layout = fully.qualified.name.of.layout.class  
log4j.appender.appenderName.layout.option1 = value1  
…  
log4j.appender.appenderName.layout.option = valueN
```

其中，Log4j提供的layout有以下几种：

> org.apache.log4j.HTMLLayout（以HTML表格形式布局），  
> org.apache.log4j.PatternLayout（可以灵活地指定布局模式），  
> org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串），  
> org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息）
>
> 可以继承Layout实现自定义布局输出

Log4J采用类似C语言中的printf函数的打印格式格式化日志信息，打印参数如下：

> ```html
> %m 输出代码中指定的消息
> %p 输出优先级，即DEBUG，INFO，WARN，ERROR，FATAL  
> %r 输出自应用启动到输出该log信息耗费的毫秒数  
> %c 输出所属的类目，通常就是所在类的全名  
> %t 输出产生该日志事件的线程名  
> %n 输出一个回车换行符，Windows平台为“rn”，Unix平台为“n”  
> %d 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如：%d{yyy MMM dd HH:mm:ss,SSS}，输出类似：2002年10月18日 22：10：28，921  
> %l 输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。举例：Testlog4.main(TestLog4.java:10)
> ```

 2.2 在代码中使用Log4j

1.获取记录器

使用Log4j，第一步就是获取日志记录器，这个记录器将负责控制日志信息。其语法为：

> public static Logger getLogger( String name)

通过指定的名字获得记录器，如果必要的话，则为这个名字创建一个新的记录器。Name一般取本类的名字，比如：

> static Logger logger = Logger.getLogger ( ServerWithLog4j.class.getName () )

2.读取配置文件

当获得了日志记录器之后，第二步将配置Log4j环境，其语法为：

> ```
> BasicConfigurator.configure ()： 自动快速地使用缺省Log4j环境。
> PropertyConfigurator.configure ( String configFilename) ：读取使用Java的特性文件编写的配置文件。
> DOMConfigurator.configure ( String filename ) ：读取XML形式的配置文件。
> ```