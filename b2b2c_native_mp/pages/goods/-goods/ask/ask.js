import * as API_Members from '../../../../api/members'
import { Foundation } from '../../../../ui-utils/index'
Component({
  properties: {
    goodsId: String,
    selectedSku:Object
  },
  data: {
    finished: false,
    params: {
      page_no: 1,
      page_size: 10
    },
    asks: [],
  },
  lifetimes: {
    ready() {
      this.getAsks()
    }
  },
  methods: {
    handleAskList(){
      wx.navigateTo({
        url: '/pages/goods/goods-ask-list/goods-ask-list?goods_id='+ this.data.goodsId,
      })
    },
    // 获取商品资询
    getAsks(){
      API_Members.getGoodsConsultations(this.data.goodsId, this.data.params).then(response => {
        const { data } = response
        if (data && data.length) {
          data.map(key => {
            key.create_time = Foundation.unixToDate(key.create_time, 'yyyy-MM-dd')
          })
          this.data.asks.push(...data)
          this.setData({ asks: this.data.asks })
        } else {
          this.setData({ finished: true })
        }
      })
    },
  }
})