package com.imooc.service;

import com.imooc.pojo.Bgm;
import com.imooc.pojo.Usersinfo;

import java.util.List;

/**
 * @author 1819014975
 * @Title: UserService
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/10 9:49
 */
public interface BgmService {

    List<Bgm> quweryBgmList();

    Bgm queryBgmById(String bgmId);
}
