import * as API_Deposit from '../../../api/deposit'
Page({
  data: {
    isPassword: ''
  },
  onLoad() {
    API_Deposit.checkPassword().then(response => {
      this.setData({isPassword: response})
    })
  }
})