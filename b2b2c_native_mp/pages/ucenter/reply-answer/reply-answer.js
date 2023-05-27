import * as API_Members from '../../../api/members.js'
let util = require('../../../utils/util.js') 
import { RegExp } from '../../../ui-utils/index'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    ask_id:'',
    ask:'',
    reply_content:'',
    checked:true,
    anonymous:'YES',
    disabled: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({ask_id:options.ask_id})
    this.getAskDetail()
  },
  handleCheck() { this.setData({ checked: !this.data.checked }) },
  handleAskContent(e) { 
    this.setData({ reply_content: e.detail.value })
    if (this.data.reply_content.length > 2) {
      this.setData({ disabled: true })
    }
  },
  //取消
  handleCancelSubmitAsk(){
    wx.navigateTo({url: '/pages/ucenter/my-ask/my-ask?active=2'})
  },
  //发布
  handleSubmitQuestion: util.throttle(function(){
    if (this.data.reply_content.length > 2){
      this.setData({ anonymous: this.data.checked ? 'YES' : 'NO' })
      API_Members.replyAsk(this.data.ask_id, this.data.reply_content, this.data.anonymous).then(() => {
        wx.showToast({ title: '发布成功' })
        wx.navigateTo({ url: '/pages/ucenter/my-ask/my-ask?active=1' })
      })
    }
  }),
  getAskDetail(){
    API_Members.getAskDetail(this.data.ask_id).then(response=>{
      this.setData({ask:response})
    })
  }
})