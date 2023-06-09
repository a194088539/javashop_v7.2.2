
Component({
  data: {
    flag: false,
    showtitle:true,
    wrapAnimate: 'wrapAnimate',
    bgOpacity: 0,
    frameAnimate: 'frameAnimate',
  },
  properties: {
    frameTitle: {
      type: String,
      value: '标题',
    },
  },
  lifetimes: {
    attached() {
      if (!this.data.frameTitle) {
        this.setData({ showtitle: false })
      }
    }
  },
  methods: {
    showFrame() {
      this.setData({ flag: true, wrapAnimate: 'wrapAnimate', frameAnimate: 'frameAnimate' });
    },
    hideFrame() {
      const that = this;
      that.setData({ wrapAnimate: 'wrapAnimateOut', frameAnimate: 'frameAnimateOut' });
      setTimeout(() => { that.setData({ flag: false })}, 400)
    },
    catchNone() {
      //阻止冒泡
    },
    _showEvent() {
      this.triggerEvent("showEvent");
    },
    _hideEvent() {
      this.triggerEvent("hideEvent");
    }
  }
})

