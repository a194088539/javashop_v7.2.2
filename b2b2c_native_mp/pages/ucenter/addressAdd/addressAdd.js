import * as API_Address from '../../../api/address'
import { Foundation, RegExp } from '../../../ui-utils/index'

Page({
  data: {
    addressDetail: {},
    //地址信息
    addressForm: {
      name: '',
      mobile: '',
      addrs: '',
      addr: '',
      ship_address_name: '',
      region: 2314,
      def_addr: false
    },
    showAddressSelector:false,
    //地区数组
    areas: [],
    addressText:''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if (options){
      this.setData({"addressForm.addr_id": options.addr_id || ''})
    }
    wx.setNavigationBarTitle({ title: options.addr_id ? '编辑收货地址' : '添加收货地址'})
    this.data.addressForm.addr_id && this.getAddressDetail()
  },
  //收货人
  changeName(e){this.setData({'addressForm.name': e.detail.value})},
  //收货人手机号
  changeMobile(e){this.setData({'addressForm.mobile': e.detail.value})},
  //打开地址选择器
  popup() {
    this.selectComponent('#bottomFrame').showFrame();
  },
  closeRegionpicke() {
    this.selectComponent('#bottomFrame').hideFrame();
  },
  //地址发生改变
  addressSelectorChanged(object){
    const item = object.detail
    const obj = {
      last_id: item[item.length - 1].id,
      addrs: item.map(key => { return key.local_name }).join(' ')
    }
    this.setData({
      showAddressSelector: false,
      'addressForm.region': obj.last_id,
      'addressForm.addrs': obj.addrs
    })
  },

  //收货人详细地址
  changeAddr(e) {this.setData({'addressForm.addr': e.detail.value})},
  //地址别名
  changeShipAddressName(e){this.setData({'addressForm.ship_address_name': e.detail.value})},
  //默认地址
  changeDefAddr(e){this.setData({'addressForm.def_addr': e.detail.value})},
  //保存收货地址
  submitAddressForm(){
    let that = this
    const params = JSON.parse(JSON.stringify(this.data.addressForm))
    if(!params.name.trim()){
      wx.showToast({title: '请填写收货人姓名！',icon:"none"})
    }else if(!params.mobile){
      wx.showToast({ title: '请填写手机号码！', icon: "none" })
    } else if (!RegExp.mobile.test(params.mobile)) {
      wx.showToast({ title: '手机号码格式有误！', icon: "none" })
    } else if (!params.region) {
      wx.showToast({ title: '请选择收货地区！', icon: "none" })
    } else if (!params.addr.trim()) {
      wx.showToast({ title: '请填写收货详细地址！', icon: "none" })
    }else{
      const addr_id = params.addr_id
      params.def_addr = params.def_addr ? 1 : 0
      if(addr_id){
        API_Address.editAddress(addr_id,params).then(()=>{   
          wx.showToast({ title: '修改成功！'})
          wx.navigateBack()
        })
      }else{
        API_Address.addAddress(params).then(() => {
          wx.showToast({ title: '添加成功！'})
          wx.navigateBack()
        })
      }
    }
  },
  //地址详情
  getAddressDetail(){
    API_Address.getAddressDetail(this.data.addressForm.addr_id).then((res)=>{
      const params = JSON.parse(JSON.stringify(res))
      params.def_addr = !!params.def_addr
      params.addrs = `${params.province} ${params.city} ${params.county} ${params.town}`
      params.region = params.town_id || params.county_id
      this.setData({
        addressForm: params
      })
    })
  },
  //地址列表
  getAddressList() {
    API_Address.getAddressList().then((response) => {
      response.forEach(key => {
        key.mobile = Foundation.secrecyMobile(key.mobile)
      })
      this.setData({ addressList: response })
    })
  }
})