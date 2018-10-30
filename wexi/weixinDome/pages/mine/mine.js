var videoutils = require("../../utils/video-utils.js")
//获取应用实例
const app = getApp()

Page({
  data: {
    faceUrl: '../resource/images/noneface.png',
    isMe:true,
    isFollow:false,

    isSelectdWork:"video-info-selected",
    isSelectdLike:"",
    isSelectdFollow:"",
    // 作品
    myVideoList:[],
    myVideoPage:1,
    myVideoTotal:1,
   //收藏
    likeVideoList:[],
    likeVideoPage:1,
    likeVideoTotal:1,
    // 关注
    followVideoList:[],
    followVideoPage:1,
    followVideoTotal:1,
    //列表展示
    myWorkFalg:false,
    myLikesFalg:true,
    myFollowFalg:true
  },
  onLoad:function(params){
    //首次加载查询
    var thar = this;
    //登录用户信息
    var user = app.getGllobalUserInfo();
     var userId = user.id;
     var publisherId = params.publisherId;
     if(publisherId != null && publisherId != undefined && publisherId != ''){
       //判断两编号是否相同
       if (publisherId != userId) {
         userId = publisherId;
         //判断是否是本人的页面
         thar.setData({
           isMe: false,
           publisherId: publisherId
         })
       }
     }
    var serviceUrl = app.serverUrl;
    wx.showLoading({
      title: '请稍等...',
      mask: true
    })
    wx.request({
      url: serviceUrl + '/user/query',
      method: "POST",
      data:{
        userId:userId,
        fanId:user.id
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
        "userId":user.id,
        "token":user.userToken
      },
      success(res) {
        wx.hideLoading();
        var data = res.data;
        if (data.status === 200) {
          var userinfo = data.data;
          //设置默认地址
          var faceUser = "../resource/images/noneface.png";
          if(userinfo.faceImage != null && userinfo.faceImage != ''&& userinfo.faceImage != undefined){
            faceUser = serviceUrl + userinfo.faceImage;
          }
          thar.setData({
            faceUrl:faceUser,
            nickname: userinfo.nickname,
            fansCounts: userinfo.fansCounts,
            followCounts: userinfo.followCounts,
            receiveLikeCounts: userinfo.receiveLikeCounts,
            isFollow: userinfo.isFollow
          });
          thar.doSelectWork();
        }else{
          //跳转登录页面
          wx.showModal({
            title: "用户提示",
            showCancel: false,
            content: data.msg,
            success() {
              wx.redirectTo({
                url: '../userLogin/login',
              });
            }
          });
        }
      },
      fail() {
        wx.hideLoading();
        wx.showToast({
          title: '连接超时',
          image: '/icom/error.png'
        });
      },
      complete(res) {
        //无论成功与否都要执行这个方法
        wx.hideLoading();
      }
    });
  },
  //关注点击和非关注点击
  followMe:function(e){
     var thar = this;
     var user = app.getGllobalUserInfo();
     var userId = user.id;
     var publisherId = thar.data.publisherId;

     var followType = e.currentTarget.dataset.followtype;
     //判断是否关注还是取消关注
    var url = "/user/userUnsubscribe";
     if(followType === "1"){
        url = "/user/userAttention";
     }
     wx.showLoading();
     //请求提交
     wx.request({
       url: app.serverUrl+url,
       method: "POST",
       header: {
         "Content-Type": "application/x-www-form-urlencoded",
         "userId": user.id,
         "token": user.userToken
       },
       data:{
         userId:publisherId,
         fanId:userId
       },
       success(res){
          wx.hideLoading();
          if(followType === "1"){
            thar.setData({
              isFollow:true,
              fansCounts:++thar.data.fansCounts
            });
          }else{
            thar.setData({
              isFollow: false,
              fansCounts: --thar.data.fansCounts
            });
          }
       }
     })
  },


  //登录退出
  logout:function(){
    var serviceUrl = app.serverUrl;
    var user = app.getGllobalUserInfo();
    wx.showLoading({
      title: '请稍等...',
      mask: true
    })
    wx.request({
      url: serviceUrl+'/logout?userId='+user.id,
      method:"POST",
      header:{
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      success(res){
        wx.hideLoading();
        var data = res.data;
        if(data.status === 200){
          wx.showToast({
            title: '注销成功',
            icon:"success",
            duration:2000
          });
          //将数据为空
          app.removeUserInfo();
          //返回到登录页面L
          wx.navigateTo({
            url: '../userLogin/login',
          });
        } else (
          //跳转登录页面
          wx.showModal({
            title: "用户提示",
            showCancel: false,
            content: data.msg,
            success(){
               wx.redirectTo({
                 url: '../userLogin/login',
               });
            }
          })
        )
      },
      fail() {
        wx.hideLoading();
        wx.showToast({
          title: '连接超时',
          image: '/icom/error.png'
        });
      },
      complete(res) {
        //无论成功与否都要执行这个方法
        wx.hideLoading();
      }
    })
  }, 
  // 更换头像
  changeFace:function(){
    // 保证this的作用域
    var that = this;
    //头像上传
    //上传组件
    wx.chooseImage({
      count: 1,//上传数量
      sizeType: ['compressed'],//是否压缩
      sourceType: ['album', 'camera'],//从相册或相机获取图片
      success(res) {
        // tempFilePath可以作为img标签的src属性显示图片
        const tempFilePaths = res.tempFilePaths;
        //要上传的地址
        var serverUrl = app.serverUrl;
        var user = app.getGllobalUserInfo();
        //等待动画
        wx.showLoading({
          title: '请稍等...',
          mask: true
        });
        //上传中
        wx.uploadFile({
          url: serverUrl + "/user/uploadFace/" + user.id, 
          filePath: tempFilePaths[0],
          name: 'file',//名称
          header:{
            "content-type": "application/json",
            "userId": user.id,
            "token": user.userToken
          },
          success(res) {
            //上传成功返回的是一个字符串不是对象所以需要转换
            var data = JSON.parse(res.data);
            //结束动画
            wx.hideLoading();
            if (data.status === 200){
              //上传成功并提示
              wx.showToast({
                title: '上传成功',
                icon:'success'
              });
              //将上传的头像进行更换
              var imageUrl = data.data;
              that.setData({
                faceUrl:serverUrl+imageUrl
              })
            } else if(data.status == 500){
              wx.showToast({
                title: data.msg
              })
            }else{
              wx.showToast({
                title: '找管理员拯救一下你',
              })
            }
          },
          fail() {
            wx.hideLoading();
            wx.showToast({
              title: '连接超时',
              image: '/icom/error.png'
            });
          },
          complete(res) {
            //无论成功与否都要执行这个方法
            wx.hideLoading();
          }
        });
      }
    });
  },
  uploadVideo:function(){
    //调用工具类中的上传组件
    videoutils.uploadVideo();
  },
  // 动态tob
  // 作品
  doSelectWork:function(){
    
    this.setData({
      isSelectdWork: "video-info-selected",
      isSelectdLike: "",
      isSelectdFollow: "",  
      // 作品
      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,
      //收藏
      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,
      // 关注
      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,
      //列表展示
      myWorkFalg: false,
      myLikesFalg: true,
      myFollowFalg: true
    });
    this.getMyVideoWork(1);
  },
  //收藏
  doSelectLike: function () {
    this.setData({
      isSelectdWork: "",
      isSelectdLike: "video-info-selected",
      isSelectdFollow: "",
      // 作品
      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,
      //收藏
      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,
      // 关注
      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,
      //列表展示
      myWorkFalg: true,
      myLikesFalg: false,
      myFollowFalg: true
    });
    this.getListVideoList(1);
  },
  //关注
  doSelectFollow: function () {
    this.setData({
      isSelectdWork: "",
      isSelectdLike: "",
      isSelectdFollow: "video-info-selected",
      // 每一次点击都需要初始化一次
      // 作品
      myVideoList: [],
      myVideoPage: 1,
      myVideoTotal: 1,
      //收藏
      likeVideoList: [],
      likeVideoPage: 1,
      likeVideoTotal: 1,
      // 关注
      followVideoList: [],
      followVideoPage: 1,
      followVideoTotal: 1,
      //列表展示
      myWorkFalg: true,
      myLikesFalg: true,
      myFollowFalg: false
    });
  },
  // 作品查询
  getMyVideoWork(page){
    var thar = this;
    var isMe = thar.data.isMe;
    var publisherId = thar.data.publisherId;
    var user = app.getGllobalUserInfo();
    var userId = user.id;
    //判断是视频用户还是用户本身
    if(!isMe){
      userId = publisherId;
    }
    var serverUrl = app.serverUrl;
    wx.showLoading();
    wx.request({
      url: serverUrl+'/video/showAll',
      method:"POST",
      header: {
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      data:{
        userId: userId,
        page:page,
        pageSize:6
      },
      success(res){
        //隐藏加载图
        wx.hideLoading();
        var data = res.data;
        var myVideoList = data.data.rows;
        var newVideoList = thar.data.myVideoList;
        if(data.status === 200){
          thar.setData({
            myVideoPage:page,
            myVideoList:newVideoList.concat(myVideoList),
            myVideoTotal:data.data.total,
            serverUrl: serverUrl,
          })
        }
      }
    })
  },
  //收藏查询
  getListVideoList(page) {
    var thar = this;
    wx.showLoading();

    var isMe = thar.data.isMe;
    var publisherId = thar.data.publisherId;
    var user = app.getGllobalUserInfo();
    var userId = user.id;
    //判断是视频用户还是用户本身
    if (!isMe) {
      userId = publisherId;
    }
    var serverUrl = app.serverUrl;
    wx.request({
      url: serverUrl + '/video/showMyLike',
      method: "POST",
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
        "userId": user.id,
        "token": user.userToken
      },
      data: {
        userId:userId,
        page: page,
        pageSize: 6
      },
      success(res) {
        //隐藏加载图
        wx.hideLoading();
        var data = res.data;
        var likeVideoList = data.data.rows;
        var newVideoList = thar.data.likeVideoList;
        if (data.status === 200) {
          thar.setData({
            LikeVideoPage: page,
            likeVideoList: newVideoList.concat(likeVideoList),
            likeVideoTotal: data.data.total,
            serverUrl: serverUrl,
          })
        }
      }
    });
  },
  //上拉加载
  onReachBottom: function () {
     var myWorkFalg = this.data.myWorkFalg;
     var myLikesFalg = this.data.myLikesFalg;
     var myFollowFalg = this.data.MyFollowFalg;
     
     if(!myWorkFalg){
       //作品
       var currentPage = this.data.myVideoPage;
       var totalPage = this.data.myVideoTotal;
       if(currentPage === totalPage){
         wx.showToast({
           title: '已经到底了...',
           icon:"none",
           mask:true,
         });
         return
       }
       var page = currentPage+1;
       this.getMyVideoWork(page)
     }else if(!myLikesFalg){
       //收藏
       var currentPage = this.data.likeVideoPage;
       var totalPage = this.data.likeVideoTotal;
       if (currentPage === totalPage) {
         wx.showToast({
           title: '已经到底了...',
           icon: "none",
           mask: true,
         });
         return
       }
       var page = currentPage + 1;
       this.getListVideoList(page);
     }else{
      // 关注

     }
  },
  //点击作品或收藏图片则打开视频
  showVideo:function(e){
    var myWorkFalg = this.data.myWorkFalg;
    var myLikesFalg = this.data.myLikesFalg;
    var myFollowFalg = this.data.myFollowFalg;
   //判断当前是哪个列表
    if(!myWorkFalg){
      var videoList = this.data.myVideoList;
    }else if (!myLikesFalg){
      var videoList = this.data.likeVideoList;
    }else if(!myFollowFalg){
      var videoList = this.data.followVideoList;
    }
    //获取视频下标
    var arrindx = e.target.dataset.arrindex;
    //将json对象转换为字符串
    var videoInfo = JSON.stringify(videoList[arrindx]);
    //跳转视频播放页面并进行播放
    wx.redirectTo({
      url: '../videoinfo/videoinfo?videoInfo='+videoInfo,
    })
  }

})
