package com.sq.base.config.elasticsearch.service;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: IEsService    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/15 13:23   // 时间
 * @Version: 1.0     // 版本
 */
public interface IEsService {

    /**
     * 创建索引库
     */
    void createIndexRequest(String index);

    /**
     * 删除索引库
     */
    void deleteIndexRequest(String index);

    /**
     * 更新索引文档
     */
    void updateRequest(String index, String id, Object object);

    /**
     * 新增索引文档
     */
    void insertRequest(String index, String id, Object object);

    /**
     * 删除索引文档
     */
    void deleteRequest(String index, String id);
}
