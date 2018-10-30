package com.imooc;

import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author 1819014975
 * @Title: Application
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 10:32
 */
@SpringBootApplication
//扫描mybatis mapper包路劲
@MapperScan(basePackages = "com.imooc.mapper")
//为其他配置过容器的文件添加包扫描
@ComponentScan(basePackages = {"com.imooc","org.n3r.idworker"})
public class Application {

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}