package com.sq.base.config.security;

import com.sq.base.dao.UserDao;
import com.sq.base.domain.sysuser.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @Description: 登录信息验证  // 类说明，在创建类时要填写
 * @ClassName: UserDetailsServiceImpl    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 13:53   // 时间
 * @Version: 1.0     // 版本
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserDao userDao;
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(userName);
        Example<SysUser> userExample = Example.of(sysUser);
        Optional<SysUser> userOptional = userDao.findOne(userExample);
        if (!userOptional.isPresent()){
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(userOptional.get().getUserName()).password(userOptional.get().getPassword()).roles("admin").authorities("user:reg","user:resetPwd").build();
    }
    /**
     * User user = new User();
     *     user.setUsername("y");
     *     user.setAddress("sh");
     *     user.setPassword("admin");
     *     ExampleMatcher matcher = ExampleMatcher.matching()
     *             .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.startsWith())//模糊查询匹配开头，即{username}%
     *             .withMatcher("address" ,ExampleMatcher.GenericPropertyMatchers.contains())//全部模糊查询，即%{address}%
     *             .withIgnorePaths("password");//忽略字段，即不管password是什么值都不加入查询条件
     *     Example<User> example = Example.of(user ,matcher);
     *     List<User> list = userRepository.findAll(example);
     *     System.out.println(list);
     */
}
