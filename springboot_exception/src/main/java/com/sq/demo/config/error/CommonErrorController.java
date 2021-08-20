package com.sq.demo.config.error;

import com.sq.demo.config.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 统一异常处理类  // 类说明，在创建类时要填写
 * @ClassName: CommonErrorController    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/8/19 17:02   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("/error")
public class CommonErrorController extends AbstractErrorController {


    public CommonErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }
    @Autowired
    public CommonErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
    }
    /**
     * @desc 页面访问时错误跳转逻辑
     * @author sq
     * @param null
     * @return
     * @date 2021/8/20 10:04
     */
    @RequestMapping(produces = {MediaType.TEXT_HTML_VALUE,MediaType.APPLICATION_JSON_VALUE})
    public String errorHtml(HttpServletRequest request, HttpServletResponse response) {

        return "/base/error";
    }
    /**
     * @desc 接口访问时错误跳转返回时结果
     * @author sq
     * @return
     * @date 2021/8/20 10:03
     */
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public R error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return R.ok(status);
    }

}
