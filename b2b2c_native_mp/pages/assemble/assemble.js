import regeneratorRuntime from '../../lib/wxPromise.min.js'
import * as API_Promotions from '../../api/promotions'
import * as API_Goods from '../../api/goods'
import { Foundation } from '../../ui-utils/index'

Page({
  data: {
    finished: false,
    categories: [],
    params: {
      page_no: 1,
      page_size: 10,
      category_id: 0
    },
    assemble: [],
    scrollTop: 0,
    scrollHeight: 0,
    showGoTop: false
  },
  async onLoad() {
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 37 })
    // 获取商品分类
    let categories = await API_Goods.getCategory()
    categories.unshift({ category_id: 0, name: '全部', active: true })
    this.setData({ categories,assemble: [] })
    this.GET_AssembleGoods()
  },
  /** 加载数据 */
  loadMore() {
    this.setData({ 'params.page_no': this.data.params.page_no += 1 })
    if (!this.data.finished) { this.GET_AssembleGoods()}
  },
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else { this.setData({ showGoTop: false }) }
  },
  goTop() { this.setData({ scrollTop: 0 }) },
  /** 选择分类 */
  handleClickCate(e) {
    const index = e.detail
    const { categories } = this.data
    const cate = categories[index]
    const _categories = categories.map(item => {
      item.active = item.category_id === cate.category_id
      return item
    })
    this.data.params.page_no = 1
    this.data.params.category_id = cate.category_id
    this.setData({
      finished: false,
      assemble: [],
      params: this.data.params,
      categories: _categories
    })
    this.GET_AssembleGoods()
  },
  /** 获取拼团商品 */
  GET_AssembleGoods() {
    const params = JSON.parse(JSON.stringify(this.data.params))
    if (params.category_id === 0) delete params.category_id
    API_Promotions.getAssembleList(params).then(response => {
      if (response && response.length) {
        response.forEach(key => {
          key.sales_price = Foundation.formatPrice(key.sales_price)
          key.origin_price = Foundation.formatPrice(key.origin_price)
        })
        this.data.assemble.push(...response)
        this.setData({
          assemble: this.data.assemble
        })
      } else {
        this.setData({ finished: true })
      }
    })
  }
})
