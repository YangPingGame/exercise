//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    totalPage: 1,
    page: 1,
    videoList: [],
    screenWidth: 100,
    serverUrl: app.serverUrl,
    searchContent:""
  },
  onLoad: function(params) {
    //定义
    var thar = this;
    //同步获取可以获取手机信息
    var screenWidth = wx.getSystemInfoSync().screenWidth;
    thar.setData({
      screenWidth: screenWidth
    });
    //搜索内容
    var searchContent = params.search;
    //是否保存查询内容
    var isSaveRecord = params.isSaveRecord;
     if(isSaveRecord == null || isSaveRecord == '' || isSaveRecord == undefined){
       isSaveRecord =0;
     }
     //因为没有内容所以会报错
     thar.setData({
       searchContent: searchContent
     })
    //获取当前的分页数
    var page = thar.data.page;
    thar.getAllVideoList(page,isSaveRecord);
  },
  getAllVideoList: function (page, isSaveRecord) {
    var thar = this;
    wx.showLoading({
      title: '请稍等,加载中...',
      mask: true
    });
    //搜索内容
    var searchContent = thar.data.searchContent;
    var user = app.getGllobalUserInfo();
    //加载资源
    wx.request({
      url: app.serverUrl + '/video/showAll?page=' + page + '&isSaveRecord=' + isSaveRecord,
      method: "post",
      header:{
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      data: {
        videoDesc: searchContent
      },
      success(res) {
        //回调函数
        wx.hideLoading();
        wx.hideNavigationBarLoading();
        //停止其他所有动画
        wx.stopPullDownRefresh();
        var data = res.data;
        if (data.status === 200) {
          //为1则清空之前数据
          if (page === 1) {
            //清空之前的内容
            thar.setData({
              videoList: []
            });
          }
          var videoList = data.data.rows;
          var newVideoList = thar.data.videoList;
          //赋值
          thar.setData({
            videoList: newVideoList.concat(videoList),
            page: page,
            totalPage: data.data.total,
            serverUrl: app.serverUrl
          });
        } else {
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
      fail(){
        wx.hideLoading();
        wx.showToast({
          title: '连接超时',
          image:'/icom/error.png'
        });
      },
      complete(res) {
        //无论成功与否都要执行这个方法
        wx.hideLoading();
      }
    });
  },
  //下拉刷新
  onPullDownRefresh: function () {
    //小加载图标
    wx.showNavigationBarLoading();
    //后台自动根据时间获取最新数据
    this.getAllVideoList(1,0);
  },
   //上拉加载
  onReachBottom: function () {
    var thar = this;
    var currentpage = thar.data.page;
    var totalPage = thar.data.totalPage;
    //判断当前页数是否和总页数是否相等，若相等则无需查询
    if (currentpage === totalPage) {
      wx.showToast({
        title: '已经没有跟多了',
        icon: "none"
      });
      return;
    }
    //页数+1
    var page = currentpage + 1;
    thar.getAllVideoList(page,0)
  }, 
  showVideoInfo:function(e){
    var thar = this;
    var videoList = thar.data.videoList;
    var arrindex = e.target.dataset.arrindes;
    var videoInfo = JSON.stringify(videoList[arrindex]);
    wx.navigateTo({
      url: '../videoinfo/videoinfo?videoInfo='+videoInfo,
    })
  }
})