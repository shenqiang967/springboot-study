package com.sq.base.util.convert;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: Converter    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 15:07   // 时间
 * @Version: 1.0     // 版本
 */
public abstract class Converter<A, B> {
    protected abstract B doForward(A a);
    protected abstract A doBackward(B b);
}
