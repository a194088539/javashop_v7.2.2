import * as API_Home from '../../api/home'

Page({
  data: {
    categoryList: [],           // 所有1级分类
    currentCategory: {},        // 当前选中分类
    scrollLeft: 0,
    scrollTop: 0,
    goodsCount: 0,
    scrollHeight: 0
  },
  onLoad: function (options) {
    API_Home.getCategory(0).then(data => {
      // const _data = data.filter(key=> key.category_id !== 85 && key.category_id !== 4)
      this.setData({
        categoryList: data,
        currentCategory: data[0]
      })
    })
  },
  switchCate(e) {
    const id = e.currentTarget.dataset.id
    const cat = this.data.categoryList.find(item => item.category_id === id)
    this.setData({ currentCategory: cat })
  }
})