import * as API_Complaint from '../../../api/complaint'
import { api } from '../../../config/config.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    complaintForm: {},
    params: '',
    radioTypes: [],
    defaultComplaintTheme: {},
    images: [],
    showTitleActionsheet: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({params:{...options}})
    this.getComplaintTheme()
  },
  handleTitleActionsheet() {
    this.setData({showTitleActionsheet:true})
  },
  cloneDialog() {
    this.setData({showTitleActionsheet:false})
  },
  // 选择主题
  bindradioTypes(e) {
    const item = e.currentTarget.dataset.item
    this.data.radioTypes.forEach(key => {
      key.checked = false
      if(key.topic_id === item.topic_id) {
        key.checked = true
      }
    })
    this.data.defaultComplaintTheme.topic_name = item.topic_name
    this.data.defaultComplaintTheme.topic_remark = item.topic_remark
    this.setData({
      defaultComplaintTheme:this.data.defaultComplaintTheme,
      radioTypes:this.data.radioTypes,
      'complaintForm.complain_topic':item.topic_name
    })
  },
  handleComplaintCont(e) {
    this.setData({'complaintForm.content':e.detail.value})
  },
  //上传投诉凭证图片
  uploader(e){
    let that = this
    let count = 3
    if(this.data.complaintForm.images && this.data.complaintForm.images.length) {
      count = count - this.data.complaintForm.images.length
    }
    wx.chooseImage({
      count: count,
      sizeType: ['compressed'],
      success: function(res) {
        const tempFilePaths = res.tempFilePaths
        wx.showLoading({ title: '加载中...', })
        for(let i = 0;i<tempFilePaths.length;i++){
          wx.uploadFile({
            url: `${api.base}/uploaders`,
            filePath: tempFilePaths[i],
            name: 'file',
            header: { 'Content-Type': 'multipart/form-data' },
            success(response) {
              wx.hideLoading()
              const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data
              data && that.data.images.push(data.url)
              that.setData({ 'complaintForm.images': that.data.images })
            }, fail() {
              wx.showToast({ title: '上传失败' })
            }
          })
        }
      },
    })
  },
  //清除投诉凭证图片
  clearImage(e){
    let index = e.currentTarget.dataset.index
    this.data.images.splice(index,1)
    this.setData({ 'complaintForm.images': this.data.images })
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  },
  // 取消
  handleCancel() {wx.navigateBack()},
  // 提交
  handleSubmit() {
    let params = JSON.parse(JSON.stringify(this.data.complaintForm))
    if(!params.content) {
      wx.showToast({ title: '请填写投诉内容!', icon:'none' })
      return 
    }
    if(params.images) {
      params.images = params.images.map(item => item).join(',')
    }
    API_Complaint.appendComplaint({ ...params, ...this.data.params}).then(response => {
      wx.redirectTo({url: '/pages/ucenter/complaints/complaints'})
    })
  },
  /** 获取投诉主题列表 */
  getComplaintTheme() {
    API_Complaint.getComplaintTheme().then(response => {
      if (Array.isArray(response) && response.length) {
        response[0].checked = true
        this.data.defaultComplaintTheme.topic_name = response[0].topic_name
        this.data.defaultComplaintTheme.topic_remark = response[0].topic_remark
        this.setData({radioTypes:response,'complaintForm.complain_topic':response[0].topic_name,defaultComplaintTheme: this.data.defaultComplaintTheme})
      }
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