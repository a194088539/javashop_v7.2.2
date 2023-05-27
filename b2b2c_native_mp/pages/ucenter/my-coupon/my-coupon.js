const app = getApp();
import * as API_Members from '../../../api/members'
import {Foundation}  from '../../../ui-utils/index.js'

Page({
  data: {
    showType:0,
    couponsList:[],
    params:{
      page_no:1,
      page_size:10,
      status: 1
    },
    finished:false,
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false,//显示返回顶部按钮
    pageCount: 0
  },
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index)
    switch (index) {
      case 0:
        this.data.params.status = 1
        break;
      case 1:
        this.data.params.status = 2
        break;
      case 2:
        this.data.params.status = 3
        break;
    }
    if(index !== this.data.showType){
      this.setData({
        showType: index,
        'params.status': this.data.params.status,
        'params.page_no': 1,
        couponsList: []
      })
      this.getCoupons();
    }
  },
  getCoupons:function(){
    const params = this.data.params
    API_Members.getCoupons(params).then(res => {
      let _pageCount = Math.ceil(res.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const data = res.data
      if(data && data.length){
        data.forEach(key=>{
          key.coupon_price = Foundation.formatPrice(key.coupon_price)
          key.coupon_threshold_price = Foundation.formatPrice(key.coupon_threshold_price)
          key.start_time = Foundation.unixToDate(key.start_time, 'yyyy-MM-dd')
          key.end_time = Foundation.unixToDate(key.end_time, 'yyyy-MM-dd')
        })
        this.data.couponsList.push(...data)
        this.setData({couponsList: this.data.couponsList})
      }else{
        this.setData({ finished: true })
      }
    })
  },
  onLoad: function (options) {
    this.getCoupons()
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 42 })
  },

  loadMore: function () {
    if (!this.data.finshed) {
      this.setData({"params.page_no": this.data.params.page_no + 1})
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getCoupons()
      }
    }  
  },
  scroll: function (e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {
      this.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 }) },

})