

Page({

  /**
   * 页面的初始数据
   */
  data: {
    
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },
  myRecommend(){
    wx.navigateTo({ url: '/pages/ucenter/distribution/my-recommend/my-recommend'})
  },
  myPerformance(){
    wx.navigateTo({url: '/pages/ucenter/distribution/my-performance/my-performance'})
  },
  withdrawalsSetting(){
    wx.navigateTo({ url: '/pages/ucenter/distribution/withdrawals-setting/withdrawals-setting' })
  },
  withdrawalsApply(){
    wx.navigateTo({ url: '/pages/ucenter/distribution/withdrawals-apply/withdrawals-apply' })
  },
  withdrawalsHistory(){
    wx.navigateTo({ url: '/pages/ucenter/distribution/withdrawals-history/withdrawals-history' })
  }
})