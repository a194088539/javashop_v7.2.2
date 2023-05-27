import * as API_Members from '../../../../api/members.js'
import { Foundation } from '../../../../ui-utils/index'

Page({

  /**
   * 页面的初始数据
   */
  data: {
    ask_id:'',
    finished:false,
    askDetail:'',
    replyList:[],
    params:{
      page_no:1,
      page_size:10
    },
    pageCount: 0,
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false,//显示返回顶部按钮
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      ask_id:options.ask_id,
      scrollHeight: wx.getSystemInfoSync().windowHeight
    })
    this.getAskDetail()
    this.getAskReplyList()
  },
  // 获取问题详情信息
  getAskDetail(){
    API_Members.getAskDetail(this.data.ask_id).then(response=>{
      response.create_time = Foundation.unixToDate(response.create_time)
      response.reply_time = Foundation.unixToDate(response.reply_time)
      this.setData({askDetail:response})
    })
  },
  //获取问题回复列表
  getAskReplyList(){
    API_Members.getGoodsAskReplys(this.data.ask_id,this.data.params).then(response=>{
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const { data } = response
      if(data && data.length){
        data.forEach(key=>{
          key.reply_time = Foundation.unixToDate(key.reply_time)
        })
        this.data.replyList.push(...data)
        this.setData({replyList:this.data.replyList})
      }else{
        this.setData({finished:true})
      }
    })
  },
  loadMore: function () {
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getAskReplyList()
      }
    }
  },
  scroll: function (e) {
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({ showGoTop: true })
    } else {
      that.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 }) }
})