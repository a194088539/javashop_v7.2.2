import * as API_AfterSale from '../../../../api/after-sale.js'
import { Foundation, RegExp } from '../../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    /** 售后服务单号 */
    service_sn: '',
    /** 售后服务详情 */
    serviceDetail: '',
    /** 最新一条日志信息 */
    log: '',
    /** 申请售后商品信息集合 */
    goodsList: [],
    /** 售后服务单允许操作信息 */
    allowable: '',
    /** 收货地址相关信息 */
    change_info: '',
    /** 退款相关信息 */
    refund_info: '',
    /** 发货单信息 */
    express_info: '',
    /** 是否展示退款相关信息 */
    refundShow: false,
    /** 是否展示退款账号相关信息 */
    accountShow: false,
    /** 是否展示银行卡相关信息 */
    bankShow: false,
    /** 预览图片信息集合 */
    imagesList: [],
    /** 物流公司集合 */
    logiList: [],
    /** 是否展示退货地址弹出框 */
    returnAddrShow: false,
    /** 物流信息表单 */
    expressForm: {
      courier_company_id: '',
      courier_number: '',
      ship_time: 0
    },
    /** 是否显示快递公司下拉框 */
    expressShow: false,
    /** 快递公司名称 */
    courier_company: '请选择快递公司',
    /** 快递公司下拉框数据 */
    expressSelectActions: [],
    /** 是否显示发货日期时间选择器 */
    shipTimeShow: false,
    /** 发货日期（默认当前时间）*/
    shipTime: new Date(),
    shipTimeText: '其选择发货时间',
    /** 是否显示发货单信息 */
    shipInfoShow: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let _time = JSON.stringify(new Date())
    this.setData({ service_sn: options.service_sn, time: _time.slice(1, 11)})
    this.getAfterSaleDetail()
  },
  //图片预览
  handleImagePreview(e) {
    let imgArr = e.currentTarget.dataset.urls
    let img = e.currentTarget.dataset.img
    wx.previewImage({ urls: imgArr, current: img })
  },
  //快递公司
  handelExpressShow(){
    this.data.logiList.forEach(item=>{
      let param = {}
      param.name = item.name
      param.value = item.id
      this.data.expressSelectActions.push(param)
      this.setData({expressSelectActions:this.data.expressSelectActions})
    })
    this.setData({ expressShow:true})
  },
  handleExpressSelectActions(e) { 
    this.setData({ 
      courier_company: e.currentTarget.dataset.name,
      'expressForm.courier_company_id': e.currentTarget.dataset.value,
       expressShow: false, 
       expressSelectActions:[]
    })
  },
  handleExpressHide() { this.setData({ expressShow: false, expressSelectActions:[]})},
  handleCourierNumber(e) { this.setData({'expressForm.courier_number':e.detail.value})},
  handleDateChange(e) { this.setData({ 'expressForm.ship_time': Foundation.dateToUnix(e.detail.value), shipTimeText: e.detail.value})},
  //提交物流信息
  submitShipInfo(){
    const { courier_company_id, courier_number, ship_time } = this.data.expressForm
    if (!courier_company_id) {
      wx.showToast({title: '请选择快递公司！',icon:'none'})
      return false
    }
    // 快递单号校验
    if (!courier_number) {
      wx.showToast({ title: '请填写快递单号！', icon: 'none' })
      return false
    }
    if (!RegExp.waybillNo.test(courier_number)) {
      wx.showToast({ title: '快递单号不符合规则，请输入大写字母或者数字!', icon: 'none' })
      return false
    }
    // 申请原因校验
    if (!ship_time || ship_time === 0) {
      wx.showToast({ title: '请选择发货时间！', icon: 'none' })
      return false
    }
    API_AfterSale.fillShipInfo(this.data.service_sn, this.data.expressForm).then(() => {
      wx.showToast({ title: '提交成功', icon: 'none' })
      this.getAfterSaleDetail()
    })
  },
  handlereTurnAddrShow(){this.setData({ returnAddrShow:true})},
  handleReturnAddrHide(){this.setData({ returnAddrShow:false})},
  handleShipInfoShow() { this.setData({ shipInfoShow:true})},
  handleShipInfoHide() { this.setData({ shipInfoShow:false})},
  getAfterSaleDetail(){
    API_AfterSale.getServiceDetail(this.data.service_sn).then(response=>{
      response.goods_list.forEach(key=>{key.price = Foundation.formatPrice(key.price)})
      response.change_info.ship_mobile = Foundation.secrecyMobile(response.change_info.ship_mobile)
      if (response.express_info) { response.express_info.ship_time = Foundation.unixToDate(response.express_info.ship_time,'yyyy-MM-dd')}
      if (response.service_type === 'RETURN_GOODS' || response.service_type === 'ORDER_CANCEL'){
        response.refund_info.refund_price = Foundation.formatPrice(response.refund_info.refund_price)
        if (response.refund_info.agree_price) { response.refund_info.agree_price = Foundation.formatPrice(response.refund_info.agree_price)}
        if (response.refund_info.actual_price) { response.refund_info.actual_price = Foundation.formatPrice(response.refund_info.actual_price)}
        if (response.refund_info.refund_time) { response.refund_info.refund_time = Foundation.unixToDate(response.refund_info.refund_time)}
      }
      this.setData({
        serviceDetail:response,
        log:response.logs[0],
        allowable: response.allowable,
        goodsList: response.goods_list,
        change_info:response.change_info,
        refund_info:response.refund_info,
        express_info:response.express_info,
        logiList: response.logi_list,
        refundShow: response.service_type === 'RETURN_GOODS' || response.service_type === 'ORDER_CANCEL',
        accountShow: (response.service_type === 'RETURN_GOODS' || response.service_type === 'ORDER_CANCEL') && response.refund_info.refund_way === 'ACCOUNT',
        bankShow: (response.service_type === 'RETURN_GOODS' || response.service_type === 'ORDER_CANCEL') && response.refund_info.refund_way === 'ACCOUNT' && response.refund_info.account_type === 'BANKTRANSFER'
      })
      if(response.images){
        let _imagesList = []
        response.images.forEach(item=>{
          _imagesList.push(item.img)
        })
        this.setData({imagesList:_imagesList})
      }
    })
  }
})