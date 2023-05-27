import * as API_Promotions from '../../../../api/promotions.js'
import { Foundation } from '../../../../ui-utils/index'
Component({
  properties: {
    goodsId: {
      type:String,
      observer(newVal){
        if(newVal){this.getAssembleInfo()}
      }
    },
    skuId: String
  },
  data: {
    assembleOrder: [],//待成团订单
    assembleOrderAll: [],//查看更多待成团订单
  },
  methods: {
    switchCommentsPop() {
      this.selectComponent('#bottomFrame').showFrame()
    },
    handleCommentsClose() {
      this.selectComponent('#bottomFrame').showFrame()
    },
    // 获取拼团信息 获取此商品所有待成团的订单
    getAssembleInfo(){
      API_Promotions.getAssembleOrderList(this.data.goodsId,{sku_id:this.data.skuId}).then(response=>{
        const orderList = response.map(key=>{
          return{
            left_time: key.left_time,
            offered_num: key.offered_num,
            required_num: key.required_num,
            face: key.participants.map(item=>{
              if(item.is_master === 1){
                return item.face
              }
            })[0],
            order_id:key.order_id,
            name: key.participants.map(item => {
              if (item.is_master === 1) {
                return item.name
              }
            })[0]
          }
        })
        if(orderList&&orderList.length){
          for(let i=0,len=orderList.length;i<len;i+=2){
            this.data.assembleOrder.push(orderList.slice(i,i+2))
          }
        }
        this.setData({ assembleOrderAll: orderList ,assembleOrder:this.data.assembleOrder})
      })
    },
    // 去拼团
    toBuyNow(e){
      const order = e.currentTarget.dataset.order
      this.triggerEvent('to-assemble-buy-now', order)
    }
  }
})