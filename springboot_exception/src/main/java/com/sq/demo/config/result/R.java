package com.sq.demo.config.result;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: R    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 17:12   // 时间
 * @Version: 1.0     // 版本
 */
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
