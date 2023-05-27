import { Foundation } from '../../../../ui-utils/index'
Component({
  properties: {
    promotions: {
      type: Object,
      observer: function(val) {
        if (!val || !val.length) return
        let prom = val.filter(item => item.exchange)
        if (prom && prom[0]){
          prom[0].exchange.exchange_money = Foundation.formatPrice(prom[0].exchange.exchange_money)
          prom[0].exchange.goods_price = Foundation.formatPrice(prom[0].exchange.goods_price)
          this.setData({ prom: prom[0].exchange })
        }
      }
    }
  },
  data: {
    prom: ''
  }
})