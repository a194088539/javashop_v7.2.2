
Component({
  properties: {
    site_name: String,
    opacity:Number
  },
  methods: {
    handlePersonal(){
      wx.switchTab({url: '/pages/ucenter/index/index'})
    }
  }
})
