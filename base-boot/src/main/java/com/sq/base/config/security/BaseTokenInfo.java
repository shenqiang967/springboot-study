package com.sq.base.config.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: BaseTokenInfo    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/26 10:48   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseTokenInfo {
    private String userName;
    private HashMap<String,Object> extraMap;
    //小程序openId 存放到extraMap
    //private String openId;
    //小程序sessionKey 存放到extraMap
    //private String sessionKey;
    //过期时间
    private Long expiration;

}
