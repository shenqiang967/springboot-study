package com.sq.base.domian;

import com.sq.base.domain.role.BaseRole;
import com.sq.base.domain.user.BaseUser;
import com.sq.base.domain.user.dto.ApiUserDTO;
import com.sq.base.domain.user.dto.BaseUserInputDTO;
import com.sq.base.domain.user.dto.BaseUserOutputDTo;
import com.sq.base.mapstruct.ApiUserDTOMapper;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: Test    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 16:19   // 时间
 * @Version: 1.0     // 版本
 */
public class Test {
    public static void main(String[] args) {
        BaseUserInputDTO baseUserInputDTO = new BaseUserInputDTO(1L, "ccc", "18055516692", "340111111111111111", "12312312", "asdada", "http://asdasda.com"
                , "yanggou", "1231231");
        BaseUser baseUser = baseUserInputDTO.inputDTOConvert2BaseUser();
        System.out.println("baseUser: "+baseUser);
        BaseUserOutputDTo baseUserOutputDTo = new BaseUserOutputDTo().outputDTOConvert2BaseUser(baseUser);
        System.out.println("baseUserOutputDTo: "+baseUserOutputDTo);
        BaseRole baseRole = new BaseRole(1L, "单位管理员");
        baseUser.setRole(baseRole);
        ApiUserDTO apiUserDTO = ApiUserDTOMapper.INSTANCES.toApiUserDto(baseUser,baseRole);
        ApiUserDTO apiUserDTO1 = ApiUserDTOMapper.INSTANCES.toApiUserDto(baseUser);

        System.out.println("apiUserDTO: "+apiUserDTO);
        System.out.println("apiUserDTO1: "+apiUserDTO1);

    }
}
