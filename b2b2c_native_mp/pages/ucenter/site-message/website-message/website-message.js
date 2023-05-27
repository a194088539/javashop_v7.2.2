const app = getApp();
let util = require('../../../../utils/util.js')
import * as API_Message from '../../../../api/message'
import { Foundation } from '../../../../ui-utils/index.js'
import regeneratorRuntime from '../../../../lib/wxPromise.min.js'

Page({
  data: {
    showType: 0,
    id: 0,
    messageList: [],
    params: {
      page_no: 1,
      page_size: 20,
      read:0
    },
    showGoTop: false,
    finished: false,
    scrollTop: 0,//滚动高度
    scrollHeight: '',
    delBtnWidth:120
  },
  onLoad: function (options) {
    this.getMessageList();
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight - 42,
      'params.read': options.read,
      delBtnWidth: util.getEleWidth(this.data.delBtnWidth)
    })
  },
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index)
    if (index !== this.data.showType) {
      this.setData({
        showType: index,
        'params.page_no': 1,
        'params.read': index,
        messageList: []
      })
      this.getMessageList(true);
    }
  },
  getMessageList: function (reset = false) {
    if (reset) {
      this.setData({
        "params.page_no": 1,
        finished: false,
        messageList: []
      })
    }
    const params = JSON.parse(JSON.stringify(this.data.params))
    API_Message.getMessages(params).then(async response => {
      const data = response.data
      if (data && data.length) {
        data.forEach(key => {
          key.send_time = Foundation.unixToDate(key.send_time)
        })
        if (params.read === 0) {
          const ids = data.map(item => item.id).join(',')
          await API_Message.messageMarkAsRead(ids)
        }
        this.data.messageList.push(...data)
        this.setData({messageList: this.data.messageList})
      } else {
        this.setData({ finished: true })
      }
    })
  },
  //删除消息
  handleDeleteMessage(e) {
    var that = this
    const message_id = e.currentTarget.dataset.message_id
    wx.showModal({
      title: '提示',
      content: '确定要删除这条消息吗?',
      confirmColor: '#f42424',
      success: function (res) {
        if (res.confirm) {
          API_Message.deleteMessage(message_id).then(() => {
            that.getMessageList(true)
            wx.showToast({ title: '删除成功' })
          })
        }
        that.data.messageList.forEach(key => { key.txtStyle = 'left:0px' })
        that.setData({ messageList: that.data.messageList })
      }
    })
  },
  loadMore () {
    this.setData({ "params.page_no": this.data.params.page_no + 1 })
    if (!this.data.finished){
      this.getMessageList()
    }
  },
  scroll(e) {
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({showGoTop: true})
    } else {
      that.setData({showGoTop: false})
    }
  },
  //返回顶部
  goTop() {this.setData({ scrollTop: 0 })},
  touchS(e) { if (e.touches.length == 1) { this.setData({ startX: e.touches[0].clientX }) } },
  touchM(e) {
    if (e.touches.length == 1) {
      var moveX = e.touches[0].clientX;
      var disX = this.data.startX - moveX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = "";
      if (disX == 0 || disX < 0) {
        txtStyle = "left:0px";
      } else if (disX > 0) {
        txtStyle = "left:-" + disX + "px";
        if (disX >= delBtnWidth) {
          txtStyle = "left:-" + delBtnWidth + "px";
        }
      }
      var index = e.currentTarget.dataset.index; 
      var list = this.data.messageList
      list.forEach(key => { key.txtStyle = 'left:0px' })
      list[index].txtStyle = txtStyle;
      this.setData({ messageList: list });
    }
  },
  touchE(e) {
    if (e.changedTouches.length == 1) {
      var endX = e.changedTouches[0].clientX;
      var disX = this.data.startX - endX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = disX > delBtnWidth / 2 ? "left:-" + delBtnWidth + "px" : "left:0px";
      var index = e.currentTarget.dataset.index;
      var list = this.data.messageList
      list.forEach(key => { key.txtStyle = 'left:0px' })
      list[index].txtStyle = txtStyle;
      this.setData({ messageList: list });
    }
  }
})