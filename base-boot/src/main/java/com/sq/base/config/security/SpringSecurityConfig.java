package com.sq.base.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Description: // 类说明，在创建类时要填写
 * @ClassName: SpringSecurityConfig    // 类名，会自动填充
 * @Author: sq          // 创建者
 * @Date: 2021/9/24 16:16   // 时间
 * @Version: 1.0     // 版本
 */
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    AuthenticationFailureHandler jwtAuthenticationFailureHandler;
    @Autowired
    AuthenticationSuccessHandler jwtAuthenticationSuccessHandler;


    /**
     * Todo configure(AuthenticationManagerBuilder)用于通过允许AuthenticationProvider容易地添加来建立认证机制。
     *      也就是说用来记录账号，密码，角色信息。下方代码不从数据库读取，直接手动赋予
     * @author sq
     * @param auth
     * @return void
     * @date 2021/10/14 14:10
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // auth
        //         .inMemoryAuthentication()
        //         .withUser("user")
        //         .password("password")
        //         .roles("USER")
        //         .and()
        //         .withUser("admin")
        //         .password("password")
        //         .roles("ADMIN","USER");
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * password 密码模式要使用此认证管理器
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Todo configure(HttpSecurity)允许基于选择匹配在资源级配置基于网络的安全性。以下示例将以/ admin /开头的网址限制为具有ADMIN角色的用户，并声明任何其他网址需要成功验证。
     *  也就是对角色的权限——所能访问的路径做出限制
     * @desc
     * @author sq
     * @param http
     * @return void
     * @date 2021/10/14 14:09
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**","/").permitAll()
                //login 不拦截
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                //授权
                .and()
                .formLogin()
                .failureHandler(jwtAuthenticationFailureHandler)
                .successHandler(jwtAuthenticationSuccessHandler)
                .and()
                // 禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 使用自己定义的拦截机制，拦截jwt
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                //授权错误信息处理
                .exceptionHandling()
                //用户访问资源没有携带正确的token
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                //用户访问没有授权资源
                .accessDeniedHandler(jwtAccessDeniedHandler)
        ;
    }
    /**
     * Todo configure(WebSecurity)用于影响全局安全性(配置资源，设置调试模式，通过实现自定义防火墙定义拒绝请求)的配置设置。
     * @desc 一般用于配置全局的某些通用事物，例如静态资源等
     * @author sq
     * @param web web控制器
     * @return void
     * @date 2021/10/14 11:30
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/repeat/*");
    }
    // @Bean
    // public PasswordEncoder getEncoder(){
    //     return new BCryptPasswordEncoder();
    // }

}
