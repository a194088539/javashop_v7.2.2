import * as API_Members from '../../../../api/members'
import * as API_Trade from '../../../../api/trade'
import { Foundation, RegExp } from '../../../../ui-utils/index'
import regeneratorRuntime from '../../../../lib/wxPromise.min.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    sellerids:'',
    receipt:'',
    current_receipt_type: '',//当前选择的发票类型
    // 发票原始类型列表
    receipt_type_menu: [
      { show_detail: false, name: '增值税普通发票', status: 'ordin_receipt_status', checked: false, receipt_type: 'VATORDINARY' },
      { show_detail: false, name: '电子普通发票', status: 'elec_receipt_status', checked: false, receipt_type: 'ELECTRO' },
      { show_detail: false, name: '增值税专用发票', status: 'tax_receipt_status', checked: false, receipt_type: 'VATOSPECIAL' }
    ],
    receipt_type_list: [],//发票列表
    need_receipt: false,// 需要发票
    receiptForm: {
      receipt_title: '',//发票抬头
      receipt_content: '不开发票',//发票内容
      receipt_type: '',//发票类型
      tax_no: '',//纳税人识别号
      regions: '',
      address: '',
      member_name: '',
      member_mobile: ''
    },
    receipt_method: '订单完成后开票',//开票方式
    last_receipt_title: '',//上次选择的单位发票抬头
    receipt_title_type: 0,// 发票抬头类型 个人为0 单位为1
    // 发票内容列表 code为代号 false 未选
    receipt_contents: [
      {
        receipt_content: '商品明细',
        code: false,
        receipt_type: '' // 显示所对应的发票类型 '' 代表对应所有
      },
      {
        receipt_content: '商品类别',
        code: false,
        receipt_type: 'ELECTRO' // 显示所对应的发票类型 ELECTRO=>电子普通发票
      }
    ],
    invoiceInfo: '',//增票资质详情信息
    invoiceAddressInfo: '',//增票资质地址信息
    receipts: [],//会员发票列表
    show_receipts: false, //显示已有发票
    showAddressSelector: false, //是否显示地址选择器
    isShowtip: true,// 是否显示提示信息
    height: 0//input距离键盘的高度
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      sellerids: parseInt(options.sellerids),
      receipt: JSON.parse(options.receipt)
    })
    this.init()
  },

  /** 函数节流隐藏元素 */
  handleOutReceipt() {
    setTimeout(() => {
      this.setData({ show_receipts: false })
    }, 50)
  },
  //初始化发票信息
  init() {
    if (this.data.sellerids){
      API_Members.queryMembersReceipt(this.data.sellerids).then(async response => {
        this.data.receipt_type_list.push({ show_detail: false, name: '不开发票', status: '', checked: false, receipt_type: '' })
        const _receipt_type_list = this.data.receipt_type_menu.filter(item => response[item.status] === 1)
        this.data.receipt_type_list = [...this.data.receipt_type_list, ..._receipt_type_list]
        // 如果商家开启专票
        if (response.tax_receipt_status === 1) {
          // 获取赠票资质基础信息
          this.GET_InvoiceInfo()
          let _invoiceAddressInfo = await API_Members.queryTicketAdress()
          this.setData({ invoiceAddressInfo: _invoiceAddressInfo })
        }
        // 如果没有发票信息
        if (!this.data.receipt || !this.data.receipt.receipt_type) {
          this.data.receipt_type_list.forEach(key => {
            if (!key.receipt_type) key.checked = true
          })
          this.setData({ receipt_type_list: this.data.receipt_type_list })
        } else {//如果有自带发票信息
          this.data.receiptForm = { ...this.data.receipt }
          this.data.current_receipt_type = this.data.receipt.receipt_type
          this.data.receipt_type_list.forEach(key => {
            if (key.receipt_type === this.data.current_receipt_type) key.checked = true
          })
          if (this.data.current_receipt_type === 'VATORDINARY' || this.data.current_receipt_type === 'VATOSPECIAL') {
            const _receipt = this.data.receipt_contents.find(key => key.receipt_content === this.data.receiptForm.receipt_content)
            if (_receipt && _receipt.code !== undefined && _receipt.code !== null) {
              _receipt.code = true
            }
          }
          this.setData({
            receiptForm: this.data.receiptForm,
            receipt_contents: this.data.receipt_contents,
            current_receipt_type: this.data.current_receipt_type,
            receipt_type_list: this.data.receipt_type_list,
            need_receipt: true
          })
          this.GET_ReceiptList()
          setTimeout(() => { this.showreceipt()},1000)
        }
      })
    }
  },
  //如果存在发票信息
  showreceipt(){
    if (this.data.receipt && this.data.receipt.receipt_title) {
      this.data.receipt_type_list.forEach(item => {
        if (item.receipt_type === this.data.receipt.receipt_type) {
          item.checked = true
        } else {
          item.checked = false
        }
      })
      this.data.receiptForm = { ...this.data.receipt }
      const _receipt = this.data.receipts.find(key => key.receipt_title === this.data.receiptForm.receipt_title)
      if (_receipt && _receipt.receipt_id) {
        this.data.receiptForm.receipt_id = _receipt.receipt_id
      }
      if (this.data.receipt.receipt_type === 'VATOSPECIAL') {
        this.data.receiptForm.receipt_title = this.data.invoiceInfo.company_name || '单位'
        this.data.receiptForm.detail_addr = this.data.receipt.detail_addr
        // this.data.receiptForm.member_mobile = this.data.receipt.member_mobile
        this.data.receiptForm.member_name = this.data.receipt.member_name

        if (this.data.receipt.town_id && this.data.receipt.town_id !== 0) {
          this.data.receiptForm.regions = [this.data.receipt.province_id, this.data.receipt.city_id, this.data.receipt.county_id, this.data.receipt.town_id]
        } else {
          this.data.receiptForm.regions = [this.data.receipt.province_id, this.data.receipt.city_id, this.data.receipt.county_id]
        }
        this.data.receiptForm.address = `${this.data.receipt.province} ${this.data.receipt.city} ${this.data.receipt.county} ${this.data.receipt.town}`
        // this.handleToggleType(this.data.receipt_type_list.find(item => item.receipt_type === this.data.receipt.receipt_type))
      }
      this.data.receipt_contents.forEach(item => {
        if (item.receipt_content === this.data.receipt.receipt_content) {
          item.code = true
        } else {
          item.code = false
        }
      })
      this.setData({
        need_receipt: true,
        receipt: this.data.receipt,
        receipts: this.data.receipts,
        receiptForm: this.data.receiptForm,
        receipt_contents: this.data.receipt_contents,
        current_receipt_type: this.data.receipt.receipt_type,
        receipt_type_list: this.data.receipt_type_list
      })
    } else { // 如果不存在发票信息
      if (this.data.receipt_type_list.length) {
        this.data.receipt_type_list.forEach(key => { key.checked = false })
        this.data.receipt_type_list[0].checked = true
      }
      this.setData({
        need_receipt: false,
        current_receipt_type: '',
        receipt_type_list: this.data.receipt_type_list
      })
      // this.handleToggleType(this.data.receipt_type_list[0])
    }
    this.data.receipt_title_type = this.data.receiptForm.receipt_title === '个人' ? 0 : 1
    this.setData({ receipt_title_type: this.data.receipt_title_type })
  },
  //切换发票类型
  async handleToggleType(e) {
    let receipt = e.currentTarget.dataset.receipt
    if (receipt.checked) return
    if (this.data.invoiceInfo && this.data.invoiceInfo.status !== 'AUDIT_PASS') {
      wx.showToast({ title: '会员还未申请增票资质或者申请审核未通过', icon: 'none' })
      return
    }
    // 获取当前发票类型 并且选择当前发票相关信息
    let _current_receipt_type = receipt.receipt_type
    this.setData({ current_receipt_type: _current_receipt_type })
    // 获取会员发票列表
    await this.GET_ReceiptList()
    setTimeout(() => {
      if (_current_receipt_type === 'VATORDINARY' || _current_receipt_type === 'ELECTRO') {
        // 设置发票抬头 如果存在发票则首先选中默认发票 否则选中 个人
        const _receipt = this.data.receipts.find(key => key.is_default === 1)
        if (_receipt) {
          this.data.receiptForm = JSON.parse(JSON.stringify(_receipt))
          this.setData({ receiptForm: this.data.receiptForm, receipt_title_type: 1 })
        } else {
          this.setData({ 'receiptForm.receipt_title': '个人', receipt_title_type: 0, })
        }
      }
      if (_current_receipt_type === 'VATOSPECIAL') {
        this.data.receiptForm.receipt_title = this.data.invoiceInfo.company_name || '单位'
        this.data.receiptForm.member_mobile = this.data.invoiceAddressInfo.member_mobile
        this.data.receiptForm.detail_addr = this.data.invoiceAddressInfo.detail_addr
        this.data.receiptForm.member_name = this.data.invoiceAddressInfo.member_name
        if (this.data.invoiceAddressInfo.town_id && this.data.invoiceAddressInfo.town_id !== 0) {
          this.data.receiptForm.regions = [this.data.invoiceAddressInfo.province_id, this.data.invoiceAddressInfo.city_id, this.data.invoiceAddressInfo.county_id, this.data.invoiceAddressInfo.town_id]
        } else {
          this.data.receiptForm.regions = [this.data.invoiceAddressInfo.province_id, this.data.invoiceAddressInfo.city_id, this.data.invoiceAddressInfo.county_id]
        }
        if (this.data.invoiceAddressInfo) {
          this.data.receiptForm.address = `${this.data.invoiceAddressInfo.province} ${this.data.invoiceAddressInfo.city} ${this.data.invoiceAddressInfo.county} ${this.data.invoiceAddressInfo.town}`
        }
        this.setData({
          receipt_title_type: 1,
          receiptForm: this.data.receiptForm
        })
      }
      // 选定发票内容 如果是增值税普通发票/专用发票 内容为商品明细； 
      if (_current_receipt_type === 'VATORDINARY' || _current_receipt_type === 'VATOSPECIAL') {
        // 设置发票内容
        this.data.receipt_contents.forEach(key => key.code = false)
        this.data.receipt_contents.find(key => key.receipt_content === '商品明细').code = true
        this.setData({ receipt_contents: this.data.receipt_contents, 'receiptForm.receipt_content': '商品明细' })
      }
      //电子发票可选类别【商品明细 | 商品类别】
      if (_current_receipt_type === 'ELECTRO') {
        const default_receipt = this.data.receipts.find(item => item.is_default)
        this.data.receipt_contents.forEach(key => key.code = false)
        if (default_receipt) {
          const current_receipt = this.data.receipt_contents.find(key => key.receipt_content === default_receipt.receipt_content)
          current_receipt.code = true
          this.setData({ receipt_contents: this.data.receipt_contents })
        } else {
          this.data.receipt_contents.find(key => key.receipt_content === '商品明细').code = true
          this.setData({ receipt_contents: this.data.receipt_contents, 'receiptForm.receipt_content': '商品明细' })
        }
      }
      receipt.checked = true
      this.data.receipt_type_list.forEach(key => {
        key.checked = false
        if (key.receipt_type === receipt.receipt_type) {
          key.checked = receipt.checked
        }
      })
      this.setData({ need_receipt: !!receipt.receipt_type, receipt_type_list: this.data.receipt_type_list, isShowtip: true })
    }, 300)
  },
  //收票人
  syncMemberName(e) { this.setData({ 'receiptForm.member_name': e.detail.value }) },
  //收票人手机号
  syncMemberMobile(e) { this.setData({ 'receiptForm.member_mobile': e.detail.value }) },
  //收票人邮箱
  syncMemberEmail(e) { this.setData({ 'receiptForm.member_email': e.detail.value }) },
  //收票人详细地址
  syncDetailAddr(e) { this.setData({ 'receiptForm.detail_addr': e.detail.value }) },
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
      'receiptForm.region': obj.last_id,
      'receiptForm.address': obj.addrs
    })
  },
  // 当选择个人发票类型的时候 设置发票抬头为 '个人' 发票抬头类型为0 税号为 ''
  handlePersonReceipt() {
    this.setData({
      'receiptForm.tax_no': '',
      'receiptForm.receipt_title': '个人',
      'receiptForm.member_mobile': '',
      'receiptForm.member_email': '',
      receipt_title_type: 0
    })
  },
  // 当选择非个人发票类型的时候 匹配默认发票
  handleChooseReceipt() {
    if (this.data.current_receipt_type === 'VATOSPECIAL') {
      return
    }
    this.data.receiptForm.receipt_title = ''
    this.data.receiptForm.tax_no = ''
    let _receipt
    if (this.data.receiptForm.receipt_id && this.data.last_receipt_title) {
      _receipt = this.data.receipts.find(key => key.receipt_title === this.data.last_receipt_title)
    } else {
      _receipt = this.data.receipts.find(key => key.is_default === 1)
    }
    if (_receipt) {
      this.data.receiptForm = { ..._receipt }
      if (!_receipt.receipt_content) {
        this.data.receipt_contents.forEach(key => key.code = false)
      }
    }
    this.setData({
      receiptForm: this.data.receiptForm,
      last_receipt_title: this.data.receiptForm.receipt_title,
      receipt_title_type: 1
    })
  },
  closeTip() { this.setData({ isShowtip: false }) },
  //获取焦点
  handleFocus() { this.setData({ height: this.data.receipts.length * 30, show_receipts: true }) },
  //单位名称
  handleBindTitle(e) {
    this.setData({ 'receiptForm.receipt_title': e.detail.value })
  },
  //纳税人识别号
  handleBindTaxNo(e) { this.setData({ 'receiptForm.tax_no': e.detail.value }) },
  //选择发票抬头类型
  handleSelectReceipt(e) {
    let receipt = e.currentTarget.dataset.rep
    if (receipt.receipt_type === 'ELECTRO') {
      if (receipt.receipt_content === '商品类别') {
        this.data.receipt_contents.forEach(key => key.code = false)
        this.data.receipt_contents.find(key => key.receipt_content === '商品类别').code = true
      } else {
        this.data.receipt_contents.forEach(key => key.code = false)
        this.data.receipt_contents.find(key => key.receipt_content === '商品明细').code = true
      }
    }
    this.setData({
      show_receipts: false,
      receiptForm: { ...receipt },
      receipt_contents: this.data.receipt_contents,
      last_receipt_title: this.data.receiptForm.receipt_title
    })
  },
  //选择发票内容
  handleChooseContent(e) {
    let item = e.currentTarget.dataset.item
    item.code = true
    this.data.receipt_contents.forEach(key => {
      key.code = false
      if (key.receipt_type === item.receipt_type) {
        key.code = item.code
      }
    })
    this.setData({ 'receiptForm.receipt_content': item.receipt_content, receipt_contents: this.data.receipt_contents })
  },
  /** 确认发票 */
  handleConfirmReceipt() {
    if (!this.data.need_receipt) {
      API_Trade.cancelReceipt().then(() => {
        wx.navigateBack()
      })
      return false
    }
    const _speparams = {
      receipt_method: this.data.receipt_method,
      bank_name: this.data.invoiceInfo.bank_name,
      bank_account: this.data.invoiceInfo.bank_account,
      reg_addr: this.data.invoiceInfo.register_address,
      reg_tel: this.data.invoiceInfo.register_tel,
      member_name: this.data.receiptForm.member_name,
      address: this.data.receiptForm.address,
      detail_addr: this.data.receiptForm.detail_addr,
      tax_no: this.data.invoiceInfo.taxpayer_code
    }
    const _elecparams = {
      member_mobile: this.data.receiptForm.member_mobile,
      member_email: this.data.receiptForm.member_email || ''
    }
    let params = {
      // 发票类型
      receipt_type: this.data.current_receipt_type,
      // 发票抬头
      receipt_title: this.data.receiptForm.receipt_title,
      // 发票内容
      receipt_content: this.data.receiptForm.receipt_content,
      // 税号
      tax_no: this.data.receiptForm.tax_no
    }
    if (this.data.current_receipt_type === 'ELECTRO') {
      params = {
        ...params,
        ..._elecparams
      }
    }
    if (this.data.current_receipt_type === 'VATOSPECIAL') {
      let region = this.data.receiptForm.region || this.data.receiptForm.regions[this.data.receiptForm.regions.length - 1] || ''
      params = {
        region,
        ...params,
        ..._elecparams,
        ..._speparams
      }
    }
    if (!params.receipt_title) {
      wx.showToast({ title: '发票抬头不能为空', icon: 'none' })
      return
    }
    if (params.receipt_title !== '个人' && params.tax_no && !RegExp.TINumber.test(params.tax_no)) {
      wx.showToast({ title: '纳税人识别号不正确', icon: 'none' })
      return
    }
    if (params.member_mobile && !RegExp.mobile.test(params.member_mobile)){
      wx.showToast({title: '收票人手机格式不正确',icon:'none'})
      return
    }
    if (params.member_email && !RegExp.email.test(params.member_email)) {
      wx.showToast({ title: '收票人邮箱格式不正确', icon: 'none' })
      return
    }
    // 验证是否相同
    API_Trade.setRecepit(params).then(() => {
      if (this.data.current_receipt_type !== 'VATOSPECIAL' && params.receipt_title !== '个人') {
        const isEdit = !!(this.data.receiptForm.receipt_id && this.data.receipts.find(key => key.receipt_title === this.data.receiptForm.receipt_title))
        Promise.all([isEdit ? API_Members.editReceipt(this.data.receiptForm.receipt_id, params) : API_Members.addReceipt(params)]).then(() => {
          wx.navigateBack()
          this.GET_ReceiptList()
          wx.showToast({ title: '设置成功!' })
        })
      } else {
        wx.navigateBack()
        wx.showToast({ title: '设置成功!' })
      }
    })
  },
  /** 获取发票列表 */
  GET_ReceiptList() {
    if (this.data.current_receipt_type === 'VATORDINARY' || this.data.current_receipt_type === 'ELECTRO') {
      API_Members.getReceipts(this.data.current_receipt_type).then(response => {
        this.setData({ receipts: response })
      })
    }
  },
  /**获取增票资质详情信息 */
  GET_InvoiceInfo() {
    API_Members.queryInvoiceInfo().then(response => {
      this.setData({ invoiceInfo: response })
    })
  }
})