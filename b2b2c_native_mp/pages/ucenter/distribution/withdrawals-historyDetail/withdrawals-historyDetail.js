Page({
  /**
   * 页面的初始数据
   */
  data: {
    currentRow:{}
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      currentRow: JSON.parse(options.item) 
    })
  }
})