package com.sq.base.mapstruct;

import com.sq.base.domain.role.BaseRole;
import com.sq.base.domain.user.BaseUser;
import com.sq.base.domain.user.dto.ApiUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: ApiUserDTOMapper    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 16:39   // 时间
 * @Version: 1.0     // 版本
 */
@Mapper
public interface ApiUserDTOMapper {
    /**
     * 获取该类自动生成的实现类的实例
     * 接口中的属性都是 public static final 的 方法都是public abstract的
     */
    ApiUserDTOMapper INSTANCES = Mappers.getMapper(ApiUserDTOMapper.class);
    /**
     * 这个方法就是用于实现对象属性复制的方法
     *
     * @Mapping 用来定义属性复制规则 source 指定源对象属性 target指定目标对象属性
     *
     * @param baseUser 这个参数就是源对象，也就是需要被复制的对象
     * @return 返回的是目标对象，就是最终的结果对象
     */
    @Mappings({
            @Mapping(source = "baseUser.id", target = "userId"),
            @Mapping(source = "baseUser.xm", target = "userName"),
            @Mapping(source = "baseUser.phone", target = "userAccount"),
            @Mapping(source = "baseRole.id", target = "roleId"),
            @Mapping(source = "baseRole.name", target = "roleName")
            //@Mapping(source = "role.roleName", target = "roleName")
    })
    ApiUserDTO toApiUserDto(BaseUser baseUser, BaseRole baseRole);

    /**
     * 这个方法就是用于实现对象属性复制的方法
     *
     * @Mapping 用来定义属性复制规则 source 指定源对象属性 target指定目标对象属性
     *
     * @param baseUser 这个参数就是源对象，也就是需要被复制的对象
     * @return 返回的是目标对象，就是最终的结果对象
     */
    @Mappings({
            @Mapping(source = "baseUser.id", target = "userId"),
            @Mapping(source = "baseUser.xm", target = "userName"),
            @Mapping(source = "baseUser.phone", target = "userAccount"),
            @Mapping(source = "baseUser.role.id", target = "roleId"),
            @Mapping(source = "baseUser.role.name", target = "roleName")
            //@Mapping(source = "role.roleName", target = "roleName")
    })
    ApiUserDTO toApiUserDto(BaseUser baseUser);
}
