package com.sq.demo.business.domain;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserDo    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 11:28   // 时间
 * @Version: 1.0     // 版本
 */
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
