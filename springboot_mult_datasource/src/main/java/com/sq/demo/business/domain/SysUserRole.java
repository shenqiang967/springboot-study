package com.sq.demo.business.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: SysUserRole    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 15:20   // 时间
 * @Version: 1.0     // 版本
 */
@Entity
@Table(name = "sys_user_role")
@Data
public class SysUserRole {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "role_id")
    private Long roleId;
}
