package com.imooc.service;

import com.imooc.pojo.UsersReport;
import com.imooc.pojo.Usersinfo;

/**
 * @author 1819014975
 * @Title: UserService
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/10 9:49
 */
public interface UserService {

    /**
     * 判断用户是否存在
     * @param username
     * @return
     */
     boolean queryUsernameIsExist(String username);

    /**
     * 注册
     * @param users
     */
    void saveUser(Usersinfo users);

    /**
     * 登录查询
     * @param users
     * @return
     */
    Usersinfo queryUser(Usersinfo users);

    /**
     * 用户修改信息
     * @param user
     */
    void updateUserInfo(Usersinfo user);

    /**
     * 根据userid获取用户信息
     * @param userId
     * @return
     */
    Usersinfo queryUser(String userId);

    /**
     * 用户是否喜欢
     * @param userId
     * @param videoId
     * @return
     */
    boolean isUserLikeVideo(String userId,String videoId);

    /**
     * 用户是否收藏
     * @param userId
     * @param videoId
     * @return
     */
    boolean isUserKeepVideo(String userId,String videoId);

    /**
     * 增加用户和粉丝的关系
     * @param userId
     * @param fanId
     */
    void saveUserFanRelation(String userId,String fanId);

    /**
     * 删除粉丝和用户的关系
     * @param UserId
     * @param fanId
     */
    void deleteUserFanRelation(String UserId,String fanId);

    /**
     * 查询用户和视发布者之间是否关注过
     * @param userId
     * @param fanId
     * @return
     */
    boolean sevaUserIsFollow(String userId,String fanId);

    /**
     * 视频举报
     * @param usersReport
     */
    void reportUser(UsersReport usersReport);
}
