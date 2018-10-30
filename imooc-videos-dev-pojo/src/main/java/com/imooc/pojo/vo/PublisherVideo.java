package com.imooc.pojo.vo;

/**
 * @author 1819014975
 * @Title: PublisherVideo
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/23 14:39
 */
public class PublisherVideo {

    //用户信息
    private UsersinfoVo usersinfoVo;
    //登录者是否喜欢
    private boolean userLikeVideo;

    //用户是否收藏过
    private boolean isKeep;

    public boolean isKeep() {
        return isKeep;
    }

    public void setKeep(boolean keep) {
        isKeep = keep;
    }

    public UsersinfoVo getUsersinfoVo() {
        return usersinfoVo;
    }

    public void setUsersinfoVo(UsersinfoVo usersinfoVo) {
        this.usersinfoVo = usersinfoVo;
    }

    public boolean isUserLikeVideo() {
        return userLikeVideo;
    }

    public void setUserLikeVideo(boolean userLikeVideo) {
        this.userLikeVideo = userLikeVideo;
    }
}
