import * as API_Trade from '../../../api/trade'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    express:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const {logi_id,ship_no} = options
    API_Trade.getExpress(logi_id, ship_no).then(response=>{
      this.setData({express:response})
    })
  }
})