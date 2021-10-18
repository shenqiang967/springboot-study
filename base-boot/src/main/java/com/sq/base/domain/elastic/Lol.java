package com.sq.base.domain.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: Lol    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/15 13:30   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lol implements Serializable {
    private Long id;
    /**
     * 英雄游戏名字
     */
    private String name;
    /**
     * 英雄名字
     */
    private String realName;
    /**
     * 英雄描述信息
     */
    private String desc;
}