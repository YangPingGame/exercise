package com.imooc.enums;

/**
 * @author 1819014975
 * @Title: VideoStatusEmums
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/17 13:30
 */
public enum  VideoStatusEmums {
    SUCCESS(1),
    FORBID(2);

    public final int value;

    VideoStatusEmums(int value){
        this.value = value;
    };
    public int getValue() {
        return value;
    }
}
