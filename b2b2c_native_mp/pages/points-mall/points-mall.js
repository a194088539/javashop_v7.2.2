import * as API_Promotions from '../../api/promotions'
import regeneratorRuntime from '../../lib/wxPromise.min.js'
import { Foundation } from '../../ui-utils/index'

Page({
  data: {
    finished: false,
    params: {
      page_no: 1,
      page_size: 10,
      cat_id: 0
    },
    pointsList: [],
    // 页面高度
    scrollTop: 0,
    scrollHeight: 0,
    showGoTop: false
  },
  async onLoad() {
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 35 })
    let categories = await API_Promotions.getPointsCategory()
    categories = categories.map(item => {
      return {
        active: false,
        name: item.name,
        cat_id: item.category_id
      }
    })
    categories.unshift({ cat_id: 0, name: '全部', active: true })
    this.setData({ categories})
    this.GET_PointsGoods()
  },
  /** 加载数据 */
  loadMore() {
    this.setData({ 'params.page_no': this.data.params.page_no += 1})
    if (!this.data.finished) {this.GET_PointsGoods()}
  },
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {this.setData({ showGoTop: false })}
  },
  goTop() {this.setData({ scrollTop: 0 })},
  /** 选择团购分类 */
  handleClickCate(e) {
    const index = e.detail
    const { categories } = this.data
    const cat = categories[index]
    const _categories = categories.map(item => {
      item.active = item.cat_id === cat.cat_id
      return item
    })
    this.data.params.cat_id = cat.cat_id
    this.setData({
      finished:false,
      pointsList: [],
      categories: _categories,
      params: this.data.params
    })
    this.data.params.page_no = 1
    this.GET_PointsGoods()
  },
  /** 获取积分商品 */
  GET_PointsGoods() {
    const params = JSON.parse(JSON.stringify(this.data.params))
    if (params.cat_id === 0) delete params.cat_id
    API_Promotions.getPointsGoods(params).then(response => {
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.exchange_money = Foundation.formatPrice(key.exchange_money)
          key.goods_price = Foundation.formatPrice(key.goods_price)
        })
        this.data.pointsList.push(...data)
        this.setData({ pointsList: this.data.pointsList })
      } else {
        this.setData({ finished: true })
      }
    })
  }
})