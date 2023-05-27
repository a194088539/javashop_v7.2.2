import { Foundation } from '../../../../ui-utils/index.js'
Component({
  properties: {
    title: String,
    type:String,
    price:String,
    oldprice:String,
    endtime:String
  },
  data: {
    timer:null,
    day:0,
    hours:'00',
    minutes:'00',
    seconds:'00',
  },
  lifetimes: {
    ready() {
      if (this.data.type === 'groupbuy') {
        this.contDown(this.data.endtime - parseInt(new Date() / 1000))
      } else {
        this.contDown(this.data.endtime)
      }
    }
  },
  methods: {
    contDown(times){
      let end_time = times
      this.setData({
        timer:setInterval(()=>{
          if(end_time<=0){
            clearInterval(this.data.timer)
            this.triggerEvent('count-end')
          }else{
            const time = Foundation.countTimeDown(end_time)
            this.setData({
              day : parseInt(time.day),
              hours : time.hours,
              minutes : time.minutes,
              seconds : time.seconds
            })
            end_time --
          }
        },1000)
      })
    }
  },
  detached:function(){
    clearInterval(this.data.timer)
  }
})