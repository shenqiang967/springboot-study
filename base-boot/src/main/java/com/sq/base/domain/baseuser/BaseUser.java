package com.sq.base.domain.baseuser;

import com.sq.base.domain.role.BaseRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: 用户类  // 类说明，在创建类时要填写
 * @ClassName: BaseUser    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 13:54   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BaseUser {
    /** 主键id */
    private Long id;

    /** 姓名 */
    private String xm;

    /** 手机号码 */
    private String phone;

    /** 身份证号 */
    private String sfzh;

    /** 性别 */
    private String xb;

    private BaseRole role;
}
