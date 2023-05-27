 import * as API_AfterSale from '../../../api/after-sale.js'
 import * as API_Order from '../../../api/order.js'
 import { Foundation } from '../../../ui-utils/index.js'

Page({
  data: {
    tabActive: 0,
    orderList:[],
    afterSaleList: [],
    finished:false,
    params: {
      page_no: 1,
      page_size: 10
    },
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false//显示返回顶部按钮
  },
  onLoad: function (options) {
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 42 })
  },
  onShow(){
    this.setData({
      orderList:[],
      afterSaleList:[],
      finished:false,
      'params.page_no':1
    })
    if(this.data.tabActive === 0){
      this.getOrderList()
    }else{
      this.getAfterSaleList()
    }
  },
  handleTitleShow(){
    wx.showModal({
      content: '当订单未确认收货|已过售后服务有效期|已申请售后服务时，不能申请售后',
      showCancel:false,
      confirmColor:'#f42424'
    })
  },
  handleApplyService(e){
    const sn = e.currentTarget.dataset.sn
    const sku_id = e.currentTarget.dataset.sku_id
    wx.navigateTo({url: '/pages/ucenter/after-sale/choose-type/choose-type?orderSn=' + sn + '&skuId=' + sku_id})
  },
  // tab切换
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index)
    this.setData({ 'params.page_no': 1, finished: false})
    if(index === 0 && index !== this.data.tabActive){
      this.setData({
        tabActive:index,
        orderList:[]
      })
      this.getOrderList()
    } else if (index === 1 && index !== this.data.tabActive){
      this.setData({
        tabActive: index,
        afterSaleList: []
      })
      this.getAfterSaleList()
    }
  },
  //获取订单列表
  getOrderList(){
    API_Order.getOrderList(this.data.params).then(response=>{
      const { data } = response
      if(data && data.length){
        this.data.orderList.push(...data)
        this.setData({orderList:this.data.orderList})
      }else{
        this.setData({finished:true})
      }
    })
  },
  //获取售后申请记录
  getAfterSaleList(){
    API_AfterSale.getAfterSaleList(this.data.params).then(response => {
      const { data } = response
      if(data && data.length){
        this.data.afterSaleList.push(...data)
        this.setData({ afterSaleList: this.data.afterSaleList })
      }else{
        this.setData({finished:true})
      }
    })
  },
  //加载更多
  loadMore: function () {
    if (!this.data.finished) {
      this.setData({'params.page_no':this.data.params.page_no += 1})
      if(this.data.tabActive === 0){
        this.getOrderList()
      }else{
        this.getAfterSaleList()
      }
    }
  },
  scroll: function (e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {
      this.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 }) }
})
