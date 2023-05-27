import * as API_Home from '../../api/home'
import * as API_Goods from '../../api/goods'
import * as watch from "../../utils/watch.js";
import { Foundation } from '../../ui-utils/index.js'

var app = getApp()
Page({
  data: {
    historyKeyword: [],     // 历史搜索
    searchType:'商品',      // 搜索类型
    showSearchType: false, // 是否显示搜索类型 
    autoCompleteData: '',    // 自动补全数据
    hotKeyword: [],         // 热门搜索词 
    keyword:''
  },
  //取消搜索
  closeSearch: function () {wx.navigateBack()},
  //清除搜索关键字
  clearKeyword: function () {
    this.setData({keyword: ''});
  },
  onLoad: function (options) {
    this.getSearchKeyword();
    watch.setWatcher(this);//启用数据监听
  },
  onShow(){
    this.setData({keyword:''})
  },
  watch:{
    keyword:function(newVal){
      if(this.data.searchType !== '商品') return
      if(!newVal){
        this.setData({autoCompleteData:[]})
        return
      }
      newVal = newVal.trim()
      API_Goods.getKeywordNum(newVal).then(response=>{
        this.setData({ autoCompleteData: response})
      })
    }
  },
  //自动补全数据事件
  handleOnSubmit(e) {
    let keyword = e.currentTarget.dataset.keyword
    this.getSearchResult(keyword)
    this.handleSearchHistory(keyword)
  },
  //热门搜索
  getSearchKeyword() {
    API_Home.getHotKeywords().then((data=>{
      this.setData({ hotKeyword: data })
    }))
    this.setData({
      historyKeyword: wx.getStorageSync('searchData') || []
    });
  },
  inputChange: function (e) {
    this.setData({keyword: e.detail.value || ''});
    this.getSearchKeyword()
  },
  inputFocus: function () {
    this.getSearchKeyword();
  },
  //搜索类型
  isShowSearchType(){this.setData({showSearchType:!this.data.showSearchType})},
  searchTypeGood(){this.setData({searchType:'商品',showSearchType:false})},
  searchTypeShop(){this.setData({searchType:'店铺',showSearchType:false})},
  //清除历史搜索
  clearHistory: function () {
    this.setData({historyKeyword: []})
    wx.clearStorageSync('searchData')
  },
  //历史记录，热门搜索事件
  onKeywordTap: function (event) {
    let keyword = event.currentTarget.dataset.keyword
    this.getSearchResult(keyword.value || keyword.hot_name)  
    this.handleSearchHistory(keyword.value || keyword.hot_name)  
  },
  //搜索结果
  getSearchResult(keyword) {
    if(this.data.searchType === '商品'){
      wx.navigateTo({ url: '/pages/category/category?keyword=' + keyword || '' })
    }else{
      wx.navigateTo({url: '/pages/shop/shop?keyword=' + keyword || ''})
    }  
  },
  //选择搜索的历史
  onKeywordConfirm(event) {
    let keyword = event.detail.value;
    this.getSearchResult(keyword);
    // if (this.data.searchType === '店铺') return   
    this.handleSearchHistory(keyword)
  },
  // 存储历史搜索记录
  handleSearchHistory(keyword) {
    let searchData = this.data.historyKeyword;
    if (keyword == undefined || keyword == '') return;
    for (let i = 0; i < searchData.length; i++) {
      let _index = searchData[i].value.indexOf(keyword)
      if (_index !== -1) {
        // 删除已存在后重新插入至数组
        searchData.splice(i, 1)
      } else if (searchData.length > 9) {
        // 只保留10个
        searchData.pop();
      }
    }
    searchData.unshift({
      value: keyword,
      id: searchData.length
    });
    wx.setStorageSync('searchData', searchData);
  }
})