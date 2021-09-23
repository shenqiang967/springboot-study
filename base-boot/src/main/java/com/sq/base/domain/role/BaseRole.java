package com.sq.base.domain.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: BaseRole    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 16:47   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BaseRole {
    private Long id ;

    private String name;

}
