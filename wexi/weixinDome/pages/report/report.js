// pages/report/report.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    reasonType:"请选择原因",
    reportReasonArray: app.reportReasonArray,
    publishUserId:"",
    videoId:""
  },
  onLoad:function(params){
    var thar = this;
    var videoId = params.videoId;
    var publishUserId = params.publishUserId;

    thar.setData({
      publishUserId:publishUserId,
      videoId:videoId
    })
  },
  //选择滑块
   changeMe:function(e){
     var thar = this;

     var index = e.detail.value;
     var reasonType = app.reportReasonArray[index];
     thar.setData({
       reasonType: reasonType
     })
   },
   //页面提交
  submitReport:function(e){
     var thar = this;

     var reasonIndex = e.detail.value.reasonIndex;
     var reasonContent = e.detail.value.reasonContent;
     var user = app.getGllobalUserInfo();
     var currentUserId = user.id;
     //判断理由是否选择
     if(reasonIndex == null || reasonIndex == '' || reasonIndex == undefined){
       wx.showToast({
         title: '请选择举报理由',
         icon:"none",
       });
       return
     }
     var serverUrl = app.serverUrl;
     wx.request({
       url: serverUrl +'/user/reportUser',
       method:"POST",
       header: {
         "content-type": "application/json",
         "userId": user.id,
         "token": user.userToken
       },data:{
         dealUserId:thar.data.publishUserId,
         dealVideoId:thar.data.videoId,
         title:app.reportReasonArray[reasonIndex],
         content: reasonContent,
         userid: currentUserId
       },
       success(res){
         var data = res.data;
         if (data.status === 500){
           wx.showToast({
             title: data.msg,
             mask:true,
             duration:1500,
             success(){
               wx.navigateBack({
                 delta:2
               });
             }
           });
         }else{
           var value = "举报失败，请重试"
           if (data.msg != null || data.msg != ""){
             value = data.msg;
           }
           wx.showToast({
             title:value,
             image:"/icom/error.png",
             duration:1500
           })
         }

       }
     })
  }
})