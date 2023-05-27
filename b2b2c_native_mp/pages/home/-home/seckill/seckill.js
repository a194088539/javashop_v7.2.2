// pages/home/-home/seckill/seckill.js
import * as API_Promotions from '../../../../api/promotions.js'
import { Foundation } from '../../../../ui-utils/index.js'

Component({
  /**
   * 组件的初始数据
   */
  data: {
    time:false,//时间
    goodsList:[],//商品列表
    times:{
      hours:'00',
      minutes:'00',
      seconds:'00'
    },
    timeLine:'',//当前这一场的时间信息
    onlyOne:false,//是否只有一场
  },
  pageLifetimes:{
    show(){
      this.getTimeline()
    },
    hide(){
      this.interval && clearInterval(this.interval)
    },
  },
  methods: {
    //开始倒计时
    startCountDown(){
      this.interval = setInterval(()=>{
        let { time } = this.data
        if(time <= 0){
          clearInterval(this.interval)
          if(this.data.onlyOne){
            wx.showModal({
              title: '提示',
              showCancel: false,
              confirmColor: "#f42424",
              content: '本轮限时抢购已结束！',
              success(res){
                wx.reLaunch({ url: '/pages/home/home' })
              }
            })
          }else{
            wx.showModal({
              title: '提示',
              showCancel: false,
              confirmColor: "#f42424",
              content: '下一轮限时抢购已经开始啦！请确认查看',
              success(res){
                wx.reLaunch({ url: '/pages/home/home' })
              }
            })
          }
          return false
        }
        time -= 1
        this.setData({
          times: Foundation.countTimeDown(time),
          time:time
        })
      },1000)
    },
    //获取时间段
    getTimeline(){
      API_Promotions.getSeckillTimeLine().then(response=>{
        if(!response || !response.length) return
        response = response.sort((x,y)=>(Number(x.time_text) - Number(y.time_text)))
        const onlyOne = response.length === 1
        this.setData({
          onlyOne:onlyOne,
          timeLine:response[0],
          time: response[0].distance_time !== 0 ? response[0].distance_time : onlyOne ? Foundation.theNextDayTime() : response[1].distance_time
        })
        this.startCountDown()
        this.getGoodsList(response[0].time_text)
      })
    },
    //获取时间段商品
    getGoodsList(range_time){
      API_Promotions.getSeckillTimeGoods({ range_time }).then(response=>{
        const { data } = response
        data.forEach(key=>{
          key.seckill_price = Foundation.formatPrice(key.seckill_price)
          key.original_price = Foundation.formatPrice(key.original_price)
        })
        this.setData({goodsList:data})
      })
    }
  }
})
