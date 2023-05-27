const app = getApp();
let util = require('../../../utils/util.js')
import * as API_Order from '../../../api/order'
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index.js'

Page({
    data: {
      orderList: [],//订单列表
      tabActive: 0,//当前tab的index
      params: {
        page_no: 1,
        page_size: 10,
        order_status: 'WAIT_COMMENT',
        audit_status:'',
        comments_type:''
      },
      scrollHeight: '',
      scrollTop: 0,//滚动高度
      finished: false,//是否已经加载完毕
      showGoTop: false,//显示返回顶部按钮
      pageCount: 0,
      msg: ''
    },
    onLoad: function (options) {
      this.setData({
        scrollHeight: wx.getSystemInfoSync().windowHeight - 42
      })
    },
    onShow(){
      this.setData({
        orderList:[],
        finished: false,
        'params.page_no':1
      })
      this.getOrderList()
    },
    //订单状态切换
    switchTab(event) {
      let index = parseInt(event.currentTarget.dataset.index);
      let status = this.getParam(index)
      if(index !== this.data.tabActive){
        this.setData({
          orderList: [],
          tabActive: index,
          finished: false,
          msg:'',
          'params.page_no': 1,
          'params.order_status': status
        })
        if(this.data.params.order_status ==='WAIT_CHASE'){
          this.setData({
            'params.audit_status':'PASS_AUDIT',
            'params.comments_type':'INITIAL'
          })
        }else{
          this.setData({
            'params.audit_status': '',
            'params.comments_type': ''
          })
        }
        this.getOrderList()
      }
    },
    //获取订单数据
    getOrderList() {
      if (this.data.params.order_status === 'WAIT_COMMENT'){
        API_Order.getOrderList(this.data.params).then(response => {
          let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
          this.setData({ pageCount: _pageCount })
          const data = response.data
          if (data && data.length) {
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
          } else {
            this.setData({ finished: true })
          }
        })
      }else{
        let params = JSON.parse(JSON.stringify(this.data.params))
        params.comment_status = params.order_status
        delete params.order_status
        API_Members.getWaitAppendComments(params).then(response => {
          let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
          this.setData({ pageCount: _pageCount })
          const { data } = response
          if (data && data.length) {
            this.data.orderList.push(...data)
            this.setData({orderList:this.data.orderList})
          } else {
            this.setData({finished:true})
          }
        })
      }
    },
    /** 规格格式化显示 */
    formatterSkuSpec(sku) {
      if (!sku.spec_list || !sku.spec_list.length) return ''
      return sku.spec_list.map(spec => spec.spec_value).join(' - ')
    },
    //根据订单状态获取tabActive
    getParam(param) {
      switch (param) {
        case 0: return 'WAIT_COMMENT'
        case 1: return 'WAIT_CHASE'
        case 2: return 'FINISHED'
        case 'WAIT_COMMENT': return 0
        case 'WAIT_CHASE': return 1
        case 'FINISHED': return 2
      }
    },
    loadMore() {
      if (!this.data.finshed) {
        this.setData({ "params.page_no": this.data.params.page_no += 1 })
        if (this.data.pageCount >= this.data.params.page_no) {
          this.getOrderList();
        } else {
          this.setData({ msg: '已经到底了~' })
        }
      }
    },
    scroll: function (e) {
      if (e.detail.scrollTop > 200) {
        this.setData({showGoTop: true })
      } else {
        this.setData({showGoTop: false})
      }
    },
    //返回顶部
    goTop: function () {this.setData({scrollTop: 0})}
 })