var videoutils = require("../../utils/video-utils.js")
const app = getApp()
Page({
  /**
   * 页面的初始数据
   */
  data: {
    filValue: "cover",
    videoId: "",
    src: "",
    videoInfo: {},
    userLikeVideo: false,
    faceImage: "../resource/images/mine.png",
    //是否关注过
    isKeep: false,

    commentsPage:1,
    commentsTotalPate:1,
    commentsList:[],
    
    placeholder:"说点什么..."
  },
  videoCtx: {},
  //页面加载事件
  onLoad: function(params) {
    var thar = this;
    //上下文对象
    thar.videoCtx = wx.createVideoContext("myVideo", this);
    //获取传输过来的数据
    var videoInfo = JSON.parse(params.videoInfo);
    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;

    var serviceUrl = app.serverUrl;
    var user = app.getGllobalUserInfo();
    var cover = "cover";
    if (width > height) {
      cover = "";
    }
    thar.setData({
      videoInfo: videoInfo,
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      filValue: cover,
    });

    var loginUserId = "";
    if (user != null && user != undefined && user != '') {
      loginUserId = user.id;
    }
    wx.request({
      url: serviceUrl + '/user/queryPublisher',
      method: "POST",
      data: {
        loginUserId: loginUserId,
        videoId: videoInfo.id,
        publishUserId: videoInfo.userId
      },
      header: {
        "Content-Type": "application/x-www-form-urlencoded",
        "userId": user.id,
        "token": user.userToken
      },
      success(res) {
        var data = res.data;
        if (data.status === 200) {
          var publlisher = data.data.usersinfoVo;
          //是否关注
          var userLikeVideo = data.data.userLikeVideo;
          var isKeep = data.data.keep;
          //将是否点赞和头像赋值
          thar.setData({
            serverUrl: serviceUrl,
            faceImage: publlisher.faceImage,
            userLikeVideo: userLikeVideo,
            isKeep: isKeep
          });
        } else {
          wx.showModal({
            title: '用户提示',
            content: data.msg,
            showCancel: false
          })
        }
      }
    });
    //留言分页查询
    thar.getCommentsList(1);
  },
  //点击头像进入个人信息页面
  showPublisher: function() {
    var thar = this;
    var user = app.getGllobalUserInfo();
    var videoInfo = thar.data.videoInfo;
    //配置重定向@代表？，#代表=
    var realUrl = "../videoinfo/videoinfo@publisherId#" + videoInfo.userId;
    if (user == null || user == undefined || user == '') {
      //跳转登录页面
      wx.navigateTo({
        url: '../userLogin/login?redlUrl=' + realUrl,
      });
    } else {
      //成功查看信息
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + videoInfo.userId,
      });
    }
  },
  //页面显示时调用
  onShow: function() {
    var thar = this;
    //视频播放
    thar.videoCtx.play();
  },
  //页面隐藏式调用
  onHide: function() {
    var thar = this;
    //视频暂停
    thar.videoCtx.pause();
  },
  showSearch: function() {
    wx.navigateTo({
      url: '../serochVideo/serochVideo',
    });
  },
  //点击上传按钮
  upload: function() {
    var thar = this;
    //上传跳转，若未登录则拦截
    var user = app.getGllobalUserInfo();
    var videoInfo = JSON.stringify(thar.data.videoInfo);
    //配置重定向@代表？，#代表=
    var realUrl = "../videoinfo/videoinfo@videoInfo#" + videoInfo;
    if (user == null || user == undefined || user == '') {
      //跳转登录页面
      wx.navigateTo({
        url: '../userLogin/login?redlUrl=' + realUrl,
      });
    } else {
      videoutils.uploadVideo();
    }
  },
  //跳转首页
  showIndex: function() {
    //跳转回列表页面
    wx.navigateTo({
      url: '../list/list',
    });
  },
  //转入个人信息页面
  showMine: function() {
    //用户信息页面跳转，若未登录则拦截
    var user = app.getGllobalUserInfo();
    if (user == null || user == undefined || user == '') {
      //跳转登录页面
      wx.navigateTo({
        url: '../userLogin/login',
      });
    }
    //登录过才跳转信息页面
    wx.navigateTo({
      url: '../mine/mine',
    });
  },
  //点赞
  showVideoOrNot: function() {
    var thar = this;
    var serviceUrl = app.serverUrl;
    var videoInfo = thar.data.videoInfo;
    var user = app.getGllobalUserInfo();
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/Login',
      });
    } else {
      var userLikeVideo = thar.data.userLikeVideo;
      var url = '/video/userLike';
      if (userLikeVideo) {
        var url = '/video/userUnLike';
      }
      wx.showLoading({
        title: '请等待...',
      });
      wx.request({
        url: serviceUrl + url,
        method: "POST",
        data: {
          userId: user.id,
          videoId: videoInfo.id,
          videoCreaterId: videoInfo.userId
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded',
          "userId": user.id,
          "token": user.userToken
        },
        success(res) {
          var data = res.data;
          wx.hideLoading();
          if (data.status === 200) {
            thar.setData({
              userLikeVideo: !userLikeVideo
            })
          } else if (data.status === 502) {

          }
        }
      })
    }
  },
  //添加关注
  showKeep: function(e) {
    //判断当前是否是关注过
    var thar = this;
    var serviceUrl = app.serverUrl;
    var videoInfo = thar.data.videoInfo;
    var user = app.getGllobalUserInfo();
    if (user == null || user == undefined || user == '') {
      //登录失效或未登录
      wx.navigateTo({
        url: '../userLogin/Login',
      });
    } else {
      var isKeep = thar.data.isKeep;
      var url = '/video/userKeep';
      if (isKeep) {
        var url = '/video/userUnKeep';
      }
      wx.showLoading({
        title: '请等待...',
        mask: true,
      });
      wx.request({
        url: serviceUrl + url,
        method: "POST",
        data: {
          userId: user.id,
          videoId: videoInfo.id,
          videoCreaterId: videoInfo.userId,
          type: e.target.dataset.keep === "0" ? "" : 1
        },
        header: {
          'content-type': 'application/x-www-form-urlencoded',
          "userId": user.id,
          "token": user.userToken
        },
        success(res) {
          var data = res.data;
          wx.hideLoading();
          if (data.status === 200) {
            thar.setData({
              isKeep: !isKeep
            })
          } else {
            videoutils.prompt(data.data.msg);
          }
        }
      })
    }
  },
  //用户分享按钮
  shareMe: function() {
    var thar = this;
    var user = app.getGllobalUserInfo();
    wx.showActionSheet({
      itemList: ['下载到本地', '举报用户', '分享到朋友圈', '分享到QQ空间', '分享到微博'],
      success(res) {
        if (res.tapIndex === 0) {
          wx.showLoading({
            title: '下载中',
          })
          //下载api
          wx.downloadFile({
            url: app.serverUrl + thar.data.videoInfo.videoPath,
            success(res) {
              if (res.statusCode === 200) {
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success(res) {
                    wx.hideLoading();
                  }
                })
              }
            }
          });
        } else if (res.tapIndex === 1) {
          var videoInfo = JSON.stringify(thar.data.videoInfo);
          var realurl = "../videoinfo/videoinfo@videoInfo#" + videoInfo;
          if (user == null && user == undefined && user == "") {
            wx.navigateTo({
              url: '../userLogin/login?redirectUrl=' + realurl,
            })
          }
          //发布者ID
          var publishUserId = thar.data.videoInfo.userId;
          //视频id
          var videoId = thar.data.videoInfo.id;
          //用户ID
          var currentuserId = user.id;
          wx.navigateTo({
            url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
          });
          //举报
        } else {
          wx.showToast({
            title: '官方暂时还未开放此功能',
            icon: "none",
            mask: true
          })
        }
      }
    })
  },
  //分享按钮
  onShareAppMessage: function(res) {
    var thar = this;

    var videoInfo = thar.data.videoInfo;
    var video = JSON.stringify(videoInfo);
    return {
      title: "短视频内容分享",
      path: "pages/videoinfo/videoinfo?videoInfo =" + video,
    }
  },
  //点击留言获取input焦点
  leaveComment: function() {
    //设置输入框的焦点
    this.setData({
      commentFocus: true
    });
  },
  //留言回复功能
  replyFocus:function(e){
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickName = e.currentTarget.dataset.tonickname;
    this.setData({
      placeholder:"回复 "+toNickName,
      replyFatherCommentId:fatherCommentId,
      replyToUserId:toUserId,
      commentFocus:true,
    })
  },
  saveComment: function(e) {
    var thar = this;
    var content = e.detail.value;
    
    wx.showLoading({
      title: '发表中,请稍等...',
      mask:true
    });
    //获取评论回复的fatherCommentId 和 toUserId
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
    var toUserId = e.currentTarget.dataset.replytouserid;


    var user = app.getGllobalUserInfo();
    var videoInfo = JSON.stringify(thar.data.videoInfo);
    var realUrl = '../videoinfo/videoinfo@videoInfo#'+videoInfo;
    if(user == null || user == undefined || user ==''){
      //登录拦截
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl ='+realUrl,
      });
    }else{
      //请求链接后面的参数和data参数内容会被对应的实体内的属性接收
     wx.request({
       url: app.serverUrl + '/video/saveComment?fatherCommentId=' + fatherCommentId +"&toUserId="+toUserId,
       method:"POST",
       header: {
         "content-type": "application/json",
         "userId": user.id,
         "token": user.userToken
       },
       data:{
          fromUserId:user.id,
          videoId:thar.data.videoInfo.id,
          comment:content,
       },
       success(res){
         wx.hideLoading();
        //清空输入的内容
         thar.setData({
           contentValue:"",
           commentsList: []
         });
        thar.getCommentsList(1);
       }
     })
    }
  },
  getCommentsList:function(page){
     var thar = this;
     var videoId = thar.data.videoInfo.id;
     var user = app.getGllobalUserInfo();
     wx.request({
       url: app.serverUrl+"/video/getVideoComments",
       method:"POST",
       data:{
         videoId:videoId,
         page:page,
         pageSize:5,
       },
       header:{
         'content-type': 'application/x-www-form-urlencoded',
         "userId": user.id,
         "token": user.userToken
       },
       success(res){
         console.log(res.data);
         var commentsList = res.data.data.rows;
         var newCommentsList = thar.data.commentsList;
         thar.setData({
           commentsList: newCommentsList.concat(commentsList),
           commentsPage:page,
           commentsTotalPage:res.data.data
         });
       }
     });
  },
  onReachBottom:function(){
    var thar = this;
    var currentPage = thar.data.commentsPage;
    var totalPage =thar.data.commentsTotalPage;
    if(currentPage === totalPage){
      return;
    }
    var page = currentPage +1;
    thar.getCommentsList(page);
  }
})