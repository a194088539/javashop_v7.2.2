/**
 * 结算页
 * 购物车清单组件
 */
Component({
  properties: {
    inventories: Array
  },
  data: {
    surActsShop: ''
  },
  methods: {
    handleShowActs(e) {
      this.selectComponent('#bottomFrame').showFrame()
      this.setData({surActsShop: e.currentTarget.dataset.shop})
    }
  }
})
