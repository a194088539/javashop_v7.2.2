import * as API_Members from '../../../../api/members.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    history_id:0,
    receiptInfo:'', //发票详情信息
    receiptInfoOrigin:{} //原始响应数据
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({history_id:options.history_id})
    this.getReceipt()
  },
  /**
   * 获取发票信息
   */
  getReceipt(){
    API_Members.queryReceiptInfoDetail(this.data.history_id).then(response=>{
      this.setData({ receiptInfoOrigin:response})
      let _receipt = []
      const logi = []
      if(response.status === 1){
        if(response.receipt_type !== 'ELECTRO'){
          logi.push({
            label:'物流公司',value:response.logi_name,
            label:'快递单号',value:response.logi_code
          })
        }else{
          logi.push({
            label:'查看发票',value:response.elec_file_list[0]
          })
        }
      }
      switch (response.receipt_type){
        case 'ELECTRO':
          _receipt = [
            {label:'发票类型',value:this.receiptType(response.receipt_type)},
            {label:'发票内容',value:response.receipt_content},
            {label:'发票抬头',value:response.receipt_title},
            {label:'发票税号',value:response.tax_no || '无'},
            {label:'开票状态',value:response.receipt_status === 0 ? '商家暂未开具发票':'商家已开具发票'},
            ...logi
          ];
          break;
        case 'VATORDINARY':
          _receipt = [
            { label: '发票类型', value: this.receiptType(response.receipt_type) },
            { label: '发票内容', value: response.receipt_content },
            { label: '发票抬头', value: response.receipt_title },
            { label: '发票税号', value: response.tax_no || '无' },
            { label: '开票状态', value: response.status === 0 ? '商家暂未开具发票' : '商家已开具发票' },
            { label: '收票人', value: response.member_name },
            { label: '联系方式', value: response.member_mobile },
            { label: '收票地址', value: `${response.province || ''}${response.city || ''}${response.county || ''}${response.town || ''}${response.detail_addr || ''}` },
            ...logi
          ];
          break;
        case 'VATOSPECIAL':
          _receipt = [
            { label: '发票类型', value: this.receiptType(response.receipt_type) },
            { label: '发票内容', value: response.receipt_content },
            { label: '单位名称', value: response.receipt_title },
            { label: '纳税人识别号', value: response.tax_no || '无' },
            { label: '注册地址', value: response.reg_addr },
            { label: '注册电话', value: response.reg_tel },
            { label: '开户银行', value: response.bank_name },
            { label: '银行账户', value: response.bank_account },
            { label: '开票状态', value: response.status === 0 ? '商家暂未开具发票' : '商家已开具发票' },
            { label: '收票人', value: response.member_name },
            { label: '联系方式', value: response.member_mobile },
            { label: '收票地址', value: `${response.province || ''}${response.city || ''}${response.county || ''}${response.town || ''}${response.detail_addr || ''}` },
            ...logi
          ];
          break;
      }
      this.setData({receiptInfo:_receipt})
    })
  },
  /**
   * 发票类型
   */
  receiptType(type) {
    switch (type) {
      case 'VATORDINARY': return '增值税普通发票'
      case 'ELECTRO': return '电子普通发票'
      case 'VATOSPECIAL': return '增值税专用发票'
      default: return ''
    }
  },
  /**
   * 查看电子发票
   */
  previewImage(e){
    let url  = e.currentTarget.dataset.src.replace(/http\:\/\//, 'https://')
    let isPdf = url.substring(url.lastIndexOf('.')) === '.pdf'
    if (isPdf) {
      wx.downloadFile({
        url: url,
        filePath: wx.env.USER_DATA_PATH + '/receipt.pdf',
        success(res) {
          wx.openDocument({
            filePath: res.filePath,
            success(res) {
              console.log(res)
            }
          })
        }
      })
    } else {
      wx.previewImage({urls: [url]})
    }
  }
})