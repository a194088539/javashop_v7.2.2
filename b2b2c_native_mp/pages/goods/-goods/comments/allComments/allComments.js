import * as API_Members from '../../../../../api/members'
import { Foundation } from '../../../../../ui-utils/index'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    goodsId:'',
    comments: [],
    finished: false,
    params: {
      page_no: 1,
      page_size: 10,
      grade: ''
    },
    scrollHeight: 0,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({ 
      goodsId: options.goodsId, 
      scrollHeight: wx.getSystemInfoSync().windowHeight
    })
    this.getGoodsComments()
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  },
  filterGrade(val) {
    switch (val) {
      case 'bad':
        return '差评'
      case 'neutral':
        return '中评'
      default:
        return '好评'
    }
  },
  loadMore() {
    this.setData({ 'params.page_no': this.data.params.page_no += 1 })
    if (!this.data.finished) { this.getGoodsComments()}
  },
  getGoodsComments() {
    API_Members.getGoodsComments(this.data.goodsId, this.data.params).then(response => {
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.grade = this.filterGrade(key.grade)
          key.create_time = Foundation.unixToDate(key.create_time, 'yyyy-MM-dd')
          if (key.additional_comment && key.additional_comment.audit_status === 'PASS_AUDIT') {
            key.additional_comment.create_time = Foundation.unixToDate(key.additional_comment.create_time, 'yyyy-MM-dd')
          }
        })
        this.data.comments.push(...data)
        this.setData({ comments: this.data.comments })
      } else {
        this.setData({ finished: true })
      }
    })
  }
})