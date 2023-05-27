import * as API_Shop from '../../../api/shop.js'
import * as API_Promotions from '../../../api/promotions.js'
import * as API_Goods from '../../../api/goods.js'
import * as API_Members from '../../../api/members.js'
import * as API_Live from '../../../api/live.js'
import { Foundation } from '../../../ui-utils/index.js'
var WxParse = require('../../../lib/wxParse/wxParse.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    shop_id: 0,
    shop: [],
    is_collection: false,//店铺是否收藏
    collection_num: 0,//店铺收藏数
    shopsildes: [],//店铺幻灯片
    coupons: '',//优惠券列表
    newGoods: [],// 新品上架
    hotGoods: [],// 热销商品
    recGoods: [],// 推荐商品
    scrollHeight: 0,
    scrollTop: 0,
    showGoTop: false, //返回顶部按钮
    liveVideoData: ''
  },
  //获取店铺基本信息
  getShopInfo(){
    let that = this;
    API_Shop.getShopBaseInfo(that.data.shop_id).then(response =>{
      that.setData({
        shop: response,
        collection_num: response.shop_collect
      })
      wx.setNavigationBarTitle({ title: response.shop_name})
      WxParse.wxParse('shopDetail', 'html', response.shop_desc, that)
    })
    that.getShopSildes()
    that.getShopCoupons()
    that.getShopTagGoods()
    that.getShopIsCollect()
  },
  //获取店铺幻灯片
  getShopSildes(){
    API_Shop.getShopSildes(this.data.shop_id).then(response => {
      this.setData({ shopsildes: response })
    })
  },
  //获取店铺优惠券
  getShopCoupons(){
    API_Promotions.getShopCoupons(this.data.shop_id).then(response =>{
      this.setData({ coupons: response})
    })
  },
  //领取店铺优惠券
  handleReceiveCoupon(e) {
    const coupon = e.currentTarget.dataset.coupon
    if (!wx.getStorageSync('refresh_token')) {
      wx.showToast({ title: '您还未登录!', image: '../../../static/images/icon_error.png' })
      return false
    }
    API_Members.receiveCoupons(coupon.coupon_id).then(() => {
      wx.showToast({ title: '领取成功' })
    })
  },
  //获取店铺标签商品
  getShopTagGoods() {
    API_Goods.getTagGoods(this.data.shop_id,'hot',10).then(response=>{
      response.forEach(key=>{key.price = Foundation.formatPrice(key.price)})
      this.setData({ hotGoods:response})
    })
    API_Goods.getTagGoods(this.data.shop_id, 'new', 10).then(response => {
      response.forEach(key => {key.price = Foundation.formatPrice(key.price)})
      this.setData({ newGoods: response })
    })
    API_Goods.getTagGoods(this.data.shop_id, 'recommend', 10).then(response => {
      response.forEach(key => {key.price = Foundation.formatPrice(key.price)})
      this.setData({ recGoods: response })
    })
  },
  //获取店铺是否收藏
  getShopIsCollect(){
    if (wx.getStorageSync('refresh_token')){
      API_Members.getShopIsCollect(this.data.shop_id).then(response => {
        this.setData({ is_collection: response.message })
      })
    }
  },
  //收藏店铺
  collectionShop(){
    if(!wx.getStorageSync('refresh_token')){
      wx.showToast({ title: '您还未登录!', image:'../../../static/images/icon_error.png'})
      return false
    }
    if(this.data.is_collection){
      API_Members.deleteShopCollection(this.data.shop_id).then(()=>{
        this.setData({is_collection:false,collection_num:this.data.collection_num -= 1})
        wx.showToast({title: '取消收藏成功!'})
      })
    }else{
      API_Members.collectionShop(this.data.shop_id).then(()=>{
        this.setData({ is_collection: true, collection_num: this.data.collection_num += 1 })
        wx.showToast({ title: '收藏成功!' })
      })
    }
  },
  //滚动到指定位置显示返回顶部按钮
  scroll: function (e) {
    console.log(e)
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({showGoTop: true})
    } else {
      that.setData({showGoTop: false})
    }
  },
  //返回顶部
  goTop(){wx.pageScrollTo({scrollTop: 0})},
  //模板三中点击导航跳转到指定位置
  hotanchor() {wx.pageScrollTo({scrollTop: 400})},
  recanchor() {wx.pageScrollTo({scrollTop: 3028})},
  newanchor() {wx.pageScrollTo({scrollTop: 6142})},
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({shop_id: parseInt(options.id)})
    this.getShopInfo()
    this.getShopLive()
  },

  /** 滚动监听 */
  onPageScroll(e) {
    if(e.scrollTop > 100){
      this.setData({showGoTop: true})
    }else{
      this.setData({showGoTop: false })
    }
  },
  /** 获取店铺直播数据 */
  getShopLive() {
    const params = {
      page_no: 1,
      page_size: 10,
      seller_id: this.data.shop_id
    }
    API_Live.getLiveVideoRoomList(params).then(response => {
      response && this.setData({liveVideoData: response.data[0]})
    })
  },
  /** 点击进入直播间 */
  handleLiveVideo(e) {
    const roomId = e.currentTarget.dataset.roomid
    wx.navigateTo({ url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}` })
  }
})