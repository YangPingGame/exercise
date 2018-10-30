package com.imooc.mapper;

import com.imooc.pojo.Usersinfo;
import com.imooc.utils.MyMapper;

public interface UsersinfoMapper extends MyMapper<Usersinfo> {

    /**
     * 视频被喜欢的累加
     * @param userId
     */
    void addReceiveLikeCount(String userId);

    /**
     * 视频被喜欢的累减
     * @param userId
     */
    void reduceReceiveLikeCount(String userId);

    /**
     * 增加关注
     * @param userId
     */
    void addFollerCount(String userId);

    /**
     * 减少关注
     * @param userId
     */
    void reduceFollerCount(String userId);


    /**
     * 增加粉丝数量
     * @param userId
     */
    void addFansCount(String userId);

    /**
     * 减少粉丝数量
     * @param userId
     */
    void reduceFansCount(String userId);

}