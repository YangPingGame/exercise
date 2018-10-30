package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BasicController {

    @Autowired
    public RedisOperator redis;

    //用户redis回话
    public static final String USER_REDIS_SESSION = "user-redis-session";

    //虚拟目录
    public static final String FILE_SPACE = "H:\\wx_user_resource";

    //ffmpeg文件所在位置
    public static final String ffmpegEXE = "H:\\wx_user_resource\\ffmpeg\\bin\\ffmpeg.exe";

    //数据显示页数
    public static final Integer PAGE_SIZE = 5;
}
