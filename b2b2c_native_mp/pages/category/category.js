import * as API_Goods from '../../api/goods'
import { Foundation } from '../../ui-utils/index.js'

Page({
  data: {
    goodsList: [],//商品列表
    currentCategory: {},
    categoryFilter: false,  // 分类筛选是否打开
    scrollLeft: 0,
    scrollTop: 0,
    scrollHeight: 0,
    params:{
      page_no:1,
      page_size:20,
      sort:'def_asc'
    },
    finished: false,
    showGoTop:false,
    msg: ''
  },
  onLoad(options) {
    // 页面初始化 options为页面跳转所带来的参数
    let params = {
      ...this.data.params,
      ...options
    }
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 90, params})
    this.getGoodsList()
  },
  getGoodsList() {
    const _params = this.data.params
    if (_params.catId) {
      delete _params.keyword
    }
    API_Goods.getGoodsList(_params).then((res)=> {
      let data = res.data
      if (data && data.length) {
        data.forEach(key => {
          key.price = Foundation.formatPrice(key.price)
        })
        this.setData({
          categoryFilter: false,
          goodsList: this.data.goodsList.concat(data)
        });
      }else{
        this.setData({ finished: true })
      }
    });
    wx.setNavigationBarTitle({ title: "商品分类"})
  },
  loadMore(e) {
    this.setData({ 'params.page_no': this.data.params.page_no += 1});
    if (!this.data.finished) {
      this.getGoodsList()
    } else {
      this.setData({ msg: '已经到底了~' })
    }
  },
  scroll(e){
    if (e.detail.scrollTop > 200) {
      this.setData({showGoTop: true})
    } else {
      this.setData({showGoTop: false})
    }
  },
  goTop(){ this.setData({scrollTop:0}) },
  goseller(e){
    wx.navigateTo({
      url: '/pages/shop/shop_id/shop_id?id='+e.currentTarget.dataset.shop_id,
    })
  },
  openSortFilter(event) {
    let currentId = event.currentTarget.id;
    switch (currentId) {
      case 'categoryFilter':
        this.setData({
          categoryFilter: !this.data.categoryFilter
        });
        break;
      case 'defSort':
        var sort = '';
        if (this.data.params.sort == 'def_asc') {
          sort = 'def_desc';
        } else {
          sort = 'def_asc';
        }
        this.setData({
          'params.sort': sort,
          'params.page_no':1,
          categoryFilter: false,
          finished:false,
          goodsList:[]
        });
        this.getGoodsList();
        break;
      case 'priceSort':
        var sort = '';
        if (this.data.params.sort == 'price_asc') {
          sort = 'price_desc';
        } else {
          sort = 'price_asc';
        }
        this.setData({
          'params.sort': sort,
          categoryFilter: false,
          finished: false,
          'params.page_no': 1,
          goodsList: []
        });

        this.getGoodsList();
        break;
      case 'gradeSort':
        var sort = '';
        if (this.data.params.sort == 'grade_asc') {
          sort = 'grade_desc';
        } else {
          sort = 'grade_asc';
        }
        this.setData({
          'params.sort': sort,
          categoryFilter: false,
          finished: false,
          'params.page_no': 1,
          goodsList: []
        });

        this.getGoodsList();
        break;
      default:
        var sort = '';
        if (this.data.params.sort == 'buynum_asc') {
          sort = 'buynum_desc';
        } else {
          sort = 'buynum_asc';
        }
        //销量排序
        this.setData({
          'params.sort': sort,
          categoryFilter: false,
          'params.page_no': 1,
          finished: false,
          goodsList: []
        });
        this.getGoodsList();
    }
  }
})