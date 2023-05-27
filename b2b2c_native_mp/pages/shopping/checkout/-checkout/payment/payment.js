/**
 * 结算页
 * 支付配送组件
 */
import * as API_Trade from '../../../../../api/trade'
Component({
  properties: {
    // 支付方式
    paymentType: String,
    // 收货时间
    receiveTime: String,
    // 是否显示
    show: Boolean,
    //是否是拼團
    isAssemble:Boolean
  },
  data: {
    payment_type: '',
    receive_time: '',
  },
  // 数据监听器
  observers: {
    show(newVal) {
      if(newVal){ this.selectComponent('#bottomFrame').showFrame();}
      this.setData({
        payment_type: (this.data.paymentType || 'ONLINE'),
        receive_time: (this.data.receiveTime || '任意时间')
      })
    }
  },
  methods: {
    // 关闭弹窗
    handleClose() {
      this.selectComponent('#bottomFrame').hideFrame();
    },
    // 选择支付方式
    handleChoosePayment(e) {
      this.setData({ payment_type: e.target.dataset.payment })
    },

    // 选择配送时间
    handleChooseTime(e) {
      this.setData({ receive_time: e.target.dataset.receivetime })
    },

    // 保存支付配送信息
    handleSavePayment() {
      const { payment_type, receive_time } = this.data
      Promise.all([
        API_Trade.setPaymentType(payment_type),
        API_Trade.setReceiveTime(receive_time)
      ]).then(() => {
        this.selectComponent('#bottomFrame').hideFrame();
        this.triggerEvent('changed', { payment_type, receive_time })
      })
    }
  }
})
