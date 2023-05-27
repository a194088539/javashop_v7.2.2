const app = getApp()
let util = require('../../utils/util.js') 
const middleware = require('../../utils/middleware.js')
import * as watch from "../../utils/watch.js";
import * as API_Trade from '../../api/trade'
import regeneratorRuntime from '../../lib/wxPromise.min.js'
import { RegExp, Foundation } from '../../ui-utils/index'

Page({
    data: {
      shopList: [],//购物车商品列表
      skuList: [],//sku列表
      checkedCount: 0,//选中商品数量
      allCount: 0,//所有商品数量
      cartTotal: {},//购物车总计清单
      current_input_value: 1, //当前操作的输入框的值【变化之前】
      activity_options: [],//促销
      showActivityActionsheet: false,//显示促销弹框
      cur_sku_id: null,//sku_id
      all_checked: false, //是否全选
      finished:false,//是否加载完毕
      scrollHeight: '', //滚动高度
      scrollTop: 0,//距离顶部高度
      showGoTop: false,//显示返回顶部按钮
      delBtnWidth:120
    },
    // 页面初始化 options为页面跳转所带来的参数
    onLoad(options) {
      watch.setWatcher(this);
      this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 50, delBtnWidth: util.getEleWidth(this.data.delBtnWidth)})
    },
    onShow(){
      if (wx.getStorageSync("refresh_token")){
        this.getCartList()
      }else{
        this.setData({ shopList: [], finished: true})
      }
    },
    watch:{
      showActivityActionsheet(newVal){
        if(!newVal){this.setData({ cur_sku_id:null})}
      }
    },
    // 获取购物车数据
    async getCartList() {
      const { cart_list, total_price } = await API_Trade.getCarts()
      this.setData({ finished:true})
      let _checkedCount = 0
      let _allCount = 0
      const _skuList = []
      cart_list.forEach(shop => {
        shop.sku_list.forEach(sku => {
          if (sku.invalid !== 1) _allCount += sku.num
          if (sku.checked && sku.invalid !== 1) _checkedCount += sku.num
          sku.purchase_price = Foundation.formatPrice(sku.purchase_price)
          if (!sku.skuName) sku.skuName = this.formatterSkuSpec(sku)
          if(sku.goods_type === 'POINT' && sku.single_list.length) {
            sku.single_list.forEach(item => {
              sku.single_list = sku.single_list.filter(key => key.promotion_type !== 'FULL_DISCOUNT')
            })
          }
        })
        const showPromotionNotice = !shop.sku_list.every(sku => sku.single_list && sku.single_list.some(item => item.is_check === 1 && item.promotion_type === 'EXCHANGE'))
        shop.showPromotionNotice = showPromotionNotice
        _skuList.push(...shop.sku_list)
      })
      // 数据处理
      total_price.total_price = Foundation.formatPrice(total_price.total_price)
      if (total_price.cash_back !== 0){
         total_price.cash_back = Foundation.formatPrice(total_price.cash_back)
      }
      this.setData({
        shopList: cart_list,
        cartTotal: total_price,
        checkedCount: _checkedCount,
        allCount: _allCount,
        skuList: _skuList
      })
      setTimeout(() => {
        this.setData({ all_checked: this.isCheckedAll() })
      })
    },
    // 判断购物车商品已全选
    isCheckedAll() {
      return !!this.data.checkedCount && this.data.checkedCount === this.data.allCount
    },
    /** 勾选、取消勾选商品 */
    handleCheckSku(e) {
      const sku = e.currentTarget.dataset.sku
      API_Trade.updateSkuChecked(sku.sku_id, sku.checked ? 0 : 1).then(_ => {this.getCartList()})
    },
    /** 勾选、取消勾选店铺 */
    handleCheckShop(e) {
      const shop = e.currentTarget.dataset.shop
      API_Trade.checkShop(shop.seller_id, shop.checked ? 0 : 1 ).then(_ => {this.getCartList()})
    },
    /** 规格格式化显示 */
    formatterSkuSpec(sku) {
      if (!sku.spec_list || !sku.spec_list.length) return ''
      return sku.spec_list.map(spec => spec.spec_value).join(' - ')
    },
    /** 全选、取消全选 */
    handleCheckAll() {API_Trade.checkAll(this.data.all_checked ? 0 : 1).then(_ => {this.getCartList()})},
    /** 打开促销选择 */
    handleChangeActivity(e) {
      let sku = e.currentTarget.dataset.sku
      const options = sku.single_list.map(item => ({
        name: item.promotion_name,
        promotion_id: item.promotion_id,
        is_check: item.is_check,
        promotion_type: item.promotion_type,
        className: item.is_check === 1 ? 'checked' : '',
        sku
      }))
      const no_act = sku.single_list.filter(item => item.is_check === 1)
      options.push({
        name: '不参加活动',
        className: no_act[0] ? '' : 'checked',
        sku
      })
      this.setData({
        activity_options : options,
        cur_sku_id : sku.sku_id,
        showActivityActionsheet: true
      })
    },
    // 关闭促销选择
    cloneDialog(){
      this.setData({showActivityActionsheet: false})
    },
    /** 确认促销选择 */
    handleActivitySelect(e) {
      const item = e.currentTarget.dataset.item
      if (item.disabled) return
      const { seller_id, single_list = [], sku_id } = item.sku
      if (item.name === '不参加活动') {
        API_Trade.cleanPromotion(seller_id, sku_id).then(() => {this.getCartList()})
      } else {
        const { promotion_type, promotion_id } = item
        const params = {
          seller_id,
          sku_id,
          promotion_id,
          promotion_type
        }
        API_Trade.changeActivity(params).then(()=>{this.getCartList()})
      }
      this.setData({showActivityActionsheet: false})
    },
    /** input失去焦点事件 */
    handleBlur(e) {
      const sku = e.target.dataset.sku
      if (!e.detail.value || isNaN(e.detail.value) || e.detail.value < 1){
        let _num = 1
        this.updateSkuNum({ sku_id: sku.sku_id, num: _num })
      }
    },
    /** 更新商品数量 */
    handleUpdateSkuNum(e) {
      const sku = e.target.dataset.sku
      const symbol = e.target.dataset.symbol
      if (!symbol || typeof symbol !== 'string') {
        if (!e.detail.value) return
        let _num = parseInt(e.detail.value)
        if (isNaN(_num) || _num < 1) {
          wx.showToast({ title: '输入格式有误，请输入正整数！',icon:'none' })
          return
        }
        if (_num >= sku.enable_quantity) {
          wx.showToast({ title: '超过最大库存！',icon:'none' })
          return
        }
        this.updateSkuNum({sku_id: sku.sku_id, num: _num})
      } else {
        if (symbol === '-' && sku.num < 2) return
        if (symbol === '+' && sku.num >= sku.enable_quantity) {
          wx.showToast({ title: '超过最大库存！',icon:'none' })
          return
        }
        let _num = symbol === '+' ? sku.num + 1 : sku.num - 1
        this.updateSkuNum({sku_id: sku.sku_id, num: _num})
      }
    },
    /* 更新sku数据 */
    updateSkuNum(params) {API_Trade.updateSkuNum(params.sku_id, params.num).then(_ => {this.getCartList()})},
    /** 删除 */
    handleDelete: util.throttle(function(e) {
      const sku = e.currentTarget.dataset.sku
      let that = this
      wx.showModal({
        title: '提示',
        content: '确定要删除这个货品吗?',
        confirmColor:'#f42424',
        success(res) {
          if(res.confirm) {
            API_Trade.deleteSkuItem(sku.sku_id).then(_ => {
              wx.showToast({ title: '删除成功!' })
              setTimeout(_ => { that.getCartList() }, 200)
            }).catch()
          }
          that.data.shopList.forEach(key => { key.sku_list.forEach(item => { item.txtStyle = 'left:0px' }) })
          that.setData({ shopList: that.data.shopList })
        }
      })
    }),
    /** 批量删除 */
    handleBatchDelete: util.throttle(function() {
      let that = this
      wx.showModal({
        title: '提示',
        content: '确定要删除这些货品吗?',
        confirmColor: '#f42424',
        success(res) {
          if(res.confirm) {
            const sku_ids = that.data.skuList.filter(item => item.checked).map(item => item.sku_id)
            API_Trade.deleteSkuItem(sku_ids).then(_ => {
              wx.showToast({ title: '删除成功！' })
              setTimeout(_ => { that.getCartList() }, 200)
            }).catch()
          }
        }
      })
    }),
    /** 清空购物车 */
    handleCleanCart() {
      wx.showModal({
        title: '提示',
        content: '确定要清空购物车吗?',
        success(res) {
          if(res.confirm) {
            API_Trade.cleanCarts().then(_ => {
              setTimeout(_ => { this.getCartList() }, 200)
            }).catch()
          }
        }
      })
    },
    /** 去结算 */
    handleCheckout: util.throttle(function() {
      if (!this.data.checkedCount) {
        wx.showToast({title: '您还没有选中商品哦',icon:'none'})
        return false
      }
      wx.navigateTo({url: '/pages/shopping/checkout/checkout?way=CART'})
    }),
    // 去购物
    goshopping(){wx.switchTab({url: '/pages/home/home'})},
    //返回按钮显示
    scroll: function (e) { if (e.detail.scrollTop > 200) {this.setData({ showGoTop: true })} else {this.setData({ showGoTop: false })}},
    //返回顶部
    goTop: function () { this.setData({ scrollTop: 0 }) },
    touchS(e) {
      if (e.touches.length == 1) {
        this.setData({ startX: e.touches[0].clientX })
      }
    },
    touchM(e) {
      if (e.touches.length == 1) {
        var moveX = e.touches[0].clientX;
        var disX = this.data.startX - moveX;
        var delBtnWidth = this.data.delBtnWidth;
        var txtStyle = "";
        if (disX == 0 || disX < 0) {
          txtStyle = "left:0px";
        } else if (disX > 0) {
          txtStyle = "left:-" + disX + "px";
          if (disX >= delBtnWidth) {
            txtStyle = "left:-" + delBtnWidth + "px";
          }
        }
        var shopIndex = e.currentTarget.dataset.shopindex
        var skuIndex = e.currentTarget.dataset.skuindex
        var list = this.data.shopList;
        list.forEach(key => { key.sku_list.forEach(item => { item.txtStyle = 'left:0px' }) })
        list[shopIndex].sku_list[skuIndex].txtStyle = txtStyle;
        this.setData({ shopList: list });
      }
    },
    touchE(e) {
      if (e.changedTouches.length == 1) {
        var endX = e.changedTouches[0].clientX;
        var disX = this.data.startX - endX;
        var delBtnWidth = this.data.delBtnWidth;
        var txtStyle = disX > delBtnWidth / 2 ? "left:-" + delBtnWidth + "px" : "left:0px";
        var shopIndex = e.currentTarget.dataset.shopindex
        var skuIndex = e.currentTarget.dataset.skuindex
        var list = this.data.shopList;
        list.forEach(key => { key.sku_list.forEach(item => { item.txtStyle = 'left:0px' }) })
        list[shopIndex].sku_list[skuIndex].txtStyle = txtStyle;
        this.setData({ shopList: list });
      }
    }
  })