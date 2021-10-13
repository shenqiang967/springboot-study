package com.sq.base.domain.sysuser;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Description: 系统用户  // 类说明，在创建类时要填写
 * @ClassName: SysUser    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 14:07   // 时间
 * @Version: 1.0     // 版本
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {
    /**
     * 主键
     *
     * @mbg.generated
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 账号
     *
     * @mbg.generated
     */
    @Column(name = "username")
    private String userName;

    /**
     * 密码
     *
     * @mbg.generated
     */
    @Column(name = "password")
    private String password;

    /**
     * 昵称
     *
     * @mbg.generated
     */
    @Column(name = "nick_name")
    private String nickName;

    /**
     * 手机号，支持电话号码（电信：133、149、153、173、177、180、181、189、191、199、联通：130、131、132、145、155、156、166、171、175、176、185、186、移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、172、178、182、183、184、187、188、198）
     *
     * @mbg.generated
     */
    @Column(name = "phone")
    private String phone;
    /**
     * 账号状态（1：初始； 2：启用； 3：注册待审批；  4：审批通过；  5：审批拒绝 ；6：锁定）
     *
     * @mbg.generated
     */
    @Column(name = "status")
    private String status;

    @JSONField(format = "yyyy-MM-dd")
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
