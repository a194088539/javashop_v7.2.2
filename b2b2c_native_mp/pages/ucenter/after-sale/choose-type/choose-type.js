import * as API_AfterSale from '../../../../api/after-sale.js'
import { Foundation } from '../../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    order_sn:'',
    sku_id:'',
    applyInfo:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      order_sn:options.orderSn,
      sku_id:options.skuId
    })
    this.getApplyInfo()
  },
  getApplyInfo(){
    API_AfterSale.getApplyInfo(this.data.order_sn,this.data.sku_id).then(response=>{
      this.setData({applyInfo:response})
    })
  },
  handleApplyService(e){
    const type = e.currentTarget.dataset.type
    wx.navigateTo({url: '/pages/ucenter/after-sale/apply/apply?orderSn=' + this.data.order_sn + '&skuId=' + this.data.sku_id + '&serviceType=' + type})
  }
})