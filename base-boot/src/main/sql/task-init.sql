CREATE TABLE `schedule_job` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(128) NOT NULL COMMENT '任务名称',
  `job_group` varchar(128) DEFAULT NULL COMMENT '任务分组',
  `job_status` varchar(128) NOT NULL COMMENT '任务状态 0禁用 1启用 2删除',
  `cron_expression` int(2) NOT NULL DEFAULT '1' COMMENT '任务运行时间表达式',
  `desc` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务描述',
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `uniqu_job_group` (`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
