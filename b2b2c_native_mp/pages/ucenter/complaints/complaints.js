import * as API_Complaint from '../../../api/complaint'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    params: {
      page_no: 1,
      page_size: 10
    },
    finished: false,
    showGoTop: false,
    // 交易投诉列表数据
    complaintData: [],
    // 导航数据
    navList: [
      { title: '全部投诉', tag: '' },
      { title: '进行中', tag: 'COMPLAINING' },
      { title: '已完成', tag: 'COMPLETE' },
      { title: '已撤销', tag: 'CANCELED' }
    ],
    navActive: 0,
    scrollTop: 0,
    scrollHeight: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight - 42
    })
  },
  onShow() {
    this.setData({complaintData: [],finished: false,'params.page_no': 1})
    this.getComplaintList()
  },
  // 导航变化
  switchNav(e) {
    const index = e.currentTarget.dataset.index
    const tag = e.currentTarget.dataset.tag
    this.setData({
      complaintData: [],
      finished: false,
      showGoTop: false,
      navActive: index,
      'params.page_no': 1,
      'params.tag':tag
    })
    this.getComplaintList()
  },
  // 撤销投诉
  handleCancel(e) {
    const id = e.currentTarget.dataset.id
    API_Complaint.cancelComplaint(id).then(res => {
      wx.showToast({title: '撤销投诉成功！'})
      this.setData({complaintData: [],finished: false,'params.page_no': 1})
      this.getComplaintList()
    })
  },
  /** 获取交易投诉列表 */
  getComplaintList() {
    let params = {}
    for (let key in this.data.params) {
      if (this.data.params[key]) {
        params[key] = this.data.params[key]
      }
    }
    API_Complaint.getComplaintList(params).then(response => {
      const data  = response.data
      if (data && data.length) {
        this.data.complaintData.push(...data)
        this.setData({complaintData: this.data.complaintData})
      } else {
        this.setData({finished:true})
      }
    })
  },
  loadMore() {
    if (!this.data.finished) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      this.getComplaintList()
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
  goTop() {this.setData({scrollTop: 0})}
})