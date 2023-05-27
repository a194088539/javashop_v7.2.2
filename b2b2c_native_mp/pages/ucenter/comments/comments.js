import * as API_Order from '../../../api/order.js'
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index.js'
let util = require('../../../utils/util.js') 
import request from '../../../utils/request.js'
import { api } from '../../../config/config.js'
import regeneratorRuntime from '../../../lib/wxPromise.min.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    order_sn:'',
    sku_id:'',
    append_comment:'',
    skulist:'',
    commentsForm:{},
    isFirstComment:'',//是否为初评
    button_show:true,//是否显示评论按钮
    goodsCommentsInfo: {},//初评信息
    diagnosisStar:[1,2,3,4,5]//店铺评分
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      order_sn:options.order_sn ,
      sku_id:options.sku_id || '',
      append_comment: options.append_comment || '',
      isFirstComment:!options.append_comment
    })
    this.getOrderDetail()
  },
  getOrderDetail(){
    const { append_comment, order_sn, sku_id } = this.data
    if (append_comment === 'review'){this.setData({isFirstComment:false})}
    let commentsResult
    API_Order.getOrderDetail(order_sn).then(async response =>{
      this.data.skulist = commentsResult = response.order_sku_list
      if (append_comment === 'review') {
        this.data.goodsCommentsInfo = commentsResult = await API_Members.getGoodsFirstComments({ order_sn,sku_id })
        this.data.goodsCommentsInfo.forEach(key=>{ 
          key.create_time = Foundation.unixToDate(key.create_time)
          if (key.additional_comment){
            key.additional_comment.create_time = Foundation.unixToDate(key.additional_comment.create_time)
          }
        })
        //是否显示发表评论按钮
        const _info = this.data.goodsCommentsInfo.filter(key => key.audit_status === 'PASS_AUDIT' && !key.additional_comment )
        if (_info && _info.length){
          if (_info.some(key => key.audit_status === 'PASS_AUDIT')){
            this.setData({ button_show: true })
          }else{
            this.setData({ button_show: false })
          }        
        } else {
          this.setData({ button_show: false })
        }
      }
      this.data.commentsForm={
        order_sn:this.data.order_sn,
        delivery_score: 5,
        description_score: 5,
        service_score: 5,
        comments:commentsResult.map((item,index)=>{
          return {
            $$goods_comment: item,
            sku_id: item.sku_id,
            audit_status: item.audit_status,
            content: '',
            grade: 'good',
            images: []
          }
        })
      }
      this.setData({
        skulist:this.data.skulist,
        goodsCommentsInfo: this.data.goodsCommentsInfo,
        commentsForm:this.data.commentsForm
      })
    })
  },
  //好评
  getGood(e){
    let cm = e.currentTarget.dataset.cm 
    let index = e.currentTarget.dataset.index
    cm.grade = 'good'
    this.data.commentsForm.comments[index] = cm
    this.setData({ 'commentsForm.comments': this.data.commentsForm.comments })
  },
  //中评
  getNeutral(e){
    let cm = e.currentTarget.dataset.cm 
    let index = e.currentTarget.dataset.index
    cm.grade = 'neutral'
    this.data.commentsForm.comments[index] = cm
    this.setData({ 'commentsForm.comments': this.data.commentsForm.comments })
  },
  //差评
  getBad(e){
    let cm = e.currentTarget.dataset.cm 
    let index = e.currentTarget.dataset.index
    cm.grade = 'bad'
    this.data.commentsForm.comments[index] = cm
    this.setData({ 'commentsForm.comments': this.data.commentsForm.comments })
  },
  //评论内容
  bankContent(e) {
    let index = e.currentTarget.dataset.index
    this.data.commentsForm.comments[index].content = e.detail.value
    this.setData({ 'commentsForm.comments': this.data.commentsForm.comments })
  },
  //上传图片
  uploader(e){
    let that = this
    let cm = e.currentTarget.dataset.cm
    let index = e.currentTarget.dataset.index
    wx.chooseImage({
      count: 5 - cm.images.length,
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
              data && cm.images.push(data.url)
              that.data.commentsForm.comments[index] = cm
              that.setData({ 'commentsForm.comments': that.data.commentsForm.comments })
            }, fail() {
              wx.showToast({ title: '上传失败' })
            }
          })
        }
      },
    })
  },
  //清除图片
  clearImage(e){
    let cm = e.currentTarget.dataset.cm 
    let index = e.currentTarget.dataset.index
    let imgindex = e.currentTarget.dataset.imgindex
    cm.images.splice(imgindex,1)
    this.data.commentsForm.comments[index] = cm
    this.setData({ 'commentsForm.comments': this.data.commentsForm.comments })
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  },
  //店铺评分
  descriptionScoreStar(e) { this.setData({ 'commentsForm.description_score': e.currentTarget.dataset.star });},
  serviceScoreStar(e) { this.setData({ 'commentsForm.service_score': e.currentTarget.dataset.star})},
  deliveryScoreStar(e) { this.setData({ 'commentsForm.delivery_score': e.currentTarget.dataset.star })},
  // 发表评论
  submitForm: util.throttle(function(){ 
    const params = JSON.parse(JSON.stringify(this.data.commentsForm))
    //默认好评
    params.comments.forEach(key =>{
      if(!key.grade) key.grade = 'good'
      if(!this.data.isFirstComment && !key.comment_id) key.comment_id = key.$$goods_comment.comment_id
    })
    //初评校验 非好评必须输入原因
    if(this.data.isFirstComment){
      const result = params.comments.some(key=>(key.grade !== 'good' && !key.content))
      if(result){
        wx.showToast({
          title: '请告诉我们您不满意的原因，我们愿尽最大的努力！',
          icon:'none'
        })
        return false
      }
    }
    //追评校验
    if(!this.data.isFirstComment){
      params.comments = params.comments.filter(key => key.audit_status === 'PASS_AUDIT' && !key.$$goods_comment.additional_comment)
      const result = params.comments.some(key => !key.content)
      if(result){
        wx.showToast({
          title: '请填写追评内容',
          icon:'none'
        })
        return false
      }
    }
    API_Members[!this.data.isFirstComment ? 'appendCommentsOrder' :'commentsOrder'](!this.data.isFirstComment ? params.comments : params).then(()=>{
      wx.navigateBack({ delta: 1 })
      wx.showToast({ title: '发表成功' })
    })
  })
})