import * as API_Live from '../../api/live'
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
    liveVideoList: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getLiveVideoList()
  },
  /** 获取直播间列表 */
  getLiveVideoList() {
    API_Live.getLiveVideoRoomList(this.data.params).then(response => {
      const { data } = response
      if (data && data.length) {
        this.data.liveVideoList.push(...data)
        this.setData({ liveVideoList: this.data.liveVideoList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  /** 点击进入直播间 */
  handleLiveVideo(e) {
    const roomId = e.currentTarget.dataset.roomid
    wx.navigateTo({ url: `plugin-private://wx2b03c6e691cd7370/pages/live-player-plugin?room_id=${roomId}` })
  }
})