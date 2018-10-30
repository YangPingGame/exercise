package com.imooc;

import com.imooc.controller.interceptor.MineInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author 1819014975
 * @Title: WebMvcconfig
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/11 16:31
 */
@Configuration//设置成配置文件
public class WebMvcconfig extends WebMvcConfigurerAdapter {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将某个文件夹设置为服务器资源
       registry.addResourceHandler("/**")
               //由于swagger-ui是静态资源所以会发生冲突需要重新设置映射
               .addResourceLocations("classpath:/META-INF/resources/")
               .addResourceLocations("file:H:/wx_user_resource/");
    }

    /**
     * 配置拦截器的bean
     * @return
     */
    @Bean
    public MineInterceptor mineInterceptor(){
        return new MineInterceptor();
    }

    /**
     * 拦截器注册中心
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //将拦截器的bean放入
        registry.addInterceptor(mineInterceptor())
                //要拦截的路径
                .addPathPatterns("/**")
                //要排除在拦截范围的路径登录和注册
                .excludePathPatterns("/login","/regist")
        .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
        super.addInterceptors(registry);
    }
}
