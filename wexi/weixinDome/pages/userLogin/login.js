const app = getApp()
Page({
  data:{
    redirectUrl:""
  },
  onLoad:function(params){
    var thar = this;
     var redirectUrl = params.redlUrl;
     if(redirectUrl != null){
       //正则表达式
       redirectUrl = redirectUrl.replace(/@/g, "?");
       redirectUrl = redirectUrl.replace(/#/g, "=");
       thar.redirectUrl = redirectUrl
     }
  },
 doLogin:function(e){
   var thar = this;
   var formObject = e.detail.value;
   var username = formObject.username;
   var password = formObject.password;
   //简单的验证
   if(username.length == 0 || password.length == 0){
     //作用类似于弹框
      wx.showToast({
        //提示内容
        title:'用户名或密码不能为空',
        //默认没有
        icon:'none',
        //显示时长
        duration:3000
      })
   } else {
     //从app.js中获取url
     var serviceUrl = app.serverUrl;
     wx.showLoading({
       title: '请稍等...',
       mask: true
     });
     wx.request({
       url:serviceUrl+'/login',
       method:'POST',
       data:{
         username:username,
         password:password
       },
       header:{
         "content-type": "application/json"
       },
       success(res){
         //回调函数
         wx.hideLoading();
         var status = res.data.status;
         if(status == 200){
            wx.showToast({
              title: '用户登录成功',
              icon:"success",
              duration:3000
            })
            //将数据赋值到全局变量中
            // app.userinfo = res.data.data;
            //fixme 修改原有的userinfo内容
           app.setGllobalUserInfo(res.data.data);
           var redirectUrl = thar.redirectUrl;
           if(redirectUrl != undefined){
             wx.redirectTo({
               url: redirectUrl,
             });
           }else{
             //登录成功所在进入的位置
             wx.navigateTo({
               url: '../list/list',
             });
           }
         }else{
           //弹框提示
           wx.showToast({
             title: res.data.msg,
             image:"/icom/error.png",
             duration:3000
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
     })
   }
 },
  goRegistPage:function(reg){
    //跳转注册页面
    wx.navigateTo({
      url: '../userRegist/regist',
    })
  },
 retrievePwd:function(){
   //弹出提示
    wx.showToast({
      title: '请联系管理员：1819014975@qq.com',
      icon:'none',
      duration:5000
    })
 }
})