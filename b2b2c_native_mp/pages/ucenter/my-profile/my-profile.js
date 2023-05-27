let middleware = require('../../../utils/middleware.js');
import * as API_Members from '../../../api/members'
let util = require('../../../utils/util.js')
import { api } from '../../../config/config.js'
import { Foundation,RegExp } from '../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    defaultFace: '../../../static/images/icon-noface.jpg',//默认图片
    profileForm:{},
    nickname:'',
    showEditNickname: true, //显示昵称修改框
    showSexActionsheet: true, // 显示性别选择菜单
    sexActions: [
      { name: '男', gender: 1 },
      { name: '女', gender: 0 }
    ],
    time:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    const userInfo = wx.getStorageSync('user')
    if (userInfo.face === 'null') { userInfo.face = null }
    let _time = JSON.stringify(new Date())
    this.setData({ 
      profileForm: userInfo,
      nickname:userInfo.nickname || '',
      time: _time.slice(1, 11),
      'profileForm.sex': userInfo.sex === 1 ? '男' : '女',
      'profileForm.birthday': userInfo.birthday ? Foundation.unixToDate(userInfo.birthday, 'yyyy-MM-dd') : ''
    })
  },
  nicknameChange(){this.setData({showEditNickname: false })},
  sexChange(){this.setData({showSexActionsheet:false})},
  //修改性别
  bindGenderTap(e){
    this.setData({
      'profileForm.sex': e.currentTarget.dataset.gender === 1 ? '男' : '女',
      showSexActionsheet: true
    })
  },
  //修改生日信息
  bindDateChange(e) {
    this.setData({'profileForm.birthday': e.detail.value})
  },
  //关闭弹出框
  cloneDialog(){
    this.setData({
      showEditNickname:true,
      showSexActionsheet: true
    })
  },
  //获取输入的昵称信息
  bindKeyInput(e) { this.setData({ "profileForm.nickname":e.detail.value})},
  //修改昵称
  updateNickname(){
    let nickname = this.data.profileForm.nickname
    if (!nickname){
      wx.showToast({
        title: '请输入昵称',
        icon:'none'
      })
    } else if (nickname.length < 2 || nickname.length > 20){
      wx.showToast({
        title: '长度应在2-20之间',
        icon: 'none'
      })
    } else if (!RegExp.userName.test(nickname)){
      wx.showToast({
        title: '格式不正确！',
        icon: 'none'
      })
    }else{
      this.setData({
        nickname: nickname,
        showEditNickname: true
      })
    }
  },
  //修改头像
  updateFace(){
    let that= this
    wx.showActionSheet({
      itemList: ['从相册中选择','拍照'],
      itemColor:'#000',
      success(res){
        if(!res.cancel){
          if(res.tapIndex === 0){
            that.chooseWxImage('album')
          }else if(res.tapIndex === 1){
            that.chooseWxImage('camera')
          }
        }
      }
    })
  },
  chooseWxImage(type){
    let that = this
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: [type],
      success: function (res) {
        const tempFilePaths = res.tempFilePaths
        wx.showLoading({ title: '加载中...', })
        wx.uploadFile({
          url: `${api.base}/uploaders`,
          filePath: tempFilePaths[0],
          name: 'file',
          header: { uuid: wx.getStorageSync('uuid')},
          success(response){
            wx.hideLoading()
            const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data
            that.setData({'profileForm.face':data.url})
          },fail(){
            wx.showToast({title: '上传失败'})
          }
        })
      },
    })
  },
  //清空昵称
  clearNickname() {
    this.setData({
      // 'nickname':'',
      "profileForm.nickname":''
    })
  },
  //保存修改
  submitProfile: util.throttle(function(){
    const params = {
      face:this.data.profileForm.face,
      nickname:this.data.nickname,
      sex:this.data.profileForm.sex,
      birthday:this.data.profileForm.birthday || ''
    }
    if(params.birthday && typeof params.birthday === 'string'){
      params.birthday = Foundation.dateToUnix(params.birthday)
    }
    if (params.sex === '女') {
      params.sex = 0
    }else{
      params.sex = 1
    }
    API_Members.saveUserInfo(params).then((response)=>{
      wx.showToast({title: '修改成功！'})
      wx.setStorageSync('user', response)
    })
  })
})