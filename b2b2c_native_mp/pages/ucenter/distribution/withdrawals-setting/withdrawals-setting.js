import * as API_Distribution from '../../../../api/distribution.js'
import { Foundation,RegExp } from '../../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    /** 申请表单 */
    setWithdrawalsForm: {
      /** 户名 */
      member_name: '',
      /** 银行名称 */
      bank_name: '',
      /** 开户行号 */
      opening_num: '',
      /** 银行卡号 */
      bank_card: ''
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    API_Distribution.getWithdrawalsParams().then(response=>{
      this.setData({
        setWithdrawalsForm: { ...response}
      })
    })
  },
  changeMemberName(e) { this.setData({'setWithdrawalsForm.member_name':e.detail.value})},
  changeBankName(e) { this.setData({'setWithdrawalsForm.bank_name':e.detail.value})},
  changeOpeningNum(e) { this.setData({'setWithdrawalsForm.opening_num':e.detail.value})},
  changeBankCard(e) { this.setData({'setWithdrawalsForm.bank_card':e.detail.value})},
  // 保存设置
  handleReserveWithdrawalsParams(){
    const { member_name,bank_name,opening_num,bank_card } = this.data.setWithdrawalsForm
    // 户名校验
    if(!member_name){
      wx.showToast({title: '户名不能为空',icon:'none'})
      return
    }else if(member_name.length > 20){
      wx.showToast({ title: '户名长度最多为20个字符', icon: 'none' })
      return
    }
    // 所属银行
    if(!bank_name){
      wx.showToast({ title: '所属银行不能为空', icon: 'none' })
      return
    } else if (bank_name.length > 20) {
      wx.showToast({ title: '所属银行长度最多为20个字符', icon: 'none' })
      return
    }
    //开户行号
    if (!opening_num) {
      wx.showToast({ title: '开户行号不能为空', icon: 'none' })
      return
    } else if (opening_num.length > 20) {
      wx.showToast({ title: '开户行号长度最多为20个字符', icon: 'none' })
      return
    } else if (!RegExp.Integer.test(opening_num)) {
      wx.showToast({ title: '开户行号必须为数字', icon: 'none' })
      return
    }
    //银行卡号
    if (!bank_card) {
      wx.showToast({ title: '银行卡号不能为空', icon: 'none' })
      return
    } else if (bank_card.length > 20) {
      wx.showToast({ title: '银行卡号长度最多为20个字符', icon: 'none' })
      return
    } else if (!RegExp.Integer.test(bank_card)) {
      wx.showToast({ title: '银行卡号必须为数字', icon: 'none' })
      return
    }
    API_Distribution.reserveWithdrawalsParams(this.data.setWithdrawalsForm).then(response=>{
      wx.showToast({ title: '保存成功'})
    })
  }
})