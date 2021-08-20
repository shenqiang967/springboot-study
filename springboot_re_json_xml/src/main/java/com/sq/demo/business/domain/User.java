package com.sq.demo.business.domain;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
