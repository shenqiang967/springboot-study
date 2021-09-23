package com.sq.demo.business.repository;

import com.sq.demo.business.domain.SysRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

/**
 * @Description: 测试dao  // 类说明，在创建类时要填写
 * @ClassName: TestDao    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 14:40   // 时间
 * @Version: 1.0     // 版本
 */
@Repository
public class RoleMenuDao extends SimpleJpaRepository<SysRoleMenu,Integer> {
    /**
     * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param entityManager     must not be {@literal null}.
     */
    @Autowired
    public RoleMenuDao(JpaEntityInformation<SysRoleMenu, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
}
