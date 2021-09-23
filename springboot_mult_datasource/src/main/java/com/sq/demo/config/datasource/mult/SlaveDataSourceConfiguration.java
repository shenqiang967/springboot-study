package com.sq.demo.config.datasource.mult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description: 主数据源配置  // 类说明，在创建类时要填写
 * @ClassName: MasterDataSourceConfiguration    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/20 14:33   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
@EnableTransactionManagement
//@EnableJpaRepositories注解里面的basePackages属性对应的是这个数据源对应的repository（因为使用的是jpa）， @Qualifier注解内的value要和DataSourceConfig的值一致即可。
@EnableJpaRepositories(
        entityManagerFactoryRef="entityManagerFactorySlave",
        transactionManagerRef="transactionManagerSlave",
        basePackages= { "com.sq.demo.business.repository" })
public class SlaveDataSourceConfiguration {
    @Autowired
    @Qualifier("slaveDataSource")
    private DataSource dataSource;


    @Bean(name = "entityManagerSlave")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySlave(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySlave")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySlave (EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource)
                .properties(getVendorProperties())
                .packages("com.sq.demo.business.domain") //设置实体类所在位置
                .persistenceUnit("SlavePersistenceUnit")
                .build();
    }

    @Autowired
    private JpaProperties jpaProperties;

    private Map<String, String> getVendorProperties() {

        return jpaProperties.getProperties();
    }

    @Bean(name = "transactionManagerSlave")
    public PlatformTransactionManager transactionManagerSlave(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySlave(builder).getObject());
    }

}
