package com.imooc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 1819014975
 * @Title: HelloWorIdController
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 12:11
 */
@RestController
@Api(value = "测试接口")
public class HelloWorIdController {

    @ApiOperation(value = "测试",notes = "测试")
    @RequestMapping("/hello")
    public String Hello(){
        return "Hello Spring boot";
    }

}
