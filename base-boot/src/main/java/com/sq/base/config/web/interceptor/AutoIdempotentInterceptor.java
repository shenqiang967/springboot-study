package com.sq.base.config.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.sq.base.aspect.AutoIdempotent;
import com.sq.base.aspect.service.impl.RepeatTokenService;
import com.sq.base.common.constants.HttpStatus;
import com.sq.base.common.exception.CustomException;
import com.sq.base.util.result.BaseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Description: 重复提交拦截器
 * @ClassName: AutoIdempotentInterceptor
 * @Author: sq          // 创建者
 * @Date: 2021/10/13 16:05   // 时间
 * @Version: 1.0     // 版本
 */
@Component
@Slf4j
public class AutoIdempotentInterceptor implements HandlerInterceptor {
    @Autowired
    private RepeatTokenService repeatTokenService;
    /**
     * Interception point before the execution of a handler. Called after
     * HandlerMapping determined an appropriate handler object, but before
     * HandlerAdapter invokes the handler.
     * <p>DispatcherServlet processes a handler in an execution chain, consisting
     * of any number of interceptors, with the handler itself at the end.
     * With this method, each interceptor can decide to abort the execution chain,
     * typically sending an HTTP error or writing a custom response.
     * <p><strong>Note:</strong> special considerations apply for asynchronous
     * request processing. For more details see
     * {@link AsyncHandlerInterceptor}.
     * <p>The default implementation returns {@code true}.
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself. Else, DispatcherServlet assumes
     * that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //被ApiIdempotment标记的扫描
        AutoIdempotent methodAnnotation = method.getAnnotation(AutoIdempotent.class);
        if(methodAnnotation != null) {
            try{
                return repeatTokenService.checkToken(request);// 幂等性校验, 校验通过则放行, 校验失败则抛出异常, 并通过统一异常处理返回友好提示
            }catch(CustomException e){
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().print(JSON.toJSONString(BaseResult.fail(HttpStatus.ILLEGAL_REPEAT_TOKEN)));
                return false;
            }catch (Exception ex){
                log.error(ex.getMessage(),ex);
                return false;
            }
        }
        //必须返回true,否则会被拦截一切请求
        return true;
    }

}
