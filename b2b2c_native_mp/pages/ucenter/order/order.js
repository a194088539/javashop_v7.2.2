const app = getApp();
let util = require('../../../utils/util.js') 
let middleware = require('../../../utils/middleware.js');
import * as API_ORDER from '../../../api/order'
import { Foundation } from '../../../ui-utils/index.js'

Page(middleware.identityFilter({
    data:{
      orderList: [],//订单列表
      tabActive:0,//当前tab的index
      order_sn:'',//订单号
      reason:'',//取消订单原因
      cancelOrder: true,//取消订单原因弹出框
      params:{
        page_no:1,
        page_size:10,
        order_status:''
      },
      scrollHeight:'',
      scrollTop:0,//滚动高度
      finished:false,//是否已经加载完毕
      showGoTop: false,//显示返回顶部按钮
      searchKeyword:'',//搜索关键字
    },
    onLoad: function (options) {
      this.setData({
        scrollHeight: wx.getSystemInfoSync().windowHeight - 42,
        tabActive: this.getParam(options.order_status || ''),
        'params.order_status': options.order_status || ''
      })
    },
    onShow: function () {
      this.setData({
        orderList: [],
        finished: false,
        'params.page_no': 1
      })
      this.getOrderList()
    },
    handleSearchKeyword(e){this.setData({searchKeyword:e.detail.value})},
    clearSearchKeyword(e){this.setData({searchKeyword:''})},
    //搜索我的订单
    handleSearchOrder(e){
      this.setData({
        searchKeyword: e.detail.value,
        'params.key_words': e.detail.value,
        'params.page_no':1,
        finished:false,
        orderList:[]
      })
      this.getOrderList()
    },
    //获取订单数据
    getOrderList(){
      API_ORDER.getOrderList(this.data.params).then(response => {
        const data = response.data
        if(data && data.length){
          data.forEach(key => {
            //格式化价格
            key.order_amount = Foundation.formatPrice(key.order_amount)
            if (key.sku_list && key.sku_list.length) {
              key.sku_list.forEach(item => {
                if (!item.skuName) item.skuName = this.formatterSkuSpec(item)
              })
            }
          })
          this.data.orderList.push(...data)
          this.setData({orderList: this.data.orderList})
        }else{
          this.setData({finished:true}) 
        }
      })
    },
    /** 规格格式化显示 */
    formatterSkuSpec(sku) {
      if (!sku.spec_list || !sku.spec_list.length) return ''
      return sku.spec_list.map(spec => spec.spec_value).join(' - ')
    },
    //订单状态切换
    switchTab(event) {
      let that = this
      let index = parseInt(event.currentTarget.dataset.index);
      let status = this.getParam(index)
      if(index !== this.data.tabActive){
        that.setData({
          orderList: [],
          tabActive: index,
          finished: false,
          'params.page_no': 1,
          'params.order_status': status
        })
        that.getOrderList()
      }
    },
    //根据订单状态获取tabActive
    getParam(param){
      switch (param) {
        case 0: return ''
        case 1: return 'WAIT_PAY'
        case 2: return 'WAIT_SHIP'
        case 3: return 'WAIT_ROG'
        case 4: return 'WAIT_COMMENT'
        case '': return 0
        case 'WAIT_PAY': return 1
        case 'WAIT_SHIP': return 2
        case 'WAIT_ROG': return 3
        case 'WAIT_COMMENT': return 4
      }
    },
    //取消订单
    cancelOrder:function(e){
      let that = this
      this.setData({
        cancelOrder: false,
        reason:'',
        order_sn: e.currentTarget.dataset.sn
      })
    },
    //取消原因
    bindinput: function (e) {
      this.setData({
        reason: e.detail.value
      })
    },
    //取消
    cloneDialog: function () {
      this.setData({cancelOrder: true})
    },
    //确认取消订单
    confirmCancelOrder: util.throttle(function(){
      let that = this
      let reason = that.data.reason
      let cancel_sn = that.data.order_sn
      if (!reason) {
        wx.showToast({
          title: '请填写取消原因!',
          icon: 'none',
          duration: 1000
        })
        this.setData({
          cancelOrder: false
        })
      } else if (reason.length > 200) {
        wx.showToast({
          title: '最多输入200个字符！',
          icon: 'none',
          duration: 1000,
        })
        this.setData({
          cancelOrder: false
        })
      } else {
        API_ORDER.cancelOrder(cancel_sn, reason).then(() => {
          that.setData({
            orderList: [],
            'params.page_no': 1,
            cancelOrder: true
          })
          that.getOrderList()
          wx.showToast({ title: '订单取消成功！' })
        })
      }
    }),
    rogOrder:function(e){
      let that = this
      let order_sn = e.currentTarget.dataset.sn
      wx.showModal({
        title: '提示',
        content: '请确认是否收到货物，否则可能会钱财两空！',
        confirmColor:'#f42424',
        success: function (res) {
          if (res.confirm) {
            API_ORDER.confirmReceipt(order_sn).then(() => {
              that.setData({
                orderList: [],
                'params.page_no': 1
              })
              that.getOrderList()
              wx.showToast({ title: '确认收货成功！' })
            })
          }
        }
      })
    },
    loadMore: function () {
      if (!this.data.finished) {
        this.setData({ "params.page_no": this.data.params.page_no += 1 })
        this.getOrderList()
      }
    },
    scroll: function (e) {
      let that = this
      if (e.detail.scrollTop > 200) {
        that.setData({showGoTop: true})
      } else {
        that.setData({showGoTop: false})
      }
    },
    //返回顶部
    goTop: function () {this.setData({scrollTop: 0})}
  })
)