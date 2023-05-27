const app = getApp();
import * as API_Members from '../../../api/members'
import { Foundation } from '../../../ui-utils/index.js'

Page({
  data: {
    showType: 0,
    points: {
      consum_point:'获取中...',
      grade_point:'获取中...'
    },
    params: {
      page_no: 1,
      page_size: 20
    },
    pointsList:[],
    finished: false,
    showGoTop: false,
    scrollTop: 0,//滚动高度
    scrollHeight: '',
    pageCount: 0,
  },
  switchTab(event) {
    let index = parseInt(event.currentTarget.dataset.index)
    if (index === 0 && this.data.points.consum_point === '获取中...'){
      this.getPoints()
    }
    if (index === 1 && !this.data.pointsList.length){
      this.setData({
        'params.page_no': 1,
        pointsList: [],
        finished: false
      })
      this.getPointsData()
    }
    this.setData({showType: index,})
  },

  onLoad: function (options) {
    this.data.showType === 0 ? this.getPoints() : this.getPointsData()
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight - 42
    })
  },
  getPoints(){
    API_Members.getPoints().then(response=>{
      this.setData({points:response})
    })
  },
  getPointsData(){
    API_Members.getPointsData(this.data.params).then(response=>{
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const data = response.data
      if(data && data.length){
        data.forEach(key=>{
          key.time = Foundation.unixToDate(key.time)
          key.grade_point = key.grade_point === 0 ? 0 : (key.grade_point_type === 0 ? `-${key.grade_point}` : `+${key.grade_point}`)
          key.consum_point = key.consum_point === 0 ? 0 : key.consum_point_type === 0 ? `-${key.consum_point}` : `+${key.consum_point}`
        })
        this.setData({ pointsList: this.data.pointsList.concat(data)})
      }else{
        this.setData({ finished: true })
      }
    })
  },

  loadMore: function () {
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no + 1 })
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getPointsData()
      }
    }
  },
  scroll: function (e) {
    let that = this
    if (e.detail.scrollTop > 200) {
      that.setData({showGoTop: true})
    } else {
      that.setData({showGoTop: false})
    }
  },
  //返回顶部
  goTop: function () { this.setData({ scrollTop: 0 })}
})