import * as API_Complaint from '../../../api/complaint'
import { Foundation } from '../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    id: '',
    // 交易投诉详情数据
    complaintDetail: '',
    // 投诉凭证
    images: [],
    // 商家申诉凭证
    appeal_images: [],
    // 对话表单
    dialogueForm: {},
    // 对话记录
    communication_list: [],
    // 流程图数据
    flow: '',
    flow_active: 0,
    // 对话表单
    dialogueForm: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({id:options.id})
    this.getComplaintDetail()
  },
  /* 撤销投诉 */
  handleCancel(e) {
    const id = e.currentTarget.dataset.id
    API_Complaint.cancelComplaint(id).then(res => {
      wx.showToast({title: '撤销投诉成功！'})
      this.getComplaintDetail()
    })
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  },
  /* 对话内容 */
  handleDialogueForm(e) {
    this.setData({'dialogueForm.content':e.detail.value})
  },
  submitDialogueForm() {
    let params = JSON.parse(JSON.stringify(this.data.dialogueForm))
    if(!params.content) {
      wx.showToast({ title: '请输入对话内容！', icon: "none" })
      return
    }
    API_Complaint.initiationSession(this.data.id, params).then(response => {
      this.getComplaintDetail()
      this.setData({'dialogueForm.content':''})
    })
  },
  /* 获取详情信息 */
  getComplaintDetail() {
    API_Complaint.getComplaintDetail(this.data.id).then(response => {
      response.create_time = Foundation.unixToDate(response.create_time)
      response.appeal_time = Foundation.unixToDate(response.appeal_time)
      response.communication_list.forEach(key => {
        key.create_time = Foundation.unixToDate(key.create_time)
      })
      this.setData({
        complaintDetail: response,
        images: response.images ? response.images.split(',') : [],
        appeal_images: response.appeal_images ? response.appeal_images.split(',') : [],
        communication_list: response.communication_list ? response.communication_list.reverse() : []
      })
    })
    // 交易投诉流程
    API_Complaint.getComplaintFlow(this.data.id).then(response => {
      const index = response.findIndex(item => item.show_status === 0)
      this.setData({
        flow: response,
        flow_active: index === -1 ? response.length : index-1
      })
    })
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