// components/top/top.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {

  },

  /**
   * 组件的初始数据
   */
  data: {
    width: '',
    height: ''
  },

  lifetimes:{
    attached(){
      this.setData({
        height: wx.getSystemInfoSync().windowHeight,
        width: wx.getSystemInfoSync().windowWidth
      })
    }
  }
})
