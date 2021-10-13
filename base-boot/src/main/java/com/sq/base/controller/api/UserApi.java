package com.sq.base.controller.api;

import com.sq.base.common.exception.CustomException;
import com.sq.base.domain.baseuser.dto.BaseUserInputDTO;
import com.sq.base.mapstruct.ApiUserDTOMapper;
import com.sq.base.util.result.BaseResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: UserApi    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 13:41   // 时间
 * @Version: 1.0     // 版本
 */
@RestController
@RequestMapping("/mp/user")
public class UserApi {
    @PostMapping(value = "/obtain")
    public BaseResult obtainApiUser(@Valid BaseUserInputDTO baseUserInputDTO, BindingResult bindingResult){
        System.out.println(bindingResult);
        if (bindingResult.hasErrors()){
            StringBuilder errorBuilder = new StringBuilder();
            bindingResult.getAllErrors().forEach(objectError -> errorBuilder.append(objectError.getDefaultMessage()).append("\n"));
            return BaseResult.fail(errorBuilder.toString(),4001);
        }
        return BaseResult.success(ApiUserDTOMapper.INSTANCES.toApiUserDto(baseUserInputDTO.inputDTOConvert2BaseUser()));
    }

    @PostMapping(value = "/list")
    public BaseResult listApiUser(){
       return BaseResult.success();
    }


    @PostMapping(value = "/exception")
    public BaseResult exception(){
        throw new CustomException("自定義異常");
        // return BaseResult.success();
    }
}
