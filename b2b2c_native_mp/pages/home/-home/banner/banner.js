Component({
  properties: {
    banner:Array
  },
  methods: {
    handleToPage(e){
      const { operation_type ,operation_param } = e.currentTarget.dataset.focus
      switch (operation_type){
        // 链接地址
        case 'URL':wx.navigateTo({url: `/pages${operation_param}${operation_param}`})
          break
        // 商品
        case 'GOODS': wx.navigateTo({ url: `/pages/goods/goods?goods_id=${operation_param}` })
          break
        // 关键字
        case 'KEYWORD': wx.navigateTo({ url: `/pages/category/category?keyword=${operation_param}` })
          break
        // 店铺
        case 'SHOP': wx.navigateTo({ url: `/pages/shop/shop_id/shop_id?id=${operation_param}` })
          break
        // 分类
        case 'CATEGORY': wx.navigateTo({ url: `/pages/category/category?category=${operation_param}` })
          break
        default: wx.reLaunch({ url: '/pages/home/home' })
      }
    }
  }
})
