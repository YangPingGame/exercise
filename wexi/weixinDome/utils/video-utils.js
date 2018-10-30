
 function uploadVideo(){
  var thar = this;
  //视频上传
  wx.chooseVideo({
    sourceType: ['album', 'camera'],//拍摄相册选择都可以
    maxDuration: 60,//最大时长
    compressed:true,//视频经过压缩
    camera: 'back',
    success(res) {
      console.log(res)
      //视频时长
      var duration = res.duration;
      //视频长宽
      var tmpHeight = res.height;
      var tmpWidth = res.width;
      //临时视频路径
      var tmpVideoUrl = res.tempFilePath;
      //临时首页
      var tmpCoverUrl = res.thumbTempFilePath;
      if(duration >61){
        wx.showToast({
          title: '视频长度不能超过60秒...',
          icon:"none",
          duration:2500
        });
      }else if(duration < 5){
        wx.showToast({
          title: '视频长度不能少于5秒...',
          icon: "none",
          duration: 2500
        });
      }else{
        //上传视频
        wx.navigateTo({
          url: '../chooseBgm/chooseBgm?duration=' + duration
            + '&tmpHeight=' + tmpHeight + '&tmpWidth=' + tmpWidth
            + '&tmpVideoUrl=' + tmpVideoUrl + '&tmpCoverUrl='+tmpCoverUrl
        });
      }
    }
  })
}
// 弹框提示，并且延迟提交
function prompt(msg){
  wx.showToast({
    title: msg,
    mask: true,
    duration: 2000
  })
}
module.exports = {
  uploadVideo: uploadVideo
}