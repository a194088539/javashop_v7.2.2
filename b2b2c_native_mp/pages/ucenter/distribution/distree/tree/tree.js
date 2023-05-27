Component({
  /**
   * 组件的属性列表
   */
  properties: {
    model:Object,
    num:String,
    nodes:String,
    root:String
  },
  methods: {
    openExpand(m) {
      this.triggerEvent("openExpand", m.currentTarget.dataset)
    },
    delAction(m) {
      this.triggerEvent("delAction", m.currentTarget.dataset);
    },
    handlelookDetails(m) {
      wx.navigateTo({
        url: '/pages/ucenter/distribution/my-performance/my-performance?member_id=' + m.currentTarget.dataset.model.id,
      })
    }
  }
})