import * as API_Message from '../../../api/message'

Page({
  data: {
    no_read:''
  },
  onShow(){
    this.getNoReadMessageNum()
  },
  getNoReadMessageNum(){
    API_Message.getNoReadMessageNum().then(response=>{
      this.setData({no_read:response})
    })
  }
})