package com.sq.base.domain.task;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @Description: 定时任务  // 类说明，在创建类时要填写
 * @ClassName: ScheduledTask    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/23 13:55   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@Entity
public class ScheduleJob  {
    /** 任务id */
    @Id
    @GeneratedValue
    private String jobId;
    /** 任务名称 */
    @Column(name = "job_name")
    private String jobName;
    /** 任务分组 */
    @Column(name = "job_group")
    private String jobGroup;
    /** 任务状态 0禁用 1启用 2删除*/
    @Column(name = "job_status")
    private String jobStatus;
    /** 任务运行时间表达式 */
    @Column(name = "cron_expression")
    private String cronExpression;
    /** 任务描述 */
    @Column(name = "desc")
    private String desc;
}
