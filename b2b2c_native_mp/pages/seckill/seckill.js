import regeneratorRuntime from '../../lib/wxPromise.min.js'
import * as API_Promotions from '../../api/promotions'
import { Foundation } from '../../ui-utils/index'
let util = require('../../utils/util.js') 

Page({
  data: {
    finished: false,
    timeLines: '', // 时间段
    timeLineIndex: 0, // 当前选择的时间段
    times: [],
    timesText: [],
    goodsList: [], //抢购商品
    params: {
      page_no: 1,
      page_size: 10
    },
    onlyOne: false,
    seckillIsStart:false,
    pageCount: 0,
    height:''
  },
  onShow() {
    this.setData({
      'params.page_no':1,
      timeLineIndex:0,
      goodsList:[],
      height: wx.getSystemInfoSync().windowHeight - 70
    })
    this.GET_TimeLine()
  },
  /** 加载数据 */
  loadData() {
    this.setData({ 'params.page_no': this.data.params.page_no += 1 })
    if (this.data.pageCount >= this.data.params.page_no) {
      this.GET_TimeLineGoods()
    }
  },
  toseckill: util.throttle(function(e){
    const goods_id = e.currentTarget.dataset.goodid
    const sku_id = e.currentTarget.dataset.skuid
    const goods_precentsec = e.currentTarget.dataset.goods_precentsec
    if(goods_precentsec !== '100'){
      wx.navigateTo({url: '/pages/goods/goods?goods_id=' + goods_id + '&sku_id=' + sku_id})
    }
  }),
  /** 时间段被选中 */
  handleClickTimeLine(e) {
    const timeLineIndex = e.currentTarget.dataset.timelineindex
    const { timeLines } = this.data
    const timeLine = timeLines[timeLineIndex]
    timeLines.map((item, index) => {
      item.active = index === timeLineIndex
      return item
    })
    const _seckillIsStart = timeLines.filter(item => item.active)
    if (_seckillIsStart[0].distance_time === 0) {
      this.setData({ seckillIsStart: true })
    }else{
      this.setData({ seckillIsStart: false })
    }
    if(timeLineIndex !== this.data.timeLineIndex){
      this.setData({
        timeLineIndex: timeLineIndex,
        finished: false,
        goodsList: [],
        'params.page_no': 1,
        'params.range_time': timeLine.time_text
      })
      this.GET_TimeLineGoods() 
    }
  },
  /** 开始倒计时 */
  startCountDown() {
    this.interval = setInterval(() => {
      const { times, timesText } = this.data
      for (let i = 0; i < times.length; i ++) {
        if (i === 0 && times[i] === 0) {
          clearInterval(this.interval)
          wx.showModal({
            title: '提示',
            showCancel:false,
            confirmColor:'#f42424',
            content: '新的限时抢购开始啦，请确认查看!',
            success(res) {
              if (res.confirm) {
                if (getCurrentPages().length != 0) {
                  //刷新当前页面的数据
                  getCurrentPages()[getCurrentPages().length - 1].onShow()
                }
              }
            }
          })
          break;
        }
        times[i] -= 1
        const timeText = Foundation.countTimeDown(times[i])
        this.data.timesText[i] = Foundation.countTimeDown(times[i])
        this.setData({timesText: this.data.timesText})
      }
      this.setData({ times: times })
    }, 1000)
  },

  /** 获取时间线 */
  GET_TimeLine() {
    API_Promotions.getSeckillTimeLine().then(response => {
      if (!response || !response.length) {
        wx.showModal({
          title: '提示',
          showCancel: false,
          confirmColor: '#f42424',
          content: '暂时还没有限时抢购正在进行，去别处看看吧！',
          success(res) {
            if(res.confirm) {
              wx.switchTab({ url: '/pages/home/home' })
            }
          }
        })
        return false
      }
      response = response.sort((x, y) => (Number(x.time_text) - Number(y.time_text)))
      response = response.slice(0, 5)
      this.setData({ 'params.range_time': response[0].time_text})
      const times = []
      const timesText = []
      const onlyOne = response.length === 1
      this.setData({onlyOne:onlyOne})
      response.map((item, index) => {
        item.active = index === 0
        if (item.distance_time === 0 && index === 0) {
          if (onlyOne) {
            times.push(Foundation.theNextDayTime())
          } else {
            times.push(response[1].distance_time)
          }
        } else {
          times.push(item.distance_time)
        }
        timesText.push({ hours: '00', minutes: '00', seconds: '00' })
        return item
      })
      const _seckillIsStart = response.filter(item=>item.active)
      if(_seckillIsStart[0].distance_time === 0){
        this.setData({seckillIsStart:true})
      }
      this.setData({ times,timesText, timeLines:response })
      this.startCountDown()
      this.GET_TimeLineGoods()
    })
  },
  /** 获取对应时刻的商品 */
  GET_TimeLineGoods() {
    API_Promotions.getSeckillTimeGoods(this.data.params).then(response => {
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const { data } = response
      if (data && data.length) {
        data.forEach(key => {
          key.seckill_price = Foundation.formatPrice(key.seckill_price)
          key.original_price = Foundation.formatPrice(key.original_price)
          // 计算销售百分比
          const _precent_sec = (key.sold_num / (key.sold_num + key.sold_quantity) * 100).toFixed(0)
          key.precent_sec = _precent_sec
        })
        this.data.goodsList.push(...data)
        this.setData({ goodsList: this.data.goodsList })
      } else {
        this.setData({ finished: true })
      }
    })
  },
  /**清除定时器 */
  onHide() {
    this.interval && clearInterval(this.interval)
  },
  onUnload(){
    this.interval && clearInterval(this.interval)
  }
})