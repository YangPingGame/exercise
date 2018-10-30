package com.imooc.service.impl;

import com.imooc.mapper.BgmMapper;
import com.imooc.mapper.UsersinfoMapper;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Usersinfo;
import com.imooc.service.BgmService;
import com.imooc.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 1819014975
 * @Title: UserServiceImpl
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/10 9:52
 */
@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    /**
     * 查询所有背景音乐
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> quweryBgmList() {
        return bgmMapper.selectAll();
    }

    /**
     * 根据id查询背景音乐信息
     * @param bgmId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmById(String bgmId){
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}
