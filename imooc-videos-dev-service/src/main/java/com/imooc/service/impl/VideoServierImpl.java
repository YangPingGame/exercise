package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentsVo;
import com.imooc.pojo.vo.VideosVo;
import com.imooc.service.VideoServer;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

/**
 * @author 1819014975
 * @Title: VideoServierImpl
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/17 13:35
 */
@Service
public class VideoServierImpl implements VideoServer {


    //报错不要紧，不会影响程序运行
    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersinfoMapper usersinfoMapper;

    @Autowired
    private VideosMapperSutom videosMapperSutom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersKeepVideosMapper usersKeepVideosMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        String id = sid.nextShort();
        //插入唯一ID
        videos.setId(id);
        //insertSelective:值若为空则使用数据库的默认值
        videosMapper.insertSelective(videos);
        return id;
    }

    /**
     * 修改视频信息
     * @param videoid
     * @param coverPath
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoid,String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoid);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideo(Videos videos,Integer isSaveRecord,
                                   Integer page, Integer pageSize) {
        String desc = videos.getVideoDesc();
        //判断是谁查询（作品查询）
        String userId = videos.getUserId();
        //保存热搜词
        if(isSaveRecord !=null && isSaveRecord==1){
            SearchRecords records = new SearchRecords();
            records.setId(sid.nextShort());
            records.setContent(desc);//搜索词
            searchRecordsMapper.insert(records);
        }
       PageHelper.startPage(page,pageSize);
       List<VideosVo> videosList = videosMapperSutom.queryAllVideoVo(desc,userId);
        PageInfo<VideosVo> pageInfo = new PageInfo<>(videosList);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页数
        pagedResult.setTotal(pageInfo.getPages());//总页数
        pagedResult.setRows(videosList);//数据内容
        pagedResult.setRecords(pageInfo.getTotal());//总条数

        return pagedResult;
    }

    /**
     * 前20名热词搜索展示
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWorde() {
        return searchRecordsMapper.getHotWords();
    }

    /**
     * 用户对视频点赞
     * 每一次喜欢和收藏都是单独创建一条数据
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.保存用户和视频的喜欢点赞关系表(为空sql默认为0)
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);
        //2.视频喜欢数量累加
        videosMapperSutom.addVideoLikeCount(videoId);
        //3.用户受喜欢数量的累加
        usersinfoMapper.addReceiveLikeCount(userId);
    }


    /**
     * 用户取消视频点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1.删除用户和视频的喜欢点赞关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersLikeVideosMapper.deleteByExample(example);
        //2.视频喜欢数量累减
        videosMapperSutom.reduceVideoLikeCount(videoId);
        //3.用户受喜欢数量的累减
        usersinfoMapper.reduceReceiveLikeCount(userId);
    }

    /**
     * 用户收藏
     * @param userId
     * @param videoId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userKeepVideo(String userId, String videoId) {
        //1.保存用户和视频的喜欢点赞关系表(为空sql默认为0)
        String likeId = sid.nextShort();
        UsersKeepVideos ukv = new UsersKeepVideos();
        ukv.setId(likeId);
        ukv.setUserId(userId);
        ukv.setVideoId(videoId);
        usersKeepVideosMapper.insert(ukv);
    }


    /**
     * 用户取消收藏
     * @param userId
     * @param videoId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnKeepVideo(String userId, String videoId) {
        //1.删除用户和视频的喜欢点赞关系表
        Example example = new Example(UsersKeepVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        usersKeepVideosMapper.deleteByExample(example);
    }
    /**
     * 用户收藏的作品列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize){
        PageHelper.startPage(page,pageSize);
        List<VideosVo> list = videosMapperSutom.queryMyKeepVideos(userId);

        PageInfo<VideosVo> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());
        return pagedResult;
    }

    /**
     * 评论保存
     * @param comments
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void seaveCommnet(Comments comments){
        comments.setId(sid.nextShort());
        comments.setCreateTime(new Date());
        commentsMapper.insert(comments);
    }

    /**
     * 评论查询
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getVideoComment(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page,pageSize);
        List<CommentsVo> list = commentsMapperCustom.queryComments(videoId);
        //将时间转换为具体的时候
        for(CommentsVo c : list){
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }
        //mybatis的分页工具
        PageInfo<CommentsVo> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}
