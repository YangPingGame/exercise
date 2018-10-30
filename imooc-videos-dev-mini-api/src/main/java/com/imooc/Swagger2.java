package com.imooc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 1819014975
 * @Title: Swagger2
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/10 11:03
 */
@Configuration
@EnableSwagger2//开启
public class Swagger2 {


    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                //api标签所在位置
                .apis(RequestHandlerSelectors.basePackage("com.imooc.controller"))
                .paths(PathSelectors.any()).build();
    }

    /**
     * 标题设置
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //题目
                .title("仿抖音小视频微信小程序后台api接口")
                //设置联系人
                .contact(new Contact("风轻云淡","https://blog.csdn.net/yang_ping_cai_niao","icooc@163.com"))
                //文档描述
                .description("描述信息")
                //版本号
               .version("1.0").build();
    }
}
