import * as API_Shop from '../../../../api/shop'
Component({
  properties: {
    shopId:String,
  },
  data: {
    shop:''
  },
  // 生命周期
  lifetimes: {
    ready() {
      // 获取商品店铺信息
      if(this.data.shopId){
        API_Shop.getShopBaseInfo(this.data.shopId).then(response => {
          this.setData({ shop: response })
        })
      }
    }
  },
  observers: {
    shopId() {
      if (this.data.shopId) {
        // 获取店铺优惠券
        API_Shop.getShopBaseInfo(this.data.shopId).then(response => {
          this.setData({ shop: response })
        })
      }
    }
  },
  methods: {
  
  }
})