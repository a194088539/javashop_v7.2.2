import * as API_AfterSale from '../../../../api/after-sale.js'
import { Foundation, RegExp} from '../../../../ui-utils/index.js'
import { api } from '../../../../config/config.js'
import * as watch from "../../../../utils/watch.js";

Page({

  /**
   * 页面的初始数据
   */
  data: {
    order_sn:'',
    sku_id:'',
    service_type: '', /**售后类型 */
    applyInfo: '',/**页面展示的信息 */
    /**申请售后表单*/
    applyForm:{
      reason: '请选择申请原因',
      images:[],
      return_num:null,
      ship_addr:'',
      ship_name:'',
      ship_mobile:'',
      account_type: ''
    },
    disabled_reduce:true,
    disabled_add:true,
    /** 是否展示取消原因下拉框 */
    reasonSelectShow: false,
    /** 取消原因下拉框数据 */
    reasonSelectActions: '',
    /** 账户类型下拉框选中的值 */
    accountTypeText: '请选择账户类型',
    /** 是否展示账户类型下拉框 */
    accountTypeSelectShow: false,
    /** 账户类型下拉框数据 */
    accountTypeSelectActions: [
      { name: '支付宝', value: 'ALIPAY' },
      { name: '微信', value: 'WEIXINPAY' },
      { name: '银行卡', value: 'BANKTRANSFER' }
    ],
    /** 是否展示申请凭证弹出层 */
    voucherPopupShow: false,
    /** 选择的申请凭证 */
    voucherText: '暂无凭证',
    /** 申请凭证CheckBox数据集合 */
    voucherList: [
      { name:'有发票',checked:false},
      { name:'有检测报告',checked:false}
    ],
    /** 收货地址地区文字展示 */
    addrText: '',
    /** 收货地址地区信息 */
    regions: null,
    /** 是否展示地区选择器 */
    showAddressSelector: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({ 
      order_sn: options.orderSn,
      sku_id: options.skuId || '',
      service_type: options.serviceType,
      reasonSelectActions: this.getReasonActions(options.serviceType)
    })
    this.getApplyInfo()
    watch.setWatcher(this);//启用数据监听
  },
  watch: {
    'applyForm.return_num': function (newVal) {
      if (newVal === 1 && newVal === this.data.applyInfo.buy_num){
        this.setData({ disabled_add: true,disabled_reduce:true })
      } else if (newVal >= 1 && newVal === this.data.applyInfo.buy_num ){
        this.setData({ disabled_add: true, disabled_reduce: false })
      }else if(newVal > 1 && newVal < this.data.applyInfo.buy_num){
        this.setData({ disabled_reduce: false,disabled_add:false })
      }else if(newVal === 1 && newVal < this.data.applyInfo.buy_num){
        this.setData({disabled_reduce:true,disabled_add:false})
      }
    }
  },
  getApplyInfo() {
    API_AfterSale.getApplyInfo(this.data.order_sn, this.data.sku_id).then(response => {
      this.setData({ 
        applyInfo: response ,
        'applyForm.return_num':response.buy_num,
        'applyForm.ship_addr': response.ship_addr,
        'applyForm.ship_name': response.ship_name,
        'applyForm.ship_mobile': response.ship_mobile,
        addrText: response.province + response.city + response.county + response.town,
        regions: response.town_id && response.town_id > 0 ? response.town_id : response.county_id
      })
      if (response.is_receipt) { 
        this.data.voucherList[0].checked = true
        this.setData({ voucherText: '有发票', voucherList: this.data.voucherList })
      }
    })
  },
  //获取申请原因下拉框数据
  getReasonActions(serviceType) {
    let actions = []
    if (serviceType === 'RETURN_GOODS') {
      actions = [
        { name: '商品降价' },
        { name: '商品与页面描述不符' },
        { name: '缺少件' },
        { name: '质量问题' },
        { name: '发错货' },
        { name: '商品损坏' },
        { name: '不想要了' },
        { name: '其他' }
      ]
      return actions
    } else if (serviceType === 'CHANGE_GOODS') {
      actions = [
        { name: '商品与页面描述不符' },
        { name: '缺少件' },
        { name: '质量问题' },
        { name: '发错货' },
        { name: '商品损坏' },
        { name: '不想要了' },
        { name: '大小/颜色/型号等不合适' },
        { name: '其他' }
      ]
      return actions
    } else {
      actions = [
        { name: '商品发货数量不对' },
        { name: '其他' }
      ]
      return actions
    }
  },
  //申请数量
  handleUpdateReturnNum(e){
    const syboml = e.currentTarget.dataset.syboml || e.detail.value
    if(syboml === '-'){
      this.setData({ 'applyForm.return_num': this.data.applyForm.return_num - 1 })
    }else if(syboml === '+'){
      this.setData({ 'applyForm.return_num': this.data.applyForm.return_num + 1 })
    }else{
      const _num = parseInt(syboml)
      if (_num > this.data.applyInfo.buy_num || _num === 0){
        this.setData({ 'applyForm.return_num': this.data.applyInfo.buy_num })
      }else{
        this.setData({ 'applyForm.return_num': _num })
      }
    }
  },
  //申请原因
  handleReasonSelectShow(){this.setData({ reasonSelectShow:true})},
  handleReasonSelectHide() { this.setData({ reasonSelectShow: false })},
  handleReaseSelect(e){this.setData({ 'applyForm.reason': e.currentTarget.dataset.name,reasonSelectShow:false})},
  handleDescText(e){this.setData({ 'applyForm.problem_desc': e.detail.value})},
  //上传图片
  handleUploader() {
    let that = this
    wx.chooseImage({
      count: 5 - this.data.applyForm.images.length,
      sizeType: ['compressed'],
      success: function (res) {
        const tempFilePaths = res.tempFilePaths
        wx.showLoading({ title: '加载中...', })
        for (let i = 0; i < tempFilePaths.length; i++) {
          wx.uploadFile({
            url: `${api.base}/uploaders`,
            filePath: tempFilePaths[i],
            name: 'file',
            header: { 'Content-Type': 'multipart/form-data' },
            success(response) {
              wx.hideLoading()
              const data = typeof response.data === 'string' ? JSON.parse(response.data) : response.data
              data && that.data.applyForm.images.push(data.url)
              that.setData({ 'applyForm.images': that.data.applyForm.images })
            }, fail() {
              wx.showToast({ title: '上传失败' })
            }
          })
        }
      },
    })
  },
  //清除图片
  clearImage(e) {
    let index = e.currentTarget.dataset.index
    this.data.applyForm.images.splice(index, 1)
    this.setData({ 'applyForm.images': this.data.applyForm.images })
  },
  //退款方式
  handleAccountTypeShow() { this.setData({ accountTypeSelectShow:true })},
  handleAccountTypeHide() { this.setData({ accountTypeSelectShow:false })},
  handleAccountSelect(e) { this.setData({ accountTypeText: e.currentTarget.dataset.name, 'applyForm.account_type': e.currentTarget.dataset.type, accountTypeSelectShow:false})},
  returnAccount(e) { this.setData({'applyForm.return_account':e.detail.value})},
  bankName(e) { this.setData({'applyForm.bank_name':e.detail.value})},
  bankDepositAame(e) { this.setData({'applyForm.bank_deposit_name':e.detail.value})},
  bankaccountName(e) { this.setData({'applyForm.bank_account_name':e.detail.value})},
  bankAccountNumber(e) { this.setData({'applyForm.bank_account_number':e.detail.value})},
  //弹窗提示
  handleTitleShow(){
    if (this.data.applyInfo.is_retrace){
      wx.showModal({
        content: '退货申请审核通过后，退款将按您订单的支付方式原路退回',
        showCancel: false,
        confirmColor: '#f42424'
      })
    } else if (this.data.applyInfo.is_retrace_balance) {
      wx.showModal({
        content: '退货申请审核通过后，平台会将退款在线转入您的余额中',
        showCancel: false,
        confirmColor: '#f42424'
      })
    } else {
      wx.showModal({
        content: '退货申请审核通过后，平台会将退款在线转入您下面所填写的账户中',
        showCancel: false,
        confirmColor: '#f42424'
      })
    }
  },
  handleTipsShow(){
    wx.showModal({
      content: '商品寄回地址将在审核通过后以短信形式告知，或在申请记录中查询。',
      showCancel: false,
      confirmColor: '#f42424'
    })
  },
  // 申请凭证
  handleVoucherPopupShow() { this.setData({ voucherPopupShow: true }) },
  handleVoucherPopupHide() { this.setData({ voucherPopupShow: false }) },
  handleVoucherCheck(e){
    let voucherText = ''
    const index = e.currentTarget.dataset.index
    this.data.voucherList[index].checked = !this.data.voucherList[index].checked
    this.setData({ voucherList: this.data.voucherList})
    let _voucherList = this.data.voucherList.filter(key => key.checked)
    if(_voucherList.length !== 0){
      _voucherList.forEach(key=>{
        let _voucherText = voucherText += key.name + ','
        this.setData({voucherText:_voucherText.substr(0,_voucherText.length-1)})
      })
    }else{
      this.setData({ voucherText:'暂无凭证'})
    }
  },
  //地址
  handleAddressSelectorShow() { this.selectComponent('#bottomFrame').showFrame()},
  handleAddressSelectorHide() { this.selectComponent('#bottomFrame').hideFrame() },
  addressSelectorChanged(object) {
    const item = object.detail
    const obj = {
      last_id: item[item.length - 1].id,
      addrs: item.map(key => { return key.local_name }).join('')
    }
    this.setData({
      addrText:obj.addrs,
      'applyForm.region':obj.last_id
    })
  },
  handleShipAddr(e) { this.setData({ 'applyForm.ship_addr': e.detail.value }) },
  handleShipName(e) { this.setData({ 'applyForm.ship_name': e.detail.value }) },
  handleShipMobile(e) { this.setData({ 'applyForm.ship_mobile': e.detail.value }) },
  // 检查参数
  handleCheckParams(){
    if (!this.data.applyForm.return_num){
      wx.showToast({ title: '请输入申请数量', icon: 'none' })
      return false
    }
    if (!this.data.applyForm.reason || this.data.applyForm.reason === '请选择申请原因'){
      wx.showToast({title: '请选择申请原因',icon:'none'})
      return false
    }
    if (!this.data.applyForm.problem_desc){
      wx.showToast({ title: '请输入问题描述！', icon: 'none' })
      return false
    }
    if (this.data.service_type === 'RETURN_GOODS'){
      if (!this.data.applyInfo.is_retrace && !this.data.applyInfo.is_retrace_balance){
        if (!this.data.applyForm.account_type) {
          wx.showToast({ title: '请选择账户类型！', icon: 'none' })
          return false
        }
        // 如果账户类型不为银行卡
        if (this.data.applyForm.account_type != 'BANKTRANSFER') {
          // 退款账号校验
          if (!this.data.applyForm.return_account) {
            wx.showToast({ title: '请输入退款账号！', icon: 'none' })
            return false
          }
        } else {
          // 银行名称校验
          if (!this.data.applyForm.bank_name) {
            wx.showToast({ title: '请输入银行名称！', icon: 'none' })
            return false
          }
          // 银行开户行校验
          if (!this.data.applyForm.bank_deposit_name) {
            wx.showToast({ title: '请输入银行开户行！', icon: 'none' })
            return false
          }
          // 银行开户名校验
          if (!this.data.applyForm.bank_account_name) {
            wx.showToast({ title: '请输入银行开户名！', icon: 'none' })
            return false
          }
          // 银行账号校验
          if (!this.data.applyForm.bank_account_number) {
            wx.showToast({ title: '请输入银行账号！', icon: 'none' })
            return false
          }
        }
      }
    }
    if (!this.data.applyForm.ship_addr) {
      wx.showToast({ title: '请输入详细地址！', icon: 'none' })
      return false
    }
    if (!this.data.applyForm.ship_name) {
      wx.showToast({ title: '请输入联系人！', icon: 'none' })
      return false
    }
    if (!this.data.applyForm.ship_mobile || !RegExp.mobile.test(this.data.applyForm.ship_mobile)) {
      wx.showToast({ title: '请输入正确格式的手机号码！', icon: 'none' })
      return false
    }
    return true
  },
  submitApplyForm(){
    if (!this.handleCheckParams()) {
      return false
    }
    this.setData({
      'applyForm.order_sn':this.data.order_sn,
      'applyForm.sku_id': this.data.sku_id,
      'applyForm.service_type': this.data.service_type,
      'applyForm.apply_vouchers':this.data.voucherText,
      'applyForm.region': this.data.regions
    })
    if (this.data.service_type === 'RETURN_GOODS'){
      API_AfterSale.applyReturnGoods(this.data.applyForm).then(()=>{
        wx.navigateBack({ delta: 2 })
        wx.showToast({title: '提交成功'})
      })
    } else if (this.data.service_type === 'CHANGE_GOODS'){
      API_AfterSale.applyChangeGoods(this.data.applyForm).then(() => {
        wx.navigateBack({ delta: 2 })
        wx.showToast({ title: '提交成功' })
      })
    } else if (this.data.service_type === 'SUPPLY_AGAIN_GOODS'){
      API_AfterSale.applyReplaceGoods(this.data.applyForm).then(() => {
        wx.navigateBack({ delta: 2 })
        wx.showToast({ title: '提交成功' })
      })
    } else {
      wx.showToast({ title: '申请的售后服务类型不正确！',icon:'none' })
      return false
    }
  }
})