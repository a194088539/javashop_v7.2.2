const app = getApp()
let util = require('../../../utils/util.js') 
const middleware = require('../../../utils/middleware.js')
import regeneratorRuntime from '../../../lib/wxPromise.min.js'
import * as API_Trade from '../../../api/trade'
import * as API_Address from '../../../api/address'
import { Foundation } from '../../../ui-utils/index'

Page(middleware.identityFilter({
    data: {
      /** 滚动区域高度 */
      height: '',
      // 是否是拼团
      isAssemble: false,
      // 结算参数
      params: '',
      // 订单总金额
      orderTotal: {
        discount_price: 0,
        exchange_point: 0,
        freight_price: 0,
        goods_price: 0,
        is_free_freight: 0,
        total_price: 0,
      },
      // 购物清单
      inventories: '',
      // 已选地址
      address: '',
      // 已选地址字符串
      addressStr: '',
      // 显示优惠券弹窗
      showCouponsPopup: false,
      // 优惠券张数
      coupon_num: 0,
      //优惠券列表
      couponList:[],
      // 显示购物清单弹窗
      showInventoryPopup: false,
      // 显示发票信息弹窗
      showReceiptPopup: false,
      // 显示支付配送弹窗
      showPaymentPopup: false,
      // 订单备注暂缓区
      remark: '',
      // 显示错误actionsheet
      showErrorActionsheet: false,
      // 错误actionsheet消息
      errorActionsheetMessage: '',
      // 错误actionsheet数据
      errorActionsheetData: [],
      // 表面跳转过来的是立即购买还是购物车结算
      way: '',
      // 参数 订单id
      orderId: '',
      // 询问框
      prompt: '',
      sellerids: '',//商品所属的商家id
    },
    onLoad(options) {
      this.setData({
        way: options.way,
        isAssemble: options.from_nav === 'assemble',
        orderId: options.order_id || ''
      })
    },
    onReady(){
      this.prompt = this.selectComponent("#prompt")
    },
    async onShow() {
      // 获取购物清单
      let params
      try {
        params = await API_Trade.getCheckoutParams()
        //拼团订单只能在线支付
        if(this.data.isAssemble){
          params.payment_type = 'ONLINE'
          await API_Trade.setPaymentType(params.payment_type)
        }
      } catch (e) {
        
      }
      this.setData({
        params: params,
        remark: params.remark,
        height: wx.getSystemInfoSync().windowHeight - 50
      })
      if (params.address_id) {
        await API_Trade.setAddressId(params.address_id)
        const address = await API_Address.getAddressDetail(params.address_id)
        const addressStr = this.formatterAddress(address)
        this.setData({ address:address, addressStr:addressStr })
      }
      await this.GET_Inventories()
    },
    /**
     * 获取购物清单，和结算金额
     * @returns {Promise<void>}
     * @constructor
     */
    GET_Inventories() {
      Promise.all([
        this.data.isAssemble ? API_Trade.getAssembleCarts() : API_Trade.getCarts('checked', this.data.way),
      ]).then(response => {
        const { cart_list, coupon_list, total_price } = response[0]
        total_price.goods_price = Foundation.formatPrice(total_price.goods_price)
        total_price.original_price = Foundation.formatPrice(total_price.original_price)
        total_price.discount_price = Foundation.formatPrice(total_price.discount_price)
        total_price.freight_price = Foundation.formatPrice(total_price.freight_price)
        total_price.total_price = Foundation.formatPrice(total_price.total_price)
        if (total_price.coupon_price !== 0){
          total_price.coupon_price = Foundation.formatPrice(total_price.coupon_price)
        }
        if(total_price.cash_back !== 0){
          total_price.cash_back = Foundation.formatPrice(total_price.cash_back)
        }
        this.setData({ orderTotal: total_price })
        // 处理优惠券信息
        let coupon_num = 0
        if (coupon_list && coupon_list.length) {
          coupon_num += coupon_list.filter(coupon => coupon.enable !== 0).length
          coupon_list.forEach(coupon => {
            coupon.amount = Foundation.formatPrice(coupon.amount)
            coupon.use_term = parseFloat(coupon.use_term.slice(1, 6)).toString()
            coupon.end_time = Foundation.unixToDate(coupon.end_time, 'yyyy-MM-dd')
          })
        }

        // 处理商品信息
        if (cart_list && cart_list.length) {
          cart_list.forEach(shop => {
            // 处理商品运费信息
            shop.price.freight_price = Foundation.formatPrice(shop.price.freight_price)
            if(shop.promotion_notice) {
              shop.promotion_notice = Foundation.formatPrice(shop.promotion_notice)
            }
            //处理赠品
            if (shop.gift_coupon_list && shop.gift_coupon_list.length) {
              shop.gift_coupon_list.forEach(gift_coupon => {
                gift_coupon.amount = Foundation.formatPrice(gift_coupon.amount)
              })
            }
            if(shop.gift_list && shop.gift_list.length){
              shop.gift_list.forEach(gift=>{
                gift.gift_price = Foundation.formatPrice(gift.gift_price)
              })
            }
            // 处理sku数据
            if (shop.sku_list && shop.sku_list.length) {
              shop.sku_list.forEach(item => {
                if (item.purchase_price < item.original_price){
                  item.original_price = Foundation.formatPrice(item.original_price)
                }else{
                  item.original_price = ''
                }
                item.purchase_price = Foundation.formatPrice(item.purchase_price)
                if (!item.skuName) {
                  item.skuName = this.formatterSkuSpec(item)
                }
                if(item.single_list){
                  const _list = item.single_list.filter(item => item.is_check === 1)
                  item.promotionName = _list[0] ? _list[0].promotion_name : '不参加活动'
                }
              })
              const showPromotionNotice = !shop.sku_list.every(sku => sku.single_list && sku.single_list.some(item => item.is_check === 1 && item.promotion_type === 'EXCHANGE'))
              shop.showPromotionNotice = showPromotionNotice
            }
          })
        }
        let _sellerIds = cart_list.map(item => item.seller_id).join(',')
        this.setData({ coupon_num: coupon_num, couponList: coupon_list, inventories: cart_list, sellerids:_sellerIds })
      })
    },
    /** 支付配送发生改变 */
    handlePaymentChanged(e) {
      const payment = e.detail
      const { params } = this.data
      this.setData({ params: {...params, ...payment}})
    },
    /** 格式化地址信息 */
    formatterAddress(address) {
      if (!address) return ''
      return `${address.province} ${address.city} ${address.county} ${address.town} ${address.addr}`
    },
    /** 提交订单 */
    async handleCreateTrade() {
      /** 先调用创建订单接口，再跳转到收银台 */
      try {
        const response = this.data.isAssemble ? await API_Trade.createAssembleTrade('MINI', this.data.orderId) : await API_Trade.createTrade('MINI',this.data.way)
        wx.redirectTo({ url: `/pages/cashier/cashier?trade_sn=${response.trade_sn}` })
      } catch (error) {
        const { data } = error.response || {}
        if (data.data) {
          const { data: list } = data
          if (!list || !list[0]) {
            wx.showToast({ title: data.message,icon:'none'})
            return
          }
        } else {
          wx.showToast({ title: data.message,icon:'none'})
        }
      }
    },
    /** 规格格式化显示 */
    formatterSkuSpec(sku) {
      if (!sku.spec_list || !sku.spec_list.length) return ''
      return sku.spec_list.map(spec => spec.spec_value).join(' - ')
    },
    /** 添加/更换收货地址 */
    handleAddress() {
      wx.navigateTo({url: '/pages/ucenter/address/address?from=checkout'})
    },
    /* 显示支付配送选择弹窗 */
    handleShowPayment() {
      this.setData({ showPaymentPopup: true })
    },
    /* 显示发票选择弹窗 */
    handleShowReceipt() {
      // this.setData({ showReceiptPopup: true })
      if (this.data.orderTotal.original_price === '0.00'){
        wx.showToast({
          title: '该订单不支持开具发票',
          icon:'none'
        })
        return
      }
      wx.navigateTo({
        url: '/pages/shopping/checkout/reicept/reicept?sellerids=' + this.data.sellerids + '&receipt=' + JSON.stringify(this.data.params.receipt),
      })
    },
    // 显示prompt 显示备注弹窗
    showPrompt(){
      this.prompt.showPrompt()
    },
    // 将输入的value暂时保存起来
    getInput(e) {
      this.setData({ remark: e.detail.value })
    },
    // 输入确认
    confirm() {
      const { remark } = this.data
      API_Trade.setRemark(remark).then(() => {
        this.data.params = {
          ...this.data.params,
          remark: remark
        }
        this.setData({
          params: this.data.params
        })
        wx.showToast({  title: '提交成功' })
        this.prompt.hidePrompt()
      })
    },
    // 输入取消
    cancel() {
      this.setData({
        remark: this.data.params.remark
      })
      this.prompt.hidePrompt()
    },
    /** 显示优惠券 */
    handleShowCoupons() {
      this.setData({ showCouponsPopup: true })
    }
  })
)