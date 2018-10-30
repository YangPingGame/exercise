package com.imooc.controller;

import com.imooc.pojo.Usersinfo;
import com.imooc.pojo.vo.UsersinfoVo;
import com.imooc.service.impl.UserServiceImpl;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author 1819014975
 * @Title: RequstLoginController
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 16:34
 */
@RestController
@Api(value = "用户注册登录接口",tags = {"注册和登录的controller"})
public class RegistLoginController extends BasicController{

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistLoginController.class);
    @Autowired
    private UserServiceImpl userService;

    /**
     * post访问注册
     * @param users
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult regist(@RequestBody Usersinfo users) throws Exception {
        //1.判断用户名和密码必须不能为空
        if(StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())){
            LOGGER.error("用户信息为空，疑似刷接口");
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        //2.判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(users.getUsername());
        //3.保存用户，注册信息
        if(!usernameIsExist){
            users.setNickname(users.getUsername());
            //对密码进行加密
            users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
            users.setFansCounts(0);
            users.setReceiveLikeCounts(0);
            users.setFollowCounts(0);
            userService.saveUser(users);
        }else{
            return IMoocJSONResult.errorMsg("用户名已存在,请更换用户名");
        }
        LOGGER.info("用户注册成功！");
        users.setPassword("");
        UsersinfoVo usersinfoVo = setUserRedisSessionToken(users);
        return IMoocJSONResult.ok(usersinfoVo);
    }

    /**
     * 生成唯一token
     * 无状态回话（用户唯一标识符）
     * @param userModel
     * @return
     */
    public UsersinfoVo setUserRedisSessionToken(Usersinfo userModel){
        String uniqueToken = UUID.randomUUID().toString();
        //使用分号，redis会自动分组（将唯一标识符存入缓存中并且保留半个小时）
        redis.set(USER_REDIS_SESSION+":"+userModel.getId(),uniqueToken,1000*60*30);
        UsersinfoVo usersinfoVo = new UsersinfoVo();
        usersinfoVo.setUserToken(uniqueToken);
        BeanUtils.copyProperties(userModel,usersinfoVo);
        return usersinfoVo;
    }

    /**
     * 访问登录
     * @param users
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户登录",notes = "用户登录接口")
    @PostMapping("/login")
    public IMoocJSONResult login(@RequestBody Usersinfo users) throws Exception {
        //判断内容是否为空
        if(StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getUsername())){
            LOGGER.error("用户信息为空，疑似刷接口");
            return IMoocJSONResult.errorMsg("用户名和密码不能为空");
        }
        //2.判断用户名是否存在
        users.setNickname(users.getUsername());
        //对密码进行加密
        users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
        Usersinfo usersinfo = userService.queryUser(users);
        //3.保存用户，登录信息
        if(usersinfo == null){
            LOGGER.error("用户信息账号或密码错误");
            return IMoocJSONResult.errorMsg("账号或密码错误，请重试");
        }

        LOGGER.info("用户登录成功！");
        usersinfo.setPassword("");
        UsersinfoVo usersinfoVo = setUserRedisSessionToken(usersinfo);
        return IMoocJSONResult.ok(usersinfoVo);
    }

    /**
     * 用户注销
     * @param userId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户注销",notes = "用户登录接口")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,
              dataType = "String",paramType = "query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId){
        redis.del(USER_REDIS_SESSION+":"+userId);
        LOGGER.info("用户退出登录成功！");
        return IMoocJSONResult.ok();
    }
}
