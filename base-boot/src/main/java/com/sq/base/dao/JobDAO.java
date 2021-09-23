package com.sq.base.dao;

import com.sq.base.domain.task.ScheduleJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserDAO    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 13:46   // 时间
 * @Version: 1.0     // 版本
 */
@Repository
public interface JobDAO extends JpaRepository<ScheduleJob,Long> {

}
