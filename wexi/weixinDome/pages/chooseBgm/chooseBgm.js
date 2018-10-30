//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    //定义空的集合
    serverUrl: app.serverUrl,
    bgmList:[],
    videoParams:{}
  },
  onLoad: function (params) {
    //首次加载查询
    var thar = this;
    thar.setData({
      videoParams:params
    })
    var serviceUrl = app.serverUrl;
    //获取缓存内容
    var user = app.getGllobalUserInfo();
    wx.showLoading({
      title: '请稍等...',
      mask:true
    })
    wx.request({
      url: serviceUrl + '/bgm/list',
      method: "POST",
      header: {
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      //请求成功
      success(res) {
        wx.hideLoading();
        var data =res.data;
        if (data.status === 200) {
          var bgmList = data.data;
          //设置歌曲列表
          thar.setData({
            bgmList:bgmList
          });
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
      //请求失败
      fail() {
        wx.hideLoading();
        wx.showToast({
          title: '服务发生错误。',
          image: '/icom/error.png'
        });
      },
      complete(res) {
        //无论成功与否都要执行这个方法
        wx.hideLoading();
      }
    });
  },
  upload:function(e){
    var thar = this;
    //背景音乐id
    var bgmId = e.detail.value.bgmId;
    //使用描述
    var desc = e.detail.value.desc;
    //视频的标准数据
    var duration = thar.data.videoParams.duration;
    var tmpHeight = thar.data.videoParams.tmpHeight;
    var tmpWidth = thar.data.videoParams.tmpWidth;
    var tmpVideoUrl = thar.data.videoParams.tmpVideoUrl;
    var tmpCoverUrl = thar.data.videoParams.tmpCoverUrl;

    //将视频上传
    var user = app.getGllobalUserInfo();
    //上传地址
    var serviceUrl = app.serverUrl;
    //上传动画
    wx.showLoading({
      title: '请稍等...',
      mask: true
    });
    //视频上传
    wx.uploadFile({
      url: serviceUrl + '/video/upload',
      formData:{
        userId: user.id,
        bgmId:bgmId,
        desc:desc,
        videoSeconds:duration,
        videoHeight:tmpHeight,
        videoWidth:tmpWidth
      },
      filePath: tmpVideoUrl,
      name: 'file',
      header: {
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      success(res){
        var data = JSON.parse(res.data);
        //隐藏加载动画
        //上传封面
        if(data.status == 200){
          wx.showToast({
            title: '上传成功',
            icon:'success',
            mask:true
          });
          wx.navigateBack({
            delta:1
          });
          //真机调试时用不到，所以可以不写
          // var videoId = data.data;
          // wx.uploadFile({
          //   url: serviceUrl + '/user/uploadCover',
          //   formData: {
          //     userId: app.userinfo.id,
          //     videoId: videoId
          //   },
          //   filePath: tmpCoverUrl,
          //   name: 'file',
          //   header: {
          //     'content-type': 'application/json'
          //   },
          //   success(res) {
          //     var data = JSON.parse(res.data);
          //     //隐藏加载动画
          //     wx.hideLoading();
          //     if (data.status == 200) {
          //       wx.showToast({
          //         title: '上传成功！',
          //       });
          //       //返回上一层页面
          //       wx.navigateBack({
          //         delta:1
          //       });
          //     } else {
          //       wx.showToast({
          //         title: '上传失败！',
          //       })
          //     }
          //   }
          // });
        } else {
          //关闭加载动画
          wx.hideLoading();
          wx.showToast({
            title: '上传失败！',
            image:"/icom/error.png"
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
  }
})
