package com.sq.base.config.elasticsearch.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.sq.base.config.elasticsearch.service.IEsService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: EsServiceImpl    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/15 13:24   // 时间
 * @Version: 1.0     // 版本
 */
@Slf4j
public class EsServiceImpl implements IEsService {
    @Autowired
    public RestHighLevelClient restHighLevelClient;

    protected static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        // 默认缓冲限制为100MB，此处修改为30MB。
        builder.setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(30 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Override
    public void createIndexRequest(String index) {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index)
                .settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 0));
        try {
            CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, COMMON_OPTIONS);
            log.info(" 所有节点确认响应 : {}", response.isAcknowledged());
            log.info(" 所有分片的复制未超时 :{}", response.isShardsAcknowledged());
        } catch (IOException e) {
            log.error("创建索引库【{}】失败", index, e);
        }
    }

    @Override
    public void deleteIndexRequest(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = restHighLevelClient.indices().delete(request, COMMON_OPTIONS);
            System.out.println(response.isAcknowledged());
        } catch (IOException e) {
            log.error("删除索引库【{}】失败", index, e);
        }
    }

    @Override
    public void updateRequest(String index, String id, Object object) {
        UpdateRequest updateRequest = new UpdateRequest(index, id);
        updateRequest.doc(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.update(updateRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("更新索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }
    }

    @Override
    public void insertRequest(String index, String id, Object object) {
        IndexRequest indexRequest = new IndexRequest(index).id(id).source(BeanUtil.beanToMap(object), XContentType.JSON);
        try {
            restHighLevelClient.index(indexRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("创建索引文档 {" + index + "} 数据 {" + object + "} 失败", e);
        }

    }

    @Override
    public void deleteRequest(String index, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(index, id);
        try {
            restHighLevelClient.delete(deleteRequest, COMMON_OPTIONS);
        } catch (IOException e) {
            log.error("删除索引文档 {" + index + "} 数据id {" + id + "} 失败", e);
        }
    }
}
