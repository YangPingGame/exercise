//app.js
App({
  /*网络访问方式
  同一局域网可以使用本机ip
  不同局域网可以使用穿透*/
  // serverUrl: "http://localhost:8081",
  serverUrl:"http://jucta5.natappfree.cc",
  userInfo: null,
  setGllobalUserInfo:function(user){
    //设置本地缓存
     wx.setStorageSync("userInfo",user)
  },
  getGllobalUserInfo:function(){
    //获取本地缓存内容
    return wx.getStorageSync("userInfo")
  },
  removeUserInfo:function(){
    wx.removeStorageSync("userInfo")
  },
  //举报类型数组
  reportReasonArray:[
     "色情低俗",
     "政治敏感",
     "涉嫌诈骗",
    "辱骂谩骂",
    "广告垃圾",
    "诱导分享",
    "引人不适",
    "过于暴力",
    "违法违纪",
    "其他原因",
  ]
})