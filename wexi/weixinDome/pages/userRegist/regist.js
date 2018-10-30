const app = getApp()
Page({
  data:{

  },
 doRegist:function(e){
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
     wx.request({
       url:serviceUrl+'/regist',
       method:'POST',
       data:{
         username:username,
         password:password
       },
       header:{
         "content-type":"application/json"
       },
       success(res){
         //回调函数
         var status = res.data.status;
         if(status === 200){
            wx.showToast({
              title: '用户注册成功',
              icon:"none",
              duration:3000
            })
           app.setGllobalUserInfo(res.data.data);
           wx.navigateTo({
             url: '../mine/mine',
           });
         }else{
           //弹框提示
           wx.showToast({
             title: res.data.msg,
             icon: 'none',
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
 goLoginPage:function(){
   //访问层数不能太多有上限
   //返回上一级
  wx.navigateBack({
    
  })
 }
})