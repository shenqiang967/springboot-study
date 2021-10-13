package com.sq.base.aspect.service.impl;

import com.sq.base.aspect.service.IRepeatTokenService;
import com.sq.base.common.constants.RedisConstants;
import com.sq.base.common.exception.CustomException;
import com.sq.base.config.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: RepeatTokenServiceImpl    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/10/13 15:26   // 时间
 * @Version: 1.0     // 版本
 */
@Service
@Slf4j
public class RepeatTokenService implements IRepeatTokenService {
    @Autowired
    private RedisService redisService;
    /**
     * 创建token
     *
     * @return
     */
    @Override
    public String createToken() {
        String str = UUID.randomUUID().toString();
        StringBuilder token = new StringBuilder();
        try{
            token.append(RedisConstants.REPEAT_TOKEN_PREFIX).append(str);
            redisService.expire(token.toString(),10000L);
            boolean notEmpty = StringUtils.isNotEmpty(token.toString());
            if(notEmpty) {
                return token.toString();
            }
        }catch(Exception ex){
            log.error("redis save repeat token fail ",ex);
        }
        return null;
    }

    /**
     * 检验token
     *
     * @param request
     * @return
     */
    @Override
    public boolean checkToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader(RedisConstants.TOKEN_NAME);
        if(StringUtils.isBlank(token)) {// header中不存在token
            token = request.getParameter(RedisConstants.TOKEN_NAME);
            if(StringUtils.isBlank(token)) {// parameter中也不存在token
                log.error("request header Missing REPEAT_TOKEN");
                throw new CustomException("request header Missing REPEAT_TOKEN");
            }
        }
        if(!redisService.exists(token)) {
            log.error("redis Missing REPEAT_TOKEN");
            throw new CustomException("redis Missing REPEAT_TOKEN");
        }
        boolean remove = redisService.deleteObject(token);
        if(!remove) {
            log.error("redis delete key fail");
            throw new CustomException("redis delete key fail");
        }
        return true;
    }
}
