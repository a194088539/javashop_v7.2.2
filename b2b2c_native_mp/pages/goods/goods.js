import regeneratorRuntime from '../../lib/wxPromise.min.js'
let util = require('../../utils/util.js')
import * as API_Goods from '../../api/goods'
import * as API_Members from '../../api/members'
import * as API_Trade from '../../api/trade'
import * as API_Promotions from '../../api/promotions'
import * as API_Common from '../../api/common'
import * as API_Distribution from '../../api/distribution'
import * as API_Live from '../../api/live'
import { Foundation } from '../../ui-utils/index.js'
const CONFIG = require('../../config/config')

let livePlayer = requirePlugin('live-player-plugin')

Page({
  data: {
    /** 商品信息相关 */
    goods_id: 0,//商品id
    goods: {},//商品详情信息
    param_list: [],//商品参数
    gallery_list: [],//商品轮播图片
    /** 促销相关 */
    promotions: '', // 促销信息
    exchange: '', //积分
    skuPromotions: '',
    showPromotion: true,
    assemble: '',//拼团信息
    assembleOrderList: '',//待成团订单
    is_assemble: false,//是否是拼团
    tabIndex: 0,//tab切换
    grade: 0,//好评率
    in_store: true, // 配送地区是否有货状态 默认有货
    cartGoodsCount: 0,//购物车商品数量
    /** 规格相关 */
    showSpecsPopup: false, // 是否显示规格选择弹窗 默认不显示 为false
    selectedSku: '',// 已选sku
    skuId: '', // 路由加载参数中携带的skuId
    pintuanId: '',// 路由加载参数中携带的pintuanId
    from_nav: '', //路由加载参数中携带的from_nav
    buyNum: 1, // 所选商品数量
    originPriceShow: false,//是否隐藏价格
    canView: false,// 当前商品是否可预览
    /* 收藏相关 */
    collected: false, // 商品是否已被收藏
    scrollTop: 0, // 滚动距离
    scrollHeight:0,
    showGoTop: false, // 显示返回顶部按钮
    finished: false, // 是否加载完毕
    showShare:false,//分享
    disParams:'',//分销参数
    showDisPopup: false, //是否显示海报图
    mobile_intro: '', // 商品详情
    activity_id: '', //活动id
    promotion_type: '', //活动类型
    is_liveVideo: false, // 是否显示直播入口
    room_id: '' // 直播id
  },
  // tab切换
  tabs(e) {
    let index = parseInt(e.currentTarget.dataset.index)
    this.setData({ tabIndex: index })
  },
  onLoad(options) {
    //解析获取到生成二维码时传入的 scene
    const scene = decodeURIComponent(options.scene)
    const sceneArr = scene.split("&")
    // 页面初始化 options为页面跳转所带来的参数
    this.setData({
      goods_id: options.goods_id || parseInt(sceneArr[0])  || '',
      skuId: options.sku_id || '',
      pintuanId: options.pintuan_id || '',
      from_nav: options.from_nav || '',
      su:options.su || '',
      from:sceneArr[1] || ''
    })
    //获取设备宽高
    wx.getSystemInfo({
      success: (res) => {
        this.setData({scrollHeight: res.windowHeight})
      },
    })
  },
  async onShow() {
    let goods = {}
    let that = this
    try {
      goods = await API_Goods.getGoods(that.data.goods_id)
    } catch (e) {
      wx.showToast({ title: '商品已不存在', icon: 'none' })
    }
    goods.price = Foundation.formatPrice(goods.price)
    //商品图片
    const thumbnail = goods.thumbnail.replace(/http\:\/\//, 'https://')
    wx.getImageInfo({
      src: thumbnail,
      success(res){that.setData({thumbnail:res.path})}
    })
    that.setData({
      goods: goods,
      grade: goods.grade,
      param_list: goods.param_list,
      gallery_list: goods.gallery_list || [],
      canView: goods.is_auth !== 0 && goods.goods_off === 1,
      selectedSku:'',
      // disParams: `?goods_id=${goods.goods_id}`
    })
    that.getGoodsInfo()
    if (goods.mobile_intro) {
      this.setData({
        mobile_intro: JSON.parse(goods.mobile_intro)
      })
    }
    if(wx.getStorageSync('refresh_token')) {
      if(!this.data.disParams) { this.generateShortLink() }
      wx.showShareMenu({withShareTicket: true})
    } else {
      wx.hideShareMenu()
    }
    this.getLiveVideoRoomList()
  },

  // 获取商品活动信息
  getGoodsInfo() {
    //如果商品可以查看
    if (this.data.canView) {
      let pages = getCurrentPages();//获取加载页面
      let currentPage = pages[pages.length-1];//获取当前页面的对象
      let route = currentPage.route;//获取当前页面路径
      let goods_id = currentPage.options.goods_id;//获取当前路由参数
      let url = route + '/' +goods_id //完整路径
      API_Common.recordView(url) //记录浏览量统计

      // 如果页面是被分享的 则调用分销参数回传API
      this.data.su && API_Distribution.accessShortLink({su:this.data.su})
      // 如果页面是通过小程序码分享的 则存储分销的上级id
      this.data.from && API_Distribution.visitMiniprogramCode({from:this.data.from})

      //如果存在sku_id 并且带有拼团标识
      if (this.data.skuId && this.data.from_nav === 'assemble') {
        this.setData({ is_assemble: true })
        API_Promotions.getAssembleDetail(this.data.skuId).then(response => {
          response.reduce_moeny = Foundation.formatPrice(response.origin_price - response.sales_price)
          response.sales_price = Foundation.formatPrice(response.sales_price)
          response.origin_price = Foundation.formatPrice(response.origin_price)
          this.setData({assemble:response})
        })
      } else {
        //获取商品活动内容
        API_Promotions.getGoodsPromotions(this.data.goods_id).then(response => {
          this.setData({promotions:response})
        })
      }
    }
    if (wx.getStorageSync('refresh_token')) {
      let that = this
      //获取收藏状态
      API_Members.getGoodsIsCollect(that.data.goods_id).then(response => {
        that.setData({collected: response.message})
      })
      //获取用户信息
      const userInfo = wx.getStorageSync('user')
      if(userInfo.face){
        const face = userInfo.face.replace(/http\:\/\//, 'https://')
        wx.getImageInfo({
          src: face,
          success(res) { that.setData({ 'userInfo.face': res.path }) }
        })
      }
      that.setData({ userInfo: userInfo })
      //获取购物车数量
      that.getAllCount()
    }
  },
  //获取购物车数量
  async getAllCount(){
    let _allCount = 0
    const { cart_list } = await API_Trade.getCarts()
    cart_list.forEach(shop => {
      shop.sku_list.forEach(item => {
        if (item.invalid !== 1) {
          _allCount += item.num
        }
      })
    })
    wx.setStorageSync('shoplist',cart_list)
    this.setData({ cartGoodsCount: _allCount })
  },
  //配送地区是否有货发生改变
  handleInStockChange(e) {
    const in_store = e.detail
    this.setData({ in_store: in_store })
  },
  // 所选规格发生变化 sku变化
  onSkuChanged(sku) {
    if(sku.detail) {
      if (this.data.skuId && this.data.from_nav === 'assemble') {
        this.setData({is_assemble:true})
        /** 查询获取某个拼团的sku详情 */
        const { sku_id } = sku.detail
        API_Promotions.getAssembleDetail(sku_id).then(res => {
          res.reduce_moeny = Foundation.formatPrice(res.origin_price - res.sales_price)
          res.sales_price = Foundation.formatPrice(res.sales_price)
          res.origin_price = Foundation.formatPrice(res.origin_price)
          this.setData({assemble: res})
         })
      }
      this.setData({ selectedSku: sku.detail })
      setTimeout(() => {
        if(!this.data.promotions || !this.data.promotions.length) {
          this.setData({ originPriceShow:true })
          return
        }
        const _promotions = this.data.promotions.filter(key => key.sku_id === this.data.selectedSku.sku_id)
        const _exchange = this.data.promotions.find(key => key.promotion_type === 'EXCHANGE')
        if (_exchange) {
          _exchange.exchange.exchange_money = Foundation.formatPrice(_exchange.exchange.exchange_money)
          _exchange.exchange.goods_price = Foundation.formatPrice(_exchange.exchange.goods_price)
          this.setData({exchange:_exchange.exchange,originPriceShow:false})
        }
        // 如果是限时抢购、团购、积分促销活动不显示原价格
        if (_promotions && _promotions.length) {
          const _groupbuy = _promotions.find(key => key.promotion_type === 'GROUPBUY')
          const _seckill = _promotions.find(key => key.promotion_type === 'SECKILL')
          if (_groupbuy) {
            _groupbuy.groupbuy_goods_vo.price = Foundation.formatPrice(_groupbuy.groupbuy_goods_vo.price)
            _groupbuy.groupbuy_goods_vo.original_price = Foundation.formatPrice(_groupbuy.groupbuy_goods_vo.original_price)
          } else if (_seckill) {
            _seckill.seckill_goods_vo.seckill_price = Foundation.formatPrice(_seckill.seckill_goods_vo.seckill_price)
            _seckill.seckill_goods_vo.original_price = Foundation.formatPrice(_seckill.seckill_goods_vo.original_price)
          }
          this.setData({originPriceShow: false,skuPromotions:_promotions[0] || ''})
        } else {
          this.setData({originPriceShow: true})
        }
      }, 500)
    }
  },

  // 所选规格数量发生变化
  onNumChanged(num) {
    this.setData({ buyNum: num.detail || 0 })
  },
  // 去首页
  goHome(){wx.switchTab({url: '/pages/home/home'})},
  // 返回上页
  goReturn(){wx.navigateBack()},
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {
      this.setData({ showGoTop: false })
    }
  },
  goTop() { this.setData({ scrollTop: 0 }) },
  // 跳转到购物车
  openCartPage() {
    wx.switchTab({ url: '/pages/cart/cart' })
  },
  // 跳转到店铺
  openSeller(e){
    let seller_id = e.currentTarget.dataset.seller_id
    wx.navigateTo({
      url: '/pages/shop/shop_id/shop_id?id=' + seller_id
    })
  },
  // 收藏商品
  addCannelCollect() {
    if (!wx.getStorageSync('refresh_token')) {
      wx.showToast({ title: '您还未登录!', image: '../../static/images/icon_error.png' })
      return false
    }
    const { goods_id } = this.data.goods
    if (this.data.collected) {
      API_Members.deleteGoodsCollection(goods_id).then(() => {
        wx.showToast({ title: '取消收藏成功!' })
        this.setData({ collected: false })
      })
    } else {
      API_Members.collectionGoods(goods_id).then(() => {
        wx.showToast({ title: '收藏成功!' })
        this.setData({ collected: true })
      })
    }
  },
  //分销参数
  generateShortLink() {
    API_Distribution.generateShortLink({ goods_id: this.data.goods_id }).then(response => {
      const _query = response.message.substring(response.message.indexOf('?'), response.message.length)
      this.setData({ disParams: `${_query}&goods_id=${this.data.goods_id}` })
    })
  },
  //获取小程序码
  getMiniprogramCode(){
    let that = this
    API_Distribution.getMiniprogramCode(that.data.goods_id).then(response => {
      let minicode = response.replace(/http\:\/\//, 'https://')
      wx.getImageInfo({
        src: minicode,
        success(res) {
          that.setData({ minicode: res.path })
        },
        fail(err) {
          console.log(err)
        }
      })
    })
  },
  //分享
  handShare(){
    if (!wx.getStorageSync('refresh_token')) {
      wx.showToast({ title: '您还未登录!', image: '../../static/images/icon_error.png' })
      return
    } else {
      if(!this.data.disParams) { this.generateShortLink() }
      if(!this.data.minicode) { this.getMiniprogramCode() }
      this.setData({ showShare: true })
    }
  },
  cloneDialog(){
    this.setData({showShare:false})
  },
  /** 加入购物车 */
  handleAddToCart: util.throttle(function() {
    if (!this.data.in_store || !this.isLogin()) return
    this.getActivityId()
    const { sku_id } = this.data.selectedSku
    const { buyNum, activity_id, promotion_type } = this.data
    API_Trade.addToCart(sku_id, buyNum, activity_id, promotion_type).then(response => {
      //更新购物车数量
      this.getAllCount()
      wx.showModal({
        title: '提示',
        content: '加入购物车成功，要去看看么？',
        confirmColor:'#f42424',
        success: function (res) {
          if (res.confirm) {
            wx.switchTab({ url: '/pages/cart/cart' })
          }
        }
      })
    })
  }),
  // 登录校验
  isLogin() {
    if (!this.data.selectedSku) {
      wx.showToast({
        title: '请选择商品规格！',
        icon: 'none',
        duration: 1500
      })
      this.setData({ showSpecsPopup: true })
      return false
    }
    if (!wx.getStorageSync('refresh_token')) { // 如果没有登录
      wx.showModal({
        title: '提示',
        content: '您还未登录，要现在去登录吗？',
        confirmColor: '#f42424',
        success(res) {
          if (res.confirm) {
            wx.navigateTo({ url: '/pages/auth/login/login' })
          }
        }
      })
      return false
    } else {
      return true
    }
  },
  // 立即购买
  BuyNow: util.throttle(function() {
    if (!this.data.in_store || !this.isLogin()) return
    this.getActivityId()
    const { sku_id } = this.data.selectedSku
    const { buyNum, activity_id, promotion_type } = this.data
    API_Trade.buyNow(sku_id, buyNum, activity_id, promotion_type).then((response) => {
      this.setData({ showSpecsPopup: false })
      wx.navigateTo({ url: '/pages/shopping/checkout/checkout?way=BUY_NOW' })
    })
  }),
  //拼团买 创建拼团订单
  handleAssembleBuyNow:util.throttle(function () {
    if (!this.data.in_store || !this.isLogin()) return
    const { buyNum } = this.data
    const { sku_id } = this.data.selectedSku
    API_Trade.addToAssembleCart(sku_id, buyNum).then((response) => {
      wx.navigateTo({ url: '/pages/shopping/checkout/checkout?from_nav=assemble' })
    })
  }),
  //参与拼团
  toAssembleBuyNow(e){
    const order = e.detail
    if (!this.data.in_store || !this.isLogin()) return
    const { buyNum } = this.data
    const { sku_id } = this.data.selectedSku
    API_Trade.addToAssembleCart(sku_id,buyNum).then(response=>{
      wx.navigateTo({ url: `/pages/shopping/checkout/checkout?order_id=${order.order_id}&from_nav=assemble` })
    })
  },
  // 获取活动
  getActivityId() {
    const { promotions } = this.data
    if (!promotions || !promotions.length) return ''
    let pro
    for (let i = 0; i < promotions.length; i++) {
      let item = promotions[i]
      if (item.exchange || item.groupbuy_goods_do || item.seckill_goods_vo || item.minus_vo || item.full_discount_vo || item.half_price_vo) {
        pro = item
        break
      }
    }
    if (!pro) return ''
    this.setData({
      activity_id: pro.activity_id,
      promotion_type: pro.promotion_type
    })
  },
  /** 分享 */
  onShareAppMessage() {
    return {
      title: this.data.goods.goods_name,
      path: `/pages/goods/goods${this.data.disParams}`,
      imageUrl: this.data.goods.thumbnail
    }
  },
  /** 显示海报图 */
  handleCreatePoster() {
    this.setData({ showDisPopup: true})
  },
  /** 点击进入直播间 */
  handleLiveVideo(e) {
    const roomId = e.currentTarget.dataset.roomid
    wx.navigateTo({ url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}` })
  },
  /** 关闭直播提示 */
  handleDeleteLive() {this.setData({ is_liveVideo: false })},
  /** 获取店铺直播信息 */
  getLiveVideoRoomList() {
    const params = {
      page_no: 1,
      page_size: 10,
      seller_id: this.data.goods.seller_id
    }
    API_Live.getLiveVideoRoomList(params).then(response => {
      if (response.data && response.data.length) {
        this.setData({
          is_liveVideo: CONFIG.LIVEVIDEO,
          room_id: response.data[0].we_chat_room_id

      })
      } else {
        this.setData({ is_liveVideo: false })
      }
    })
  }
})
