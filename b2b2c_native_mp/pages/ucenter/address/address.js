import * as API_Address from '../../../api/address'
import * as API_Trade from '../../../api/trade'
import { Foundation, RegExp } from '../../../ui-utils/index'
let util = require('../../../utils/util.js')
const App = getApp()

Page({
  data: {
    addr_id:0,
    finished:false,
    from: '', // 页面涞源
    addressList:[],//地址列表
    delBtnWidth:120
  },
  //删除地址
  deteleAddress(e){
    let that = this
    wx.showModal({
      title: '提示',
      confirmColor: '#f42424',
      content: '确定要删除这个地址吗？',
      success(res) {
        if (res.confirm) {
          API_Address.deleteAddress(e.currentTarget.dataset.addr_id).then(() => {
            wx.showToast({ title: '删除成功' })
            that.getAddressList()
          })
        }
        that.data.addressList.forEach(key => { key.txtStyle = 'left:0px;transition: left 0.2s ease-in-out' })
        that.setData({ addressList: that.data.addressList })
      }
    })
  },
  //编辑地址
  selectAddress: util.throttle(function(e) {
    let addr_id = e.currentTarget.dataset.addr_id
    wx.navigateTo({
      url: '/pages/ucenter/addressAdd/addressAdd?addr_id='+ addr_id
    })
  }),
  //添加新地址
  addAddress: util.throttle(function(e){
    wx.navigateTo({
      url: '/pages/ucenter/addressAdd/addressAdd'
    })
  }),
  //选择地址
  handleSelectAddress(e){
    if(this.data.from === 'checkout'){
      API_Trade.setAddressId(e.currentTarget.dataset.addr_id).then(()=>{
        wx.navigateBack()
      })
    }
  },  
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) { // 设置页面来源
    this.setData({ from: options.from || '', delBtnWidth: util.getEleWidth(this.data.delBtnWidth) })
  },
  onShow() { // 获取地址列表
    this.getAddressList()
  },
  getAddressList(){
    API_Address.getAddressList().then((response)=>{
      response.forEach(key => {
        key.mobile = Foundation.secrecyMobile(key.mobile)
      })
      this.setData({
        addressList:response,
        finished:true
      })
    })
  },
  touchS(e) {
    if (e.touches.length == 1) {
      this.setData({startX: e.touches[0].clientX})
    }
  },
  touchM(e) {
    if (e.touches.length == 1) {
      var moveX = e.touches[0].clientX;
      var disX = this.data.startX - moveX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = "";
      if (disX == 0 || disX < 0) {
        txtStyle = "left:0px";
      } else if (disX > 0) {
        txtStyle = "left:-" + disX + "px";
        if (disX >= delBtnWidth) {
          txtStyle = "left:-" + delBtnWidth + "px";
        }
      }
      var index = e.currentTarget.dataset.index;
      var list = this.data.addressList;
      list.forEach(key => { key.txtStyle = 'left:0px' })
      list[index].txtStyle = txtStyle;
      this.setData({ addressList: list });
    }
  },
  touchE(e) {
    if (e.changedTouches.length == 1) {
      var endX = e.changedTouches[0].clientX;
      var disX = this.data.startX - endX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = disX > delBtnWidth / 2 ? "left:-" + delBtnWidth + "px" : "left:0px";
      var index = e.currentTarget.dataset.index;
      var list = this.data.addressList;
      list.forEach(key => { key.txtStyle = 'left:0px' })
      list[index].txtStyle = txtStyle; 
      this.setData({ addressList: list });
    }
  }
})