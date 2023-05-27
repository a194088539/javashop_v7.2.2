import * as API_Distribution from '../../../../api/distribution.js'
import { Foundation } from '../../../../ui-utils/index.js'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    withdrawalsList:[],
    finished:false,
    params:{
      page_no:1,
      page_size:20
    },
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    //当前折叠面板名称
    showactiveNames: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({scrollHeight: wx.getSystemInfoSync().windowHeight})
    this.getWithdrawalsList()
  },
  isshowactiveNames(e) {
    const index = e.currentTarget.dataset.index
    this.data.withdrawalsList[index].showactiveNames = !this.data.withdrawalsList[index].showactiveNames
    this.setData({ withdrawalsList: this.data.withdrawalsList })
  },
  withdraealsStatus(val){
    switch(val){
      case 'APPLY':return '申请中'
        break;
      case 'TRANSFER_ACCOUNTS': return '已转账'
        break;
      case 'VIA_AUDITING': return '审核通过'
        break;
      case 'FAIL_AUDITING': return '审核拒绝'
        break;  
    }
  },
  loadMore: function () {
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      this.getWithdrawalsList()
    }
  },
  //获取提现记录
  getWithdrawalsList(){
    API_Distribution.getWithdrawalsList(this.data.params).then(response=>{
      const { data } = response
      if (data && data.length){
        data.forEach(key=>{
          key.showactiveNames = false
          key.apply_time = Foundation.unixToDate(key.apply_time)
          key.apply_money = Foundation.formatPrice(key.apply_money)
          key.status = this.withdraealsStatus(key.status)
        })
        this.data.withdrawalsList.push(...data)
        this.setData({ withdrawalsList: this.data.withdrawalsList })
      }else{
        this.setData({ finished: true })
      }
    })
  },
  //查看详情
  lookDetails(e){
    wx.navigateTo({
      url: '/pages/ucenter/distribution/withdrawals-historyDetail/withdrawals-historyDetail?item=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  }
})