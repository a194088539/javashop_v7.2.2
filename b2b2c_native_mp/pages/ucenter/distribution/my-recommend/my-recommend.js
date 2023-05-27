import * as API_Distribution from '../../../../api/distribution.js'
import { Foundation } from '../../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    myRefereer: '',
    referee: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getMyReferee()
    this.getReferee()
  },
  getMyReferee(){
    API_Distribution.getMyRefereer().then(response=>{
      this.setData({myRefereer:response.message})
    })
  },
  getReferee(){
    API_Distribution.getRefereeList().then(response=>{
      response.forEach(key=>{
        key.rebate_total = Foundation.formatPrice(key.rebate_total)
        if(key.item){
          key.item.forEach(item=>{
            item.rebate_total = Foundation.formatPrice(item.rebate_total)
          })
        }
      })
      this.setData({referee:response})
    })
  }
})