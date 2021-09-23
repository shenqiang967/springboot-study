package com.sq.base.domain.user.dto;

import com.sq.base.domain.user.BaseUser;
import com.sq.base.util.convert.Converter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

/**
 * @Description: 输出DTO，用于详情或者列表  // 类说明，在创建类时要填写
 * @ClassName: BaseUserOutputDTo    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/22 13:55   // 时间
 * @Version: 1.0     // 版本
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class BaseUserOutputDTo {
    /** 姓名 */
    private String xm;

    /** 手机号码 */
    private String phone;

    /** 身份证号 */
    private String sfzh;

    public BaseUserOutputDTo outputDTOConvert2BaseUser(BaseUser baseUser){
        return new BaseUserConverter().doBackward(baseUser);
    }

    private static class BaseUserConverter extends Converter<BaseUserOutputDTo, BaseUser> {

        @Override
        protected BaseUser doForward(BaseUserOutputDTo baseUserOutputDTO) {
            BaseUser baseUser = new BaseUser();
            BeanUtils.copyProperties(baseUserOutputDTO,baseUser);
            return baseUser;
        }

        @Override
        protected BaseUserOutputDTo doBackward(BaseUser baseUser) {
            BaseUserOutputDTo baseUserOutputDTO = new BaseUserOutputDTo();
            BeanUtils.copyProperties(baseUser,baseUserOutputDTO);
            return baseUserOutputDTO;
        }
    }
}
