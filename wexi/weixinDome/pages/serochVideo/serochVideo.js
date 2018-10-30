var WxSearch = require('../../wxSearchView/wxSearchView.js');
//获取应用实例
const app = getApp()
Page({

  data: {},
  onLoad: function () {
    // 2 搜索栏初始化
    var that = this;
    var serverUrl = app.serverUrl;
    var user = app.getGllobalUserInfo();
    wx.request({
      url: serverUrl+'/video/hot',
      method: "POST",
      header: {
        "content-type": "application/json",
        "userId": user.id,
        "token": user.userToken
      },
      success(res){
        var hotList = res.data.data;
        WxSearch.init(
          that,  // 本页面一个引用
          hotList, // 热点搜索推荐，[]表示不使用
          hotList,// 搜索匹配，[]表示不使用
          that.mySearchFunction, // 提供一个搜索回调函数
          that.myGobackFunction //提供一个返回回调函数
        );
      }
    });
  },
  // 3 转发函数，固定部分，直接拷贝即可
  wxSearchInput: WxSearch.wxSearchInput,  // 输入变化时的操作
  wxSearchKeyTap: WxSearch.wxSearchKeyTap,  // 点击提示或者关键字、历史记录时的操作
  wxSearchDeleteAll: WxSearch.wxSearchDeleteAll, // 删除所有的历史记录
  wxSearchConfirm: WxSearch.wxSearchConfirm,  // 搜索函数
  wxSearchClear: WxSearch.wxSearchClear,  // 清空函数

  // 4 搜索回调函数  
  mySearchFunction: function (value) {
    // do your job here
    // 示例：跳转
    wx.redirectTo({
      url: '../list/list?isSaveRecord=1&search=' + value
    })
  },

  // 5 返回回调函数
  myGobackFunction: function () {
    // do your job here
    // 示例：返回
    wx.navigateBack({
      delta:1
    });
  }

})