const app = getApp();
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index.js'
let util = require('../../../utils/util.js')

Page({
  data: {
    typeId: 0,
    goodsList:[],
    params_goods:{
      page_no: 1,
      page_size: 10
    },
    shopsList:[],
    params_shops: {
      page_no: 1,
      page_size: 10
    },
    finished_goods: false,//是否加载完成
    finished_shops: false,//是否加载完成
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false,//显示返回顶部按钮
    pageCount: 0
  },
  getCollection(){
    if (this.data.typeId === 0){
      API_Members.getGoodsCollection(this.data.params_goods).then(res => {
        let _pageCount = Math.ceil(res.data_total / this.data.params_goods.page_size)
        this.setData({ pageCount: _pageCount })
        const data = res.data
        if (data && data.length) {
          data.forEach(key => {
            key.goods_price = Foundation.formatPrice(key.goods_price)
          })
          this.setData({ goodsList: this.data.goodsList.concat(data) })
        } else {
          this.setData({ finished_goods: true })
        }
      }).catch(() => {  })
    }else{
      API_Members.getShopCollection(this.data.params_shops).then(res => {
        let _pageCount = Math.ceil(res.data_total / this.data.params_shops.page_size)
        this.setData({ pageCount: _pageCount })
        const data = res.data
        if (data && data.length) {
          this.setData({ shopsList: this.data.shopsList.concat(data) })
        } else {
          this.setData({ finished_shops: true })
        }
      }).catch(() => {  })
    }
  },
  //取消收藏商品
  deleteGoodsColl: function(e){
    let that = this
    const goods_id = e.currentTarget.dataset.goods_id;
    wx.showModal({
      title: '提示',
      content: '确定要删除这个商品收藏吗？',
      confirmColor: '#f42424',
      success(res) {
        if (res.confirm) {
          API_Members.deleteGoodsCollection(goods_id).then(() => {
            wx.showToast({ title: '删除成功！' })
            that.setData({
              'params_goods.page_no': 1,
              goodsList: [],
              confirmGoods: true
            })
            that.getCollection()
          })
        }
      }
    })
  },
  //取消收藏店铺
  deleteShopColl: util.throttle(function(e){
    let that = this
    const shop_id = e.currentTarget.dataset.shop_id
    wx.showModal({
      title: '提示',
      content: '确定要删除这个店铺收藏吗？',
      confirmColor: '#f42424',
      success(res) {
        if (res.confirm) {
          API_Members.deleteShopCollection(shop_id).then(() => {
            wx.showToast({ title: '删除成功！' })
            that.setData({
              'params_shops.page_no': 1,
              shopsList: [],
              confirmShops: true
            })
            that.getCollection()
          })
        }
      }
    })
  }),
  onLoad: function (options) {
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 42 })
    this.getCollection()
  }, 
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index);
    if (index === 0 && index !== this.data.typeId){
      this.setData({
        'params_goods.page_no': 1,
        goodsList: [],
        typeId: index,
        finished_goods:false,
        pageCount:0
      })
      this.getCollection()
    }
    if (index === 1 && index !== this.data.typeId) {
      this.setData({
        'params_shops.page_no': 1,
        shopsList: [],
        typeId: index,
        finished_shops: false,
        pageCount: 0
      })
      this.getCollection()
    }
  },

  loadMore: function () {
    if (this.data.typeId === 0) {
      this.setData({ "params_goods.page_no": this.data.params_goods.page_no += 1 })
      if (this.data.pageCount >= this.data.params_goods.page_no) {
        this.getCollection()
      }
    } else {
      this.setData({ 'params_shops.page_no': this.data.params_shops.page_no += 1 })
      if (this.data.pageCount >= this.data.params_shops.page_no) {
        this.getCollection()
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