import * as API_AfterSale from '../../../../api/after-sale.js'
import { Foundation } from '../../../../ui-utils/index.js'
Page({
  data: {
    logList:[]
  },
  onLoad: function (options) {
    API_AfterSale.getServiceLogs(options.service_sn).then(response=>{
      response.forEach(item=>{
        item.log_time = Foundation.unixToDate(item.log_time)
      })
      this.setData({logList:response})
    })
  }
})