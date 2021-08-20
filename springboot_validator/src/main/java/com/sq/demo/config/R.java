package com.sq.demo.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: R    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 13:08   // 时间
 * @Version: 1.0     // 版本
 */
@XmlRootElement
public class R<T> {
    @XmlElement
    private T data;
    @XmlElement
    private Integer code;
    @XmlElement
    private String msg;

    public R() {
    }

    public R(T data, Integer code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static R ok(){
        return new R(null,200,"请求成功！");
    }
    public static R ok(Object data){
        return new R(data,200,"请求成功！");
    }
    @Override
    public String toString() {
        return "R{" +
                "data=" + data +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
