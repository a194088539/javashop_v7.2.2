import * as API_Shop from '../../api/shop.js'
import { Foundation } from '../../ui-utils/index.js'
var WxParse = require('../../lib/wxParse/wxParse.js');

Page({

  /**
   * 页面的初始数据
   */
  data: {
    shopList:[],//店铺列表
    scrollHeight:0,
    scrollTop: 0,
    showGoTop:false,
    params:{
      page_no:1,
      page_size:10,
      name:''
    },
    finished: false,//是否已经加载完毕
  },
  //获取店铺列表
  getShopList:function(){
    const params = JSON.parse(JSON.stringify(this.data.params))
    if (!params.name) delete params.name
    API_Shop.getShopList(params).then(response=>{
      const data = response.data
      if (data && data.length) {
        data.forEach(key=>{
          key.goods_list.forEach(item=>{
            item.price = Foundation.formatPrice(item.price)
          })
        })
        this.setData({ shopList: this.data.shopList.concat(data) })
      }else{
        this.setData({ finished: true })
      }
    })
  },
  //加载更多店铺
  loadMore: function (e) {
    this.setData({"params.page_no": this.data.params.page_no += 1});
    this.getShopList();
  },
  scroll: function (e) {
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({
        showGoTop: true
      })
    } else {
      that.setData({
        showGoTop: false
      })
    }
  },
  goTop: function () {this.setData({scrollTop: 0})}, 
 
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this
    that.setData({'params.name':options.keyword});
    wx.getSystemInfo({
      success: function (res) {
        that.setData({
          scrollHeight: res.windowHeight + 'px'
        })
      },
    })
    that.getShopList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})