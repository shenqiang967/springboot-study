package com.sq.base.domain.baseuser.dto;

import com.sq.base.domain.baseuser.BaseUser;
import com.sq.base.util.convert.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.Pattern;

/**
 * @Description: 输入类DTO，用于新增编辑  // 类说明，在创建类时要填写
 * @ClassName: BaseUserInputDTO    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 13:55   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BaseUserInputDTO {
    /** 主键id */
    private Long id;

    /** 姓名 */
    @NotEmpty(message = "姓名不能为空")
    @Length(min = 2,max = 5,message = "长度大于等于2小于等于5")
    private String xm;

    /** 手机号码 */
    @NotEmpty(message = "手机号码不能为空")
    @Pattern(regexp = "^(((13[0-9])|(14[567])|(15[0-9])|(17[6-8])|(18[0-9]))\\d{8})|((170[059]\\d{7}))$",message = "不符合手机号格式")
    private String phone;

    /** 身份证号 */
    @NotEmpty(message = "身份证号不能为空")
    @Pattern(regexp ="^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X|x))",message = "不符合身份证号格式")
    private String sfzh;

    /** 性别 */
    private String xb;

    /** OpenId */
    private String openId;

    /** UnionId */
    private String unionId;

    /** 头像 */
    private String avatarUrl;

    /** 昵称 */
    private String nickname;

    public BaseUser inputDTOConvert2BaseUser(){
        return new BaseUserConverter().doForward(this);
    }

    private static class BaseUserConverter extends Converter<BaseUserInputDTO, BaseUser> {

        @Override
        protected BaseUser doForward(BaseUserInputDTO baseUserInputDTO) {
            BaseUser baseUser = new BaseUser();
            BeanUtils.copyProperties(baseUserInputDTO,baseUser);
            return baseUser;
        }

        @Override
        protected BaseUserInputDTO doBackward(BaseUser baseUser) {
            BaseUserInputDTO baseUserInputDTO = new BaseUserInputDTO();
            BeanUtils.copyProperties(baseUserInputDTO,baseUser);
            return baseUserInputDTO;
        }
    }
}
