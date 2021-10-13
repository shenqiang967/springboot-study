package com.sq.base.service.impl;

import com.sq.base.dao.JobDAO;
import com.sq.base.domain.task.ScheduleJob;
import com.sq.base.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserService    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 13:47   // 时间
 * @Version: 1.0     // 版本
 */
@Service("jobService")
@Slf4j
public class JobServiceImpl implements IJobService {
    @Autowired
    JobDAO userDAO;


    @Override
    public void say() {
        List<ScheduleJob> all = userDAO.findAll();
        log.info("22222222222222222222");
    }
}
