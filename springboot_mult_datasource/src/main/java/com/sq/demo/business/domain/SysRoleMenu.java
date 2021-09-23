package com.sq.demo.business.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: SysRoleMenu    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 15:24   // 时间
 * @Version: 1.0     // 版本
 */
@Entity
@Table(name = "sys_role_menu")
public class SysRoleMenu {
    @Id
    private Long id;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "user_id")
    private Long userId;
}
