package com.imooc.service.impl;

import com.imooc.mapper.*;
import com.imooc.pojo.*;
import com.imooc.service.UserService;
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
 * @Title: UserServiceImpl
 * @ProjectName imooc-videos-dev
 * @Description: TODO
 * @date 2018/10/10 9:52
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersinfoMapper usersinfoMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersKeepVideosMapper usersKeepVideosMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    /*
    @Transactional(propagation=Propagation.SUPPORTS) ：
    如果其他bean调用这个方法,在其他bean中声明事务,那就用事务.如果其他bean没有声明事务,那就不用事务.
    */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Usersinfo usersinfo = new Usersinfo();
        usersinfo.setUsername(username);
        Usersinfo users = usersinfoMapper.selectOne(usersinfo);
        return users != null;
    }

    /*
    @Transactional(propagation=Propagation.REQUIRED) ：
    如果有事务, 那么加入事务, 没有的话新建一个(默认情况下)
    适合新增，修改，删除
    */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Usersinfo users) {
        //获取唯一id
         users.setId(sid.nextShort());
         //数据插入
         usersinfoMapper.insert(users);
    }

    /**
     * 登录查询
     * @param users
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS )
    @Override
    public Usersinfo queryUser(Usersinfo users){
      return usersinfoMapper.selectOne(users);
    }

    /**
     * 用户信息修改
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Usersinfo user) {
        usersinfoMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 用户信息查询
     * @param userId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Usersinfo queryUser(String userId) {
        Example userExampl = new Example(Usersinfo.class);
        Example.Criteria criteria = userExampl.createCriteria();
        criteria.andEqualTo("id",userId);
        return usersinfoMapper.selectOneByExample(userExampl);
    }

    /**
     * 查询当前用户是否喜欢这个视频
     * @param userId
     * @param videoId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {
        Example userLikeVideoExampl = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = userLikeVideoExampl.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(userLikeVideoExampl);
        if(list != null && list.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 用户是否收藏了这个视频
     * @param userId
     * @param videoId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserKeepVideo(String userId, String videoId) {
        Example userKeepVideoExampl = new Example(UsersKeepVideos.class);
        Example.Criteria criteria = userKeepVideoExampl.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);
        List<UsersKeepVideos> list = usersKeepVideosMapper.selectByExample(userKeepVideoExampl);
        if(list != null && list.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 添加关注增加粉丝
     * @param userId
     * @param fanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        //1.增加关粉丝数
        UsersFans usersFans = new UsersFans();
        usersFans.setId(sid.nextShort());
        usersFans.setUserId(userId);
        usersFans.setFanId(fanId);
        usersFansMapper.insert(usersFans);

        //2.增加关注
        usersinfoMapper.addFansCount(userId);
        usersinfoMapper.addFollerCount(fanId);
    }

    /**
     * 取消关注删除粉丝
     * @param userId
     * @param fanId
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example userLikeVideoExampl = new Example(UsersFans.class);
        Example.Criteria criteria = userLikeVideoExampl.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(userLikeVideoExampl);

    }

    /**
     * 查询用户和视频发布者之间的关系
     * @param userId
     * @param fanId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean sevaUserIsFollow(String userId, String fanId) {
        Example userLikeVideoExampl = new Example(UsersFans.class);
        Example.Criteria criteria = userLikeVideoExampl.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(userLikeVideoExampl);
        if(list != null && !list.isEmpty() && list.size()>0){
            return true;
        }
        return false;
    }

    /**
     * 用户举报非法信息
     * @param usersReport
     */
    @Override
    public void reportUser(UsersReport usersReport) {
        String urId = sid.nextShort();
        usersReport.setId(urId);
        usersReport.setCreateDate(new Date());
        usersReportMapper.insertSelective(usersReport);

    }
}
