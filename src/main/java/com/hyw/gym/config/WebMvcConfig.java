package com.hyw.gym.config;
// @author 黄元威

import com.hyw.gym.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 【关键】只拦会员接口
                .addPathPatterns("/api/member/**")
                // 排除登录、发码
                .excludePathPatterns(
                        "/api/member/code",
                        "/api/member/login"
                );
    }
}