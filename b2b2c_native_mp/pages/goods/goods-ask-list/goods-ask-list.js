import * as API_Members from '../../../api/members'
import * as API_Goods from '../../../api/goods.js'
let util = require('../../../utils/util.js') 
import { Foundation,RegExp } from '../../../ui-utils/index'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    finished:false,
    goods:'',
    goods_id:'',
    params:{
      page_no:1,
      page_size:10
    },
    pageCount:0,
    asks:[],
    ask_content:'',
    checked:true,
    anonymous:'YES',
    isShowAskList:true,
    isShowSubmitBtn:true,
    isShowSubmitAsk:false,
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false,//显示返回顶部按钮
    disabled:false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      goods_id:options.goods_id,
      scrollHeight: wx.getSystemInfoSync().windowHeight
    })
    this.getGoodsDetail()
    this.getAsks()
  },
  // 获取商品详情信息
  getGoodsDetail(){
    API_Goods.getGoods(this.data.goods_id).then(response=>{
      this.setData({goods:response})
    })
  },
  // 获取商品资询
  getAsks() {
    API_Members.getGoodsConsultations(this.data.goods_id, this.data.params).then(response => {
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
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
  //咨询详情
  handleAskDetail(e){
    wx.navigateTo({
      url: '/pages/goods/goods-ask-list/ask-detail/ask-detail?ask_id=' + e.currentTarget.dataset.ask_id,
    })
  },
  //提问详情
  handleSubmitAsk(){
    if (wx.getStorageSync('refresh_token')){
      this.setData({
        isShowAskList:false,
        isShowSubmitBtn:false,
        isShowSubmitAsk:true
      })
    }else{
      wx.showModal({
        title: '提示',
        content: '您还未登录，要现在去登录吗？',
        confirmColor:'#f42424',
        success(res) {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/auth/login/login' })
          }
        }
      })
      return false
    }
  },
  handleCheck(){this.setData({checked:!this.data.checked})},
  handleAskContent(e){
    this.setData({ ask_content: e.detail.value })
    if(this.data.ask_content.length>2){
      this.setData({disabled:true})
    }
  },
  handleCancelSubmitAsk(){
    this.setData({
      isShowAskList: true,
      isShowSubmitBtn: true,
      isShowSubmitAsk: false
    })
  },
  //发布咨询
  handleSubmitQuestion: util.throttle(function () {
    if(this.data.ask_content.length>2){
      this.setData({ anonymous: this.data.checked ? 'YES' : 'NO'})
      API_Members.consultating(this.data.goods_id, this.data.ask_content, this.data.anonymous).then(() => {
        this.setData({
          asks: [],
          finished: false,
          pageCount: 0,
          anonymous: 'YES',
          checked: true,
          ask_content: '',
          disabled:false
        })
        this.handleCancelSubmitAsk()
        this.getAsks()
        setTimeout(() => { wx.showToast({ title: '发布成功', duration: 1500 })},100)
      })
    }
  }),
  loadMore: function () {
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getAsks()
      }
    }
  },
  scroll: function (e) {
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({ showGoTop: true })
    } else {
      that.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 }) }
})