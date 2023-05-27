import * as API_Promotions from '../../../../api/promotions'
import { Foundation } from '../../../../ui-utils/index'

Component({
  properties: {
    promotions:{
      type:Object,
      observer: function(val) {
        if (!val || !val.length) return
        const prom = val.filter(item => item.groupbuy_goods_vo || item.seckill_goods_vo)
        const _prom = prom.filter(key => key.sku_id === this.data.selectedSku.sku_id)
        this.handPromotion(_prom)
      }
    },
    selectedSku: {
      type: Object,
      observer: function (val) {
        if (!this.data.promotions || !this.data.promotions.length || !val) return
        const prom = this.data.promotions.filter(item => item.groupbuy_goods_vo || item.seckill_goods_vo)
        const _prom = prom.filter(key => key.sku_id === val.sku_id)
        this.handPromotion(_prom)
      }
    }
  },
  data: {
    promotion: {},
    showPromotion: true
  },
  methods: {
    handleCountEnd() {
      this.setData({showPromotion : false})
      wx.showToast({title: '活动已结束，商品已恢复原价。',icon:"none"})
    },
    handPromotion(val) {
      if (val && val[0]) {
        if (val[0].promotion_type === 'GROUPBUY') {
          this.setData({ showPromotion: true })
          val[0].groupbuy_goods_vo.price = Foundation.formatPrice(val[0].groupbuy_goods_vo.price)
          val[0].groupbuy_goods_vo.original_price = Foundation.formatPrice(val[0].groupbuy_goods_vo.original_price)
        } else {
          if (val[0].seckill_goods_vo.distance_start_time < 0) {
            this.setData({ showPromotion: false })
          } else {
            this.setData({ showPromotion: true })
            val[0].seckill_goods_vo.seckill_price = Foundation.formatPrice(val[0].seckill_goods_vo.seckill_price)
            val[0].seckill_goods_vo.original_price = Foundation.formatPrice(val[0].seckill_goods_vo.original_price)
          }
        }
        this.setData({ promotion: val[0] })
      }
      if (!val || !val[0]) {
        this.setData({ showPromotion: false })
      }
    }
  }
})