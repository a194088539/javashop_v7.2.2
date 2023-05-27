// pages/home/-home/menu/menu.js
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    menus:{
      type:Array,
      observer: function(val) {
        val.forEach(item => {
          if(/^\/goods\/.*/.test(item.url)){
            item.url = item.url.replace('/goods/','/pages/goods/goods?goods_id=')
          } else if(/^\/shop\/.*/.test(item.url)){
            item.url = item.url.replace('/shop/','/pages/shop/shop_id/shop_id?id=')
          } else if(/^\/goods\?.*/.test(item.url)){
            item.url = item.url.replace('/goods','/pages/category/category')
          } else {
            item.url = `/pages${item.url}${item.url}`
          }
        })
        this.setData({newMenus: val})
      }
    }
  },
  data() {
    newMenus:''
  }
})
