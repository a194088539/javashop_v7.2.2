import * as APT_Trade from '../../../api/trade'
import { Foundation } from '../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    goods: '',
    mobile_intro: '',
    tabIndex: 0,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const { id, owner } = options
    if(owner === 'BUYER') {
      APT_Trade.getSnapshot(id).then(response => {
        response.price = Foundation.formatPrice(response.price)
        if (response.mobile_intro) {
          this.setData({
            mobile_intro: JSON.parse(response.mobile_intro)
          })
        }
        this.setData({ goods: response })
      })
    }
  },
  handleTabs(e) {
    let index = parseInt(e.currentTarget.dataset.index)
    this.setData({ tabIndex: index })
  },
})