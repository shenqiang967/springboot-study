package com.sq.base.config.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Objects;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: RedisConfiguration    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 10:14   // 时间
 * @Version: 1.0     // 版本
 */
@Configuration
@EnableCaching //开启缓存
public class RedisConfiguration extends CachingConfigurerSupport {
    @Bean
    public CacheManager cacheManager(RedisTemplate<?,?> redisTemplate) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig(this.getClass().getClassLoader());
        redisCacheConfiguration
                //设置缓存保存时间
                .entryTtl(Duration.ofMinutes(1))
                //设置缓存的name(key)前缀
                .prefixCacheNameWith("test:cache:")
                //.prefixKeysWith() 等同于prefixCacheNameWith，该方法被废弃了
        ;
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        return new RedisCacheManager(redisCacheWriter,redisCacheConfiguration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        FastJson2JsonRedisSerializer fastJson2JsonRedisSerializer = new FastJson2JsonRedisSerializer(Object.class);
        //key值使用Utf-8String
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        //使用自定义的fastJson2JsonRedisSerializer 也可以使用官方的fastjson序列化类
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        fastJson2JsonRedisSerializer.setObjectMapper(mapper);
        redisTemplate.setValueSerializer(fastJson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(fastJson2JsonRedisSerializer);
        return redisTemplate;
    }
}
