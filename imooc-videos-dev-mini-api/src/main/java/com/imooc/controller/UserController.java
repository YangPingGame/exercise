package com.imooc.controller;

import com.imooc.pojo.UsersReport;
import com.imooc.pojo.Usersinfo;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.pojo.vo.UsersinfoVo;
import com.imooc.service.impl.UserServiceImpl;
import com.imooc.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author 1819014975
 * @Title: RequstLoginController
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 16:34
 */
@RestController
@Api(value = "用户相关业务接口",tags = {"用户相关业务controller"})
@RequestMapping("/user")
public class UserController extends BasicController{

    //日志
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServiceImpl userService;

    /**
     * 用户头像上传
     * @param userId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户头像上传",notes = "用户头像上传接口")
    @ApiImplicitParam(name="userId",value = "用户id",required = true,
              dataType = "String",paramType = "query")
    @PostMapping("/uploadFace/{userId}")
    public IMoocJSONResult logout(@PathVariable("userId") String userId,
                                 @RequestParam("file") MultipartFile[] files) throws Exception {
        if(StringUtils.isBlank(userId)){
            LOGGER.error("图片上传错误，错误原因err=（用户ID为空）");
            return IMoocJSONResult.errorMsg("用户ID不能为空...");
        }

        //文件保存的命名空间
//        String fileSpace = "H:\\wx_user_resource";
        //文件保存的相对路径
        String uploadPathDB = "/"+userId+"/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            //判断数据不能为空
            if (files != null && files.length > 0) {
                //获取文件名称
                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    String finalFacePath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置最终保存路径
                    uploadPathDB += ("/" + fileName);
                    //判断目录文件是否存在
                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父类文件
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    //使用工具将数据流进行拷贝
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else{
                LOGGER.error("图片上传错误，错误原因err=（图片路径为空）");
                return IMoocJSONResult.errorMsg("上传出错，请重试");
            }
        }catch (Exception e){
            LOGGER.error("图片上传错误，错误原因err="+e);
            return IMoocJSONResult.errorMsg("上传出错，请重试");
        }finally {
            if(fileOutputStream != null){
                //关闭数据流
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //修改用户头像
        Usersinfo usersinfo = new Usersinfo();
        usersinfo.setId(userId);
        usersinfo.setFaceImage(uploadPathDB);
        userService.updateUserInfo(usersinfo);
        LOGGER.info("用户头像上传成功");
        return IMoocJSONResult.ok(uploadPathDB);
    }


    /**
     * 用户信息查询
     * @param userId
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "用户个人信息查询",notes = "用户信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",value = "用户id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="fanId",value = "粉丝id",required = true,
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId){
        if(StringUtils.isBlank(userId)){
            LOGGER.info("用户ID为空");
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        Usersinfo usersinfo = userService.queryUser(userId);
        UsersinfoVo userVo = new UsersinfoVo();
        //是否有点赞关系
        boolean isFollow = userService.sevaUserIsFollow(userId,fanId);
        userVo.setIsFollow(isFollow);
        //将数据复制到userVo中
        BeanUtils.copyProperties(usersinfo,userVo);
        LOGGER.info("用户信息查询成功");
        return IMoocJSONResult.ok(userVo);
    }

    /**
     * 用户信息查询
     * @param loginUserId
     * @param videoId
     * @param publishUserId
     * @return
     */
    @ApiOperation(value = "视频发布者用户个人信息查询",notes = "视频用户信息查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name="loginUserId",value = "登录用户id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="videoId",value = "视频id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="publishUserId",value = "发布视频用户id",required = true,
                    dataType = "String",paramType = "query")

    })
    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
        if(StringUtils.isBlank(loginUserId) || StringUtils.isBlank(videoId) || StringUtils.isBlank(publishUserId)){
            LOGGER.info("视频用户ID为空");
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //查询视频发布者的信息
        Usersinfo usersinfo = userService.queryUser(publishUserId);
        UsersinfoVo userVo = new UsersinfoVo();
        //将数据复制到userVo中
        BeanUtils.copyProperties(usersinfo,userVo);
        //查询当前登录者和视频关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId,videoId);
        boolean isKeep = userService.isUserKeepVideo(loginUserId,videoId);
        PublisherVideo video = new PublisherVideo();
        video.setUsersinfoVo(userVo);
        video.setUserLikeVideo(userLikeVideo);
        video.setKeep(isKeep);
        LOGGER.info("视频发布者与用户户关系信息查询成功");
        return IMoocJSONResult.ok(video);
    }

    /**
     * 加关注，添加粉丝
     * @param userId
     * @param fanId
     * @return
     */
    @ApiOperation(value = "添加关注，并增加粉丝",notes = "用户关注")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",value = "用户用户id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="fanId",value = "粉丝id",required = true,
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/userAttention")
    public IMoocJSONResult userAttention(String userId,String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
         userService.saveUserFanRelation(userId,fanId);
        LOGGER.info("("+userId+")关注("+fanId+")成功");
        return IMoocJSONResult.ok("关注成功...");
    }

    /**
     * 取消关注，删除粉丝
     * @param userId
     * @param fanId
     * @return
     */
    @ApiOperation(value = "取消关注，并减少粉丝",notes = "用户取消关注")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId",value = "用户用户id",required = true,
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name="fanId",value = "粉丝id",required = true,
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/userUnsubscribe")
    public IMoocJSONResult userUnsubscribe(String userId,String fanId){
        if(StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.deleteUserFanRelation(userId,fanId);
        LOGGER.info("("+userId+")取消关注("+fanId+")成功");
        return IMoocJSONResult.ok("取消关注成功...");
    }

    /**
     * 用户对视频的举报
     * @param usersReport
     * @return
     */
    @ApiOperation(value = "用户视频举报入口",tags = "对不良视频进行举报")
    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){
        userService.reportUser(usersReport);
        LOGGER.info("用户视频举报成功");
        return IMoocJSONResult.errorMsg("有你会更加美好...");
    }
}