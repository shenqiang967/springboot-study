package com.sq.base.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
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
    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }

}
