package com.sq.base.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: ApiUserDTO    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 16:34   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiUserDTO {

    private Long userId;

    private String userName;

    private String userAccount;

    private String roleId;

    private String roleName;
}
