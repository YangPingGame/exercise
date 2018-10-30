//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    danmuList: [
      {
        text: '第 1s 出现的弹幕',
        color: '#ff0000',
        time: 12
      },
      {
        text: '第 3s 出现的弹幕',
        color: '#ff00ff',
        time: 15
      }]
  }
})