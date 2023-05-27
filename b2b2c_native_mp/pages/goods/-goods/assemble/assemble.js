import { Foundation } from '../../../../ui-utils/index'
Component({
  properties:{
    assemble:{
      type:Object,
      observer: function (n) {
        this.data.assemble.time_left && this.contDown(this.data.assemble.time_left)
      }
    }
  },
  data:{
    timer:null,
    day:0,
    hours:'00',
    minutes:'00',
    seconds:'00'
  },
  methods: {
    contDown(times) {
      let end_time = times
      this.setData({
        timer: setInterval(() => {
          if (end_time <= 0) {
            clearInterval(this.data.timer)
            this.triggerEvent('count-end')
          } else {
            const time = Foundation.countTimeDown(end_time)
            this.setData({
              day: parseInt(time.day),
              hours: time.hours,
              minutes: time.minutes,
              seconds: time.seconds
            })
            end_time--
          }
        }, 1000)
      })
    }
  },
  detached: function () {
    clearInterval(this.data.timer)
  }
})