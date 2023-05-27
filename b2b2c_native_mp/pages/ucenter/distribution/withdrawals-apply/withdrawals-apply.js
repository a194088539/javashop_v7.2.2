import * as API_Distribution from '../../../../api/distribution.js'
import { Foundation,RegExp } from '../../../../ui-utils/index.js'
let util = require('../../../../utils/util.js') 

Page({

  /**
   * 页面的初始数据
   */
  data: {
    /** 申请表单 */
    applyWithdrawalsForm: {
      /** 可提现金额 */
      can_rebate: 0,

      /** 提现金额 */
      apply_money: 0,

      /** 备注 */
      remark: ''
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getWithdrawalsCanRebate()
  },
  getWithdrawalsCanRebate(){
    API_Distribution.getWithdrawalsCanRebate().then(response => {
      this.setData({ 'applyWithdrawalsForm.can_rebate': response.message, 'applyWithdrawalsForm.apply_money': 0, 'applyWithdrawalsForm.remark': '' })
    })
  },
  changeApplyMoney(e) { this.setData({'applyWithdrawalsForm.apply_money':e.detail.value})},
  changeRemark(e) { this.setData({'applyWithdrawalsForm.remark':e.detail.value})},
  //提现申请
  handleApplyWithdrawals: util.throttle(function(){
    const { can_rebate, apply_money, remark } = this.data.applyWithdrawalsForm
    if (!apply_money) {
      wx.showToast({ title: '提现金额不能为空或0', icon: 'none' })
      return
    } else if (!RegExp.money.test(apply_money)) {
      wx.showToast({ title: '请输入正整数或两位小数', icon: 'none' })
      return
    } else if (parseInt(apply_money) < 1) {
      wx.showToast({ title: '提现金额最少1元', icon: 'none' })
      return
    } else if (parseFloat(apply_money) > parseFloat(can_rebate)) {
      wx.showToast({ title: '已超可提现金额', icon: 'none' })
      return
    }
    const that = this
    const params = { apply_money, remark }
    API_Distribution.applyWithdrawals(params).then(() => {
      wx.showToast({ 
        title: '已提交申请，请耐心等待。。。',
        icon: 'none',
        mask: true,
        duration: 2000,
        success(res) {
          that.getWithdrawalsCanRebate()
        }
      })
    })
  })
})