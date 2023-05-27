import regeneratorRuntime from '../../lib/wxPromise.min.js'
import * as API_Promotions from '../../api/promotions'
import { Foundation } from '../../ui-utils/index'

Page({
  data: {
    finished: false,
    params: {
      page_no: 1,
      page_size: 10,
      cat_id: 0
    },
    groupBuy: [],
    // 分类列表
    categories: [],
    // 页面高度
    scrollTop: 0,
    scrollHeight: 0,
    showGoTop: false
  },
  async onLoad() {
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 37 })
    // 获取团购分类
    let categories = await API_Promotions.getGroupBuyCategorys()
    categories.unshift({ cat_id: 0, cat_name: '全部', cat_order: 0, active: true })
    categories.sort((x, y) => x.cat_order > y.cat_order)
    this.setData({ categories, groupBuy: [] })
    this.GET_GroupBuyGoods()
  },
  /** 加载数据 */
  loadData() {
    this.setData({ "params.page_no": this.data.params.page_no += 1 })
    if (!this.data.finished) {this.GET_GroupBuyGoods()}
  },
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else { this.setData({ showGoTop: false }) }
  },
  goTop() { this.setData({ scrollTop: 0 }) },
  /** 选择团购分类 */
  handleClickCate(e) {
    const index = e.detail
    const { categories } = this.data
    const cate = categories[index]
    const _categories = categories.map(item => {
      item.active = item.cat_id === cate.cat_id
      return item
    })
    this.data.params.page_no = 1
    this.data.params.cat_id = cate.cat_id
    this.setData({
      finished:false,
      groupBuy: [],
      params: this.data.params,
      categories: _categories
    })
    this.GET_GroupBuyGoods()
  },
  /** 获取团购商品 */
  GET_GroupBuyGoods() {
    const params = JSON.parse(JSON.stringify(this.data.params))
    if (params.cat_id === 0) delete params.cat_id
    API_Promotions.getGroupBuyGoods(params).then(response => {
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.price = Foundation.formatPrice(key.price)
          key.original_price = Foundation.formatPrice(key.original_price)
        })
        this.data.groupBuy.push(...data)
        this.setData({ groupBuy: this.data.groupBuy })
      } else {
        this.setData({ finished: true })
      }
    })
  }
})