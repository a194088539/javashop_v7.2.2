import * as API_Members from '../../../../api/members.js'
import { Foundation } from '../../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    comment_id:'',
    commentsDetail:'',
    diagnosisStar: [1, 2, 3, 4, 5],//店铺评分
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({comment_id:options.comment_id})
    this.getCommentDetail()
  },
  gradeFilter(val){
    switch(val){
      case 'bad':return '差评'
      case 'neutral': return '中评'
      default: return '好评'
    }
  },
  getCommentDetail(){
    API_Members.getCommentsDetail(this.data.comment_id).then(response=>{
      response.create_time = Foundation.unixToDate(response.create_time)
      response.grade = this.gradeFilter(response.grade)
      if (response.additional_comment){
        response.additional_comment.create_time = Foundation.unixToDate(response.additional_comment.create_time)
      }
      this.setData({commentsDetail:response})
    })
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  }
})