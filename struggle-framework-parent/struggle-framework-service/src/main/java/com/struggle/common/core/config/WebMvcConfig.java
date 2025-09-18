package com.struggle.common.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * WebMvc配置, 拦截器、资源映射等都在此配置
 *
 * @author EleAdmin
 * @since 2019-06-12 10:11:16
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginInterceptor loginInterceptor;

    /**
     * 将登录拦截器添加进来
     * addPathPatterns()添加拦截
     * excludePathPatterns（）排除拦截
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /***
         * 所有路径都被拦截  addPathPatterns("/**")
         * 允许通过   excludePathPatterns("/login")
         */
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/error",
                        "/druid/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/swagger-ui/**");
    }
}
