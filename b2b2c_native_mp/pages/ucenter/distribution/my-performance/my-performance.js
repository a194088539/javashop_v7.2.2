import * as API_Distribution from '../../../../api/distribution.js'
import { Foundation } from '../../../../ui-utils/index.js'

Page({
  /**
   * 页面的初始数据
   */
  data: {
    member_id: 0,
    active: 0,
    // 我的结算单
    settlementTotal: {
      push_money: 0,
      final_money: 0,
      return_order_money: 0
    },
    //分页请求参数 
    params: {
      page_no: 1,
      page_size: 10
    },
    //分页信息
    pageData: {
      page_no: 1,
      page_size: 10,
      data_total: 0
    },
    //业绩列表
    performanceList: [],
    //导航
    performance: [
      { title: '与我相关的订单' },
      { title: '与我相关的退货单' },
      { title: '我的历史业绩' }
    ],
    //结算单id
    billId: '',
    //当前折叠面板名称
    showactiveNames: false,
    //当前面板对应的文本名称
    sn_text: '订单编号'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({ member_id: options.member_id })
    this.getSettlementTotal()
  },
  isshowactiveNames(e) {
    const index = e.currentTarget.dataset.index
    this.data.performanceList[index].showactiveNames = !this.data.performanceList[index].showactiveNames
    this.setData({ performanceList: this.data.performanceList })
  },
  getSettlementTotal() {
    API_Distribution.getSettlementTotal({ member_id: this.data.member_id || 0}).then(response => {
      response.final_money = Foundation.formatPrice(response.final_money)
      response.push_money = Foundation.formatPrice(response.push_money)
      response.return_push_money = Foundation.formatPrice(response.return_push_money)
      this.setData({
        settlementTotal: response,
        billId: response.total_id,
        params: {
          ...this.data.params,
          member_id: response.member_id,
          bill_id: response.id
        }
      })
      this.getRelevantList()
    })
  },
  tabChange(e) {
    const index = parseInt(e.currentTarget.dataset.index)
    this.setData({ active: index })
    switch (index) {
      case 0: this.getRelevantList()
        break
      case 1: this.getRelevantRefundList()
        break
      case 2: this.getMyHistoryList()
        break
    }
  },
  //获取与我相关的订单记录
  getRelevantList() {
    API_Distribution.getRelevantList(this.data.params).then(response => {
      this.data.pageData = {
        page_no: response.page_no,
        page_size: response.page_size,
        data_total: response.data_total
      }
      response.data.forEach(key => {
        key.showactiveNames = false
        if (key.order_price) { key.order_price = Foundation.formatPrice(key.order_price) }
        if (key.price) { key.price = Foundation.formatPrice(key.price) }
        if (key.final_money) { key.final_money = Foundation.formatPrice(key.final_money) }
        if (key.create_time) { key.create_time = Foundation.unixToDate(key.create_time) }
        if (key.end_time) { key.end_time = Foundation.unixToDate(key.end_time) }
      })
      this.setData({
        pageData: this.data.pageData,
        performanceList: response.data
      })
    })
  },
  //获取与我相关的退款单记录
  getRelevantRefundList() {
    API_Distribution.getRelevantRefundList(this.data.params).then(response => {
      this.data.pageData = {
        page_no: response.page_no,
        page_size: response.page_size,
        data_total: response.data_total
      }
      response.data.forEach(key => {
        key.showactiveNames = false
        if (key.order_price) { key.order_price = Foundation.formatPrice(key.order_price) }
        if (key.price) { key.price = Foundation.formatPrice(key.price) }
        if (key.final_money) { key.final_money = Foundation.formatPrice(key.final_money) }
        if (key.create_time) { key.create_time = Foundation.unixToDate(key.create_time) }
        if (key.end_time) { key.end_time = Foundation.unixToDate(key.end_time) }
      })
      this.setData({
        pageData: this.data.pageData,
        performanceList: response.data
      })
    })
  },
  //获取我的历史业绩记录
  getMyHistoryList() {
    API_Distribution.getMyHistoryList(this.data.params).then(response => {
      this.data.pageData = {
        page_no: response.page_no,
        page_size: response.page_size,
        data_total: response.data_total
      }
      response.data.forEach(key => {
        key.showactiveNames = false
        if (key.order_price) { key.order_price = Foundation.formatPrice(key.order_price) }
        if (key.price) { key.price = Foundation.formatPrice(key.price) }
        if (key.final_money) { key.final_money = Foundation.formatPrice(key.final_money) }
        if (key.create_time) { key.create_time = Foundation.unixToDate(key.create_time) }
        if (key.end_time) { key.end_time = Foundation.unixToDate(key.end_time) }
      })
      this.setData({
        pageData: this.data.pageData,
        performanceList: response.data,
        sn_text: '结算单编号'
      })
    })
  },
  handleDetails(e) {
    const item = e.currentTarget.dataset.item
    this.setData({
      active: 0,
      params: {
        ...this.data.params,
        member_id: item.member_id,
        bill_id: item.id
      }
    })
    this.getRelevantList()
  }
})