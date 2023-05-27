import * as API_Members from '../../../../api/members.js'
import { Foundation, RegExp } from '../../../../ui-utils/index'
import regeneratorRuntime from '../../../../lib/wxPromise.min.js'

Page({
  data: {
    addressDetail: {},
    //地址信息
    addressForm: {
      member_name: '',
      member_mobile: '',
      address: '',
      detail_addr: '',
      region: '',
    },
    showAddressSelector: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.getInviceAddress()
  },
  //收票人
  syncMemberName(e) { this.setData({ 'addressForm.member_name': e.detail.value }) },
  //收票人手机号
  syncMemberMobile(e) { this.setData({ 'addressForm.member_mobile': e.detail.value }) },
  //收票人详细地址
  syncDetailAddr(e) { this.setData({ 'addressForm.detail_addr': e.detail.value }) },
  //打开地址选择器
  openRegionpicke() { this.selectComponent('#bottomFrame').showFrame() },
  //关闭地址选择器
  closeRegionpicke() { this.selectComponent('#bottomFrame').hideFrame() },
  //地址发生改变
  addressSelectorChanged(object) {
    const item = object.detail
    const obj = {
      last_id: item[item.length - 1].id,
      addrs: item.map(key => { return key.local_name }).join(' ')
    }
    this.setData({
      showAddressSelector: false,
      'addressForm.region': obj.last_id,
      'addressForm.address': obj.addrs
    })
  },
  //保存收货地址
  handleSaveAddress() {
    const params = JSON.parse(JSON.stringify(this.data.addressForm))
    if (!params.member_name.trim()) {
      wx.showToast({ title: '请输入收票人姓名！', icon: "none" })
    } else if (!params.member_mobile) {
      wx.showToast({ title: '请输入手机号码！', icon: "none" })
    } else if (!RegExp.mobile.test(params.member_mobile)) {
      wx.showToast({ title: '请输入正确的手机号码！', icon: "none" })
    } else if (!params.region) {
      wx.showToast({ title: '请选择所在地区！', icon: "none" })
    } else if (!params.detail_addr.trim()) {
      wx.showToast({ title: '请输入详细地址！', icon: "none" })
    } else {
      Promise.all([this.data.addressForm.id ? API_Members.changeTicketAdress(params,this.data.addressForm.id) : API_Members.ticketAdressApply(params)]).then(()=>{
        wx.showToast({ title: '保存成功'})
        this.getInviceAddress()
      })
    }
  },
  //查询收票人地址
  getInviceAddress() {
    API_Members.queryTicketAdress().then((res) => {
      if(res){
        this.data.addressForm.id = res.id
        this.data.addressForm.member_name = res.member_name
        this.data.addressForm.member_mobile = res.member_mobile
        this.data.addressForm.detail_addr = res.detail_addr
        this.data.addressForm.address = `${res.province} ${res.city} ${res.county} ${res.town}`
        this.data.addressForm.region = res.town_id || res.county_id
      }
      this.setData({addressForm: this.data.addressForm})
    })
  }
})