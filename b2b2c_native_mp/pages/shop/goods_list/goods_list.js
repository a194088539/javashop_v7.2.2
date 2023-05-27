import * as API_Shop from '../../../api/shop.js'
import * as API_Goods from '../../../api/goods.js'
import { Foundation } from '../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    goodsList: [],
    searchStatus: false,    //搜索状态-是否已经在搜索了
    categoryFilter: false,  // 分类筛选是否打开
    scrollTop: 0,
    scrollHeight: 0,
    finished:false,
    params:{
      seller_id:0,
      page_no:1,
      page_size:10,
      keyword:'',
      sort: 'def_asc'//筛选条件类型
    },
    categories:'',//店铺分组
    show_cate:false,//展示分组
    msg:''
  },
  getShopInfo(){
    API_Shop.getShopBaseInfo(this.data.params.seller_id).then(response=>{
      wx.setNavigationBarTitle({ title: response.shop_name})
    })
  },
  //获取店铺分类分组
  getShopCategorys(){
    API_Shop.getShopCategorys(this.data.params.seller_id).then(response=>{
      const categories = [{shop_cat_name:'全部分组',shop_cat_id:0}]
      response.forEach(item=>{
        item.disable === 1 && categories.push(item)
        if(item.children && item.children.length){
          item.children.forEach(_item=>{
            _item.lv = 2
            _item.disable ===1 && categories.push(_item)
          })
        }
      })
      this.setData({categories:categories})
    })
  },
  showShopCate(){this.setData({show_cate:true})},
  hideShopCate(){this.setData({show_cate:false})},
  handleShopCare(e){
    const cate = e.currentTarget.dataset.cate
    this.data.categories.forEach(item=>{
      if (cate.shop_cat_id === item.shop_cat_id){
        item.checked = true
      }else{
        item.checked = false
      }     
    })
    this.setData({ 'params.page_no': 1, 'params.shop_cat_id': cate.shop_cat_id, goodsList: [],categories:this.data.categories })
    if(cate.shop_cat_id === 0) delete this.data.params.shop_cat_id
    this.getGoodsList()
  },
  getGoodsList() {
    API_Goods.getGoodsList(this.data.params).then(response=>{
      const data = response.data
      if(data && data.length){
        data.forEach(key=>{
          key.price = Foundation.formatPrice(key.price)
        })
        this.data.goodsList.push(...data)
        this.setData({
          categoryFilter: false,
          goodsList: this.data.goodsList
        });
      }else{
        this.setData({ finished: true })
      }
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
          'params.page_no': 1,
          categoryFilter: false,
          finished: false,
          goodsList: []
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
  },
  inputChange(e) {
    this.setData({
      'params.keyword': e.detail.value || '',
      searchStatus: false
    });
  },
  onKeywordConfirm(){
    this.setData({'params.page_no': 1,goodsList:[]})
    this.getGoodsList()
  },
  //店内搜素
  inStoreSearch(){
    this.setData({'params.page_no':1,goodsList: []})
    this.getGoodsList()
  },
  //全站搜索
  allSearch(){
    wx.navigateTo({
      url: '/pages/category/category?keyword=' + this.data.params.keyword,
    })
  },
  scroll (e) {
    if (e.detail.scrollTop > 200) {
      this.setData({showGoTop: true})
    } else {
      this.setData({showGoTop: false})
    }
  },
  goTop() {this.setData({scrollTop: 0})},
  loadMore(){
    this.setData({ "params.page_no": this.data.params.page_no += 1 })
    if (!this.data.finished) {
      this.getGoodsList()
    } else {
      this.setData({ msg: '已经到底了~' })
    }
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.setData({
      'params.seller_id': options.shop_id,
      scrollHeight:wx.getSystemInfoSync().windowHeight + 'px'
    });
    this.getShopInfo()
    this.getShopCategorys()
    this.getGoodsList()
  }
})