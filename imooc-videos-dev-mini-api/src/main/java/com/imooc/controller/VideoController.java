package com.imooc.controller;

import com.imooc.enums.VideoStatusEmums;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.service.impl.BgmServiceImpl;
import com.imooc.service.impl.VideoServierImpl;
import com.imooc.utils.FfmpegVideoAndMp3;
import com.imooc.utils.FfmpegVideoJpg;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * @author 1819014975
 * @Title: RequstLoginController
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/9 16:34
 */
@RestController
@Api(value = "视频相关业务接口",tags = {"视频相关业务controller"})
@RequestMapping("/video")
public class VideoController extends BasicController{

    //日志
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

    @Autowired
    private VideoServierImpl videoServier;

    @Autowired
    private BgmServiceImpl bgmService;

    /**
     * 用户头像上传
     * @param userId
     * @return
     * @throws Exception
     *
     * path	以地址的形式提交数据
     * query	直接跟参数完成自动映射赋值
     * body	以流的形式提交 仅支持POST
     * header	参数在request headers 里边提交
     * form	以form表单的形式提交 仅支持POST
     */
    @ApiOperation(value = "视频上传",notes = "视频上传接口")
    //设置多个api字段
    @ApiImplicitParams({
        @ApiImplicitParam(name="userId",value = "用户id",required = true,
                dataType = "String",paramType = "form"),
        @ApiImplicitParam(name="bgmId",value = "背景音乐id",required = true,
                dataType = "String",paramType = "form"),
        @ApiImplicitParam(name="videoSeconds",value = "视频时长",required = true,
                dataType = "double",paramType = "form"),
        @ApiImplicitParam(name="videoWidth",value = "视频宽度",required = true,
                dataType = "int",paramType = "form"),
        @ApiImplicitParam(name="videoHeight",value = "视频高度",required = true,
                dataType = "String",paramType = "form"),
        @ApiImplicitParam(name="desc",value = "视频描述",required = true,
                dataType = "String",paramType = "form")
    })
    @PostMapping(value = "/upload",headers = "content-type=multipart/form-data")
    public IMoocJSONResult logout(String userId,String bgmId,double videoSeconds,
                                  int videoWidth,int videoHeight,String desc,
                                  @ApiParam(value = "短视频",required = true) MultipartFile file) throws Exception {
        if(StringUtils.isBlank(userId)){
            LOGGER.error("视频上传错误，错误原因err=（用户ID为空）");
            return IMoocJSONResult.errorMsg("用户ID不能为空...");
        }

        //文件保存的最终目录
        String uploadPathDB = "/"+userId+"/video";
        //首页截图文件夹
        String coverPathDB = "/"+userId+"/video/cover";
        //图片和视频名称
        String videoName = UUID.randomUUID().toString();
        //文件保存的相对路径临时文件夹中
        String finalVideoPath = FILE_SPACE + uploadPathDB + "/temporary";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            //判断数据不能为空
            if (file != null) {
                //获取文件名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath += ("/" + fileName);
                    //创建存放截图的文件夹
                    File jpgFile = new File(FILE_SPACE+coverPathDB);
                    //创建cover文件夹
                    if(!jpgFile.exists()){
                        jpgFile.mkdirs();
                    }
                    coverPathDB = coverPathDB+"/"+videoName+".jpg";
                    //视频文件夹判断目录文件是否存在
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父类文件
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    //使用工具将数据流进行拷贝
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else{
                LOGGER.error("用户："+userId+"视频上传错误，错误原因err=（视频路径为空）");
                return IMoocJSONResult.errorMsg("上传出错，请重试");
            }
        }catch (Exception e){
            LOGGER.error("用户："+userId+"视频上传错误，错误原因err="+e);
            return IMoocJSONResult.errorMsg("视频上传出错，请重试");
        }finally {
            if(fileOutputStream != null){
                //关闭数据流
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        FfmpegVideoAndMp3 ffmpegVideoAndMp3 = new FfmpegVideoAndMp3(ffmpegEXE);
        String videoOutPutName = videoName+".mp4";
        //判断是否选择了背景音乐
        if(StringUtils.isNotBlank(bgmId)){
            //查询出背景音乐列表
            Bgm bgm = bgmService.queryBgmById(bgmId);
            //查询背景音乐所在虚拟目录位置
            String mp3INputPath = FILE_SPACE+bgm.getPath();
            //视频与背景音乐合成
            uploadPathDB +=("/"+videoOutPutName);
            String vdieoOutputPath = FILE_SPACE+uploadPathDB;
            ffmpegVideoAndMp3.converter(finalVideoPath,mp3INputPath,videoSeconds,vdieoOutputPath);
        }else{
            uploadPathDB +=("/"+videoOutPutName);
            String vdieoOutputPath = FILE_SPACE+uploadPathDB;
            //视频类型格式转换
            ffmpegVideoAndMp3.videoMp4(finalVideoPath,vdieoOutputPath);
        }


        //对视频进行截图
        FfmpegVideoJpg ffmpegVideoJpg = new FfmpegVideoJpg(ffmpegEXE);
        ffmpegVideoJpg.converter(FILE_SPACE+uploadPathDB,FILE_SPACE+coverPathDB);
        //保存视频信息到数据库中
        Videos videos = new Videos();
        videos.setAudioId(bgmId);
        videos.setUserId(userId);
        videos.setVideoSeconds((float)videoSeconds);
        videos.setVideoHeight(videoHeight);
        videos.setVideoWidth(videoWidth);
        videos.setVideoDesc(desc);
        videos.setCreateTime(new Date());
        videos.setVideoPath(uploadPathDB);
        videos.setStatus(VideoStatusEmums.SUCCESS.getValue());//状态
        videos.setCoverPath(coverPathDB);
        //保存信息
        String videoId = videoServier.saveVideo(videos);
        LOGGER.info("用户："+userId+"上传成功");
        return IMoocJSONResult.ok(videoId);
    }

    /**
     * 视频封面上传
     * @param userId
     * @return
     * @throws Exception
     *
     * path	以地址的形式提交数据
     * query	直接跟参数完成自动映射赋值
     * body	以流的形式提交 仅支持POST
     * header	参数在request headers 里边提交
     * form	以form表单的形式提交 仅支持POST
     */
    @ApiOperation(value = "视频封面上传",notes = "视频封面上传接口",tags ="由于微信用不到，所以可以不用" )
    //设置多个api字段
    @ApiImplicitParams({
            @ApiImplicitParam(name="videoId",value = "视频id",required = true,
                    dataType = "String",paramType = "form"),
            @ApiImplicitParam(name="userId",value = "用户id",required = true,
                    dataType = "String",paramType = "form")
    })
    @PostMapping(value = "/uploadCover",headers = "content-type=multipart/form-data")
    public IMoocJSONResult logout(String videoId,String userId,
                                   @ApiParam(value = "视频封面",required = true) MultipartFile file) throws Exception {
        if(StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)){
            LOGGER.error("视频封面上传错误，错误原因err=（用户ID和用户封面ID为空）");
            return IMoocJSONResult.errorMsg("用户ID和用户封面ID不能为空...");
        }
        //文件保存的最终目录
        String uploadPathDB = "/"+userId+"/video/cover";
        //文件保存的相对路径临时文件夹中
        String finalVideoPath = FILE_SPACE + uploadPathDB;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            //判断数据不能为空
            if (file != null) {
                //获取文件名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath += ("/" + fileName);
                    uploadPathDB += ("/" + fileName);
                    //判断目录文件是否存在
                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父类文件
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    //使用工具将数据流进行拷贝
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else{
                LOGGER.error("视频封面上传错误，错误原因err=（视频封面路径为空）");
                return IMoocJSONResult.errorMsg("上传出错，请重试");
            }
        }catch (Exception e){
            LOGGER.error("视频封面上传错误，错误原因err="+e);
            return IMoocJSONResult.errorMsg("视频上传出错，请重试");
        }finally {
            if(fileOutputStream != null){
                //关闭数据流
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //保存视频信息到数据库中
        //保存信息
        videoServier.updateVideo(videoId,uploadPathDB);
        return IMoocJSONResult.ok(videoId);
    }

    /**
     * 微信上滑加载
     * 分页和搜索查询视频列表
     * isSaveRecord 1- 需要保存
     *              0-不需要保存
     * @param page
     * @return
     */
    @ApiOperation(value = "视频列表",notes = "视频列表搜索和条件搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videos",value = "json视频接口",
                    dataType = "Videos",paramType = "query"),
            @ApiImplicitParam(name = "isSaveRecord",value = "词汇是否需要保存",required = true,
                    dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "当前页码",required = true,
                    dataType = "Integer",paramType = "query")
    })
    @PostMapping("/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos videos,
                                   Integer isSaveRecord, Integer page){
        //判断数据为空的情况
        if(page == null){
           page = 1;
        }
        PagedResult result = videoServier.getAllVideo(videos,isSaveRecord,page,PAGE_SIZE);
        LOGGER.info("用户视频列表查询操作");
        return IMoocJSONResult.ok(result);
    }

    /**
     * 热词搜索
     * @return
     */
    @ApiOperation(value = "热搜词搜索",notes = "搜索次数最多的词汇")
    @PostMapping("/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoServier.getHotWorde());
    }

    @ApiOperation(value = "视频点赞")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "登录用户ID",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "视频id",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoCreaterId",value = "视频创建者id",
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/userLike")
    public IMoocJSONResult userLike(String userId,String videoId,String videoCreaterId){
        videoServier.userLikeVideo(userId,videoId,videoCreaterId);
        LOGGER.info(userId+"用户为视频"+videoId+"点赞操作");
        return IMoocJSONResult.ok();
    }


    @ApiOperation(value = "视频取消点赞")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "登录用户ID",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "视频id",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoCreaterId",value = "视频创建者id",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "type",value = "类型编号",
                    dataType = "Integer",paramType = "query")
    })
    @PostMapping("/userUnLike")
    public IMoocJSONResult userUnLike(String userId,String videoId,String videoCreaterId){
        videoServier.userUnLikeVideo(userId,videoId,videoCreaterId);
        LOGGER.info(userId+"用户为视频"+videoId+"取消点赞操作");
        return IMoocJSONResult.ok();
    }


    @ApiOperation(value = "视频收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "登录用户ID",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "视频id",
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/userKeep")
    public IMoocJSONResult userKeep(String userId,String videoId,String videoCreaterId){
        videoServier.userKeepVideo(userId,videoId);
        LOGGER.info(userId+"用户为视频"+videoId+"收藏操作");
        return IMoocJSONResult.ok();
    }


    @ApiOperation(value = "视频取消收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "登录用户ID",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoId",value = "视频id",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "videoCreaterId",value = "视频创建者id",
                    dataType = "String",paramType = "query")
    })
    @PostMapping("/userUnKeep")
    public IMoocJSONResult userUnKeep(String userId,String videoId){
        videoServier.userUnKeepVideo(userId,videoId);
        LOGGER.info(userId+"用户为视频"+videoId+"取消收藏操作");
        return IMoocJSONResult.ok();
    }


    /**
     * 视频收藏查询接口
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "用户视频收藏列表",tags = "用户个人个人收藏的视频")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户ID",
                    dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "page",value = "视频当前页",
                    dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "pageSize",value = "视频显示条数",
                    dataType = "Integer",paramType = "query")
    })
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId,Integer page,Integer pageSize){
        if(StringUtils.isBlank(userId)){
            return IMoocJSONResult.ok();
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 6;
        }
        //分页查询数据
        PagedResult result = videoServier.queryMyLikeVideos(userId,page,pageSize);
        return IMoocJSONResult.ok(result);
    }

    /**
     * 1.用户评论保存
     * 2.用户对用户的评论进行保存
     * @param comments
     * @return
     */
    @ApiOperation(value = "用户留言保存接口",tags = "用户用来保存评论")
    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comments,
                                       String fatherCommentId,String toUserId){
        comments.setFatherCommentId(fatherCommentId);
        comments.setToUserId(toUserId);
        videoServier.seaveCommnet(comments);
        return IMoocJSONResult.ok();
    }

    /**
     * 对应视频信息的视频留言查询
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "用户留言查询接口",tags = "用户用户来查询留言")
    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId,Integer page,Integer pageSize){

        if(StringUtils.isBlank(videoId)){
            return IMoocJSONResult.ok();
        }
        //分页查询列表，按时间顺序倒叙排序
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }
        PagedResult list = videoServier.getVideoComment(videoId,page,pageSize);

        return IMoocJSONResult.ok(list);

    }
}
