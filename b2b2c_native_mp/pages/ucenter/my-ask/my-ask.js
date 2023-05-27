const app = getApp();
let util = require('../../../utils/util.js')
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index.js'

Page({
  data: {
    activeTab: 0,
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    finished: false,//是否已经加载完毕
    showGoTop: false,//显示返回顶部按钮
    pageCount: 0,
    delBtnWidth:120,
    //我的提问
    askList: [],
    ask_params: {
      page_no: 1,
      page_size: 10,
    },
    //我的回答
    answerList:[],
    answer_params:{
      page_no: 1,
      page_size: 10,
      reply_status:'YES'
    },
    //邀请我答
    invitationList:[],
    invitation_params:{
      page_no:1,
      page_size:10,
      reply_status:'NO'
    }
  },
  onLoad: function (options) {
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight - 42,
      activeTab: parseInt(options.active) || 0,
      delBtnWidth: util.getEleWidth(this.data.delBtnWidth)
    })
  },
  onShow() {
    if(this.data.activeTab === 0){
      this.setData({ askList:[],finished:false,'ask_params.page_no':1})
      this.getAskList()
    } else if (this.data.activeTab === 1){
      this.setData({ answerList: [], finished: false, 'answer_params.page_no': 1 })
      this.getAnswerList()
    }else if(this.data.activeTab === 2){
      this.setData({ invitationList: [], finished: false, 'invitation_params.page_no': 1 })
      this.getinvitationList()
    }
  },
  //状态切换
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index);
    if (index !== this.data.activeTab){
      this.setData({
        activeTab: index,
        finished: false,
        scrollTop: 0,
        pageCount: 0,
      })
      if (index === 0) {
        this.setData({ askList: [], 'ask_params.page_no': 1 })
        this.getAskList()
      } else if (index === 1) {
        this.setData({ answerList: [], 'answer_params.page_no': 1 })
        this.getAnswerList()
      } else if (index === 2) {
        this.setData({ invitationList: [], 'invitation_params.page_no': 1 })
        this.getinvitationList()
      }
    }
  },
  //我的提问
  getAskList() {
    API_Members.getConsultations(this.data.ask_params).then(response=>{
      let _pageCount = Math.ceil(response.data_total / this.data.ask_params.page_size)
      this.setData({ pageCount: _pageCount })
      const { data } =  response
      if(data && data.length){
        data.forEach(key=>{key.create_time = Foundation.unixToDate(key.create_time)})
        this.data.askList.push(...data)
        this.setData({askList: this.data.askList})
      }else{
        this.setData({ finished: true})
      }
    })
  },
  //我的回答
  getAnswerList(){
    API_Members.getAnswers(this.data.answer_params).then(response => {
      let _pageCount = Math.ceil(response.data_total / this.data.answer_params.page_size)
      this.setData({ pageCount: _pageCount })
      const { data } = response
      if (data && data.length) {
        this.data.answerList.push(...data)
        this.setData({ answerList: this.data.answerList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  //邀请我答
  getinvitationList(){
    API_Members.getAnswers(this.data.invitation_params).then(response => {
      let _pageCount = Math.ceil(response.data_total / this.data.invitation_params.page_size)
      this.setData({ pageCount: _pageCount })
      const { data } = response
      if (data && data.length) {
        this.data.invitationList.push(...data)
        this.setData({ invitationList: this.data.invitationList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  //详情
  handleAskDetail(e){
    const ask_id = e.currentTarget.dataset.ask_id
    wx.navigateTo({
      url: '/pages/goods/goods-ask-list/ask-detail/ask-detail?ask_id=' + ask_id,
    })
  },
  //去回答
  handleReplyAsk(e){
    wx.navigateTo({
      url: '/pages/ucenter/reply-answer/reply-answer?ask_id=' + e.currentTarget.dataset.ask_id,
    })
  },
  //删除资询
  handleDeleteAsk(e){
    var that = this
    const ask_id = e.currentTarget.dataset.ask_id
    wx.showModal({
      title: '提示',
      content: '确定要删除这条消息吗?',
      confirmColor: '#f42424',
      success: function (res) {
        if (res.confirm) {
          API_Members.deleteAsk(ask_id).then(() => {
            that.setData({ askList: [], finished: false, 'ask_params.page_no': 1 })
            that.getAskList()
            wx.showToast({ title: '删除成功' })
          })
        }
        that.data.askList.forEach(key => { key.txtStyle = 'left:0px' })
        that.setData({ askList: that.data.askList })
      }
    })
  },
  //删除回复
  handleDeleteAnswer(e){
    var that = this
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定要删除这条消息吗?',
      confirmColor: '#f42424',
      success: function (res) {
        if (res.confirm) {
          API_Members.deleteAnswers(id).then(() => {
            that.setData({ answerList: [], finished: false, 'answer_params.page_no': 1 })
            that.getAnswerList()
            wx.showToast({ title: '删除成功' })
          })
        }
        that.data.answerList.forEach(key => { key.txtStyle = 'left:0px' })
        that.setData({ answerList: that.data.answerList })
      }
    })
  },
  loadMore() {
    if (!this.data.finished) {
      if(this.data.activeTab === 0){
        this.setData({ "ask_params.page_no": this.data.ask_params.page_no += 1 })
        if (this.data.pageCount >= this.data.ask_params.page_no) {
          this.getAskList();
        }
      }else if(this.data.activeTab === 1){
        this.setData({ "answer_params.page_no": this.data.answer_params.page_no += 1 })
        if (this.data.pageCount >= this.data.answer_params.page_no) {
          this.getAnswerList();
        }
      }else if (this.data.activeTab === 2) {
        this.setData({ "invitation_params.page_no": this.data.invitation_params.page_no += 1 })
        if (this.data.pageCount >= this.data.invitation_params.page_no) {
          this.getinvitationList();
        }
      }
    }
  },
  scroll: function (e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {
      this.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 }) },
  touchS(e) {if (e.touches.length == 1) {this.setData({ startX: e.touches[0].clientX })}},
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
      if(this.data.activeTab === 0){
        var list = this.data.askList
        list.forEach(key => { key.txtStyle = 'left:0px' })
        list[index].txtStyle = txtStyle;
        this.setData({ askList: list });
      }else{
        var list = this.data.answerList
        list.forEach(key => { key.txtStyle = 'left:0px' })
        list[index].txtStyle = txtStyle;
        this.setData({ answerList: list });
      }
    }
  },
  touchE(e) {
    if (e.changedTouches.length == 1) {
      var endX = e.changedTouches[0].clientX;
      var disX = this.data.startX - endX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = disX > delBtnWidth / 2 ? "left:-" + delBtnWidth + "px" : "left:0px";
      var index = e.currentTarget.dataset.index;
      if (this.data.activeTab === 0) {
        var list = this.data.askList
        list.forEach(key => { key.txtStyle = 'left:0px' })
        list[index].txtStyle = txtStyle;
        this.setData({ askList: list });
      } else {
        var list = this.data.answerList
        list.forEach(key => { key.txtStyle = 'left:0px' })
        list[index].txtStyle = txtStyle;
        this.setData({ answerList: list });
      }
    }
  }
}) 