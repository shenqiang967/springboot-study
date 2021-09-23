package com.sq.base.util.result;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: BaseResult    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 13:40   // 时间
 * @Version: 1.0     // 版本
 */
public class BaseResult {
    private String msg;

    private Integer code;

    private Object data;

    private BaseResult(String msg, Integer code, Object data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    private BaseResult() {

    }



    public static BaseResult success(){
        return new BaseResult("请求成功",200,null);
    }
    public static BaseResult success(Object data){
        return new BaseResult("请求成功",200,data);
    }
    public static BaseResult fail(Integer code){
        return new BaseResult("请求失败",code,null);
    }
    public static BaseResult fail(String msg,Integer code){
        return new BaseResult(msg,code,null);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", data=" + data +
                '}';
    }
}
