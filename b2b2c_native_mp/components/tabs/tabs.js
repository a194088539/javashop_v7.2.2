Component({
  properties: {
    topNavs: {
      type:Array,
      value: []
    },
    title: {
      type: String,
      value: ''
    }
  },
  data: {
    /**
     * 当前激活的当航索引
     */
    currentActiveNavIndex: 0,
    /**
     * 上一个激活的当航索引
     */
    prevActiveNavIndex: -1,
    /**
     * scroll-view 横向滚动条位置
     */
    scrollLeft: 0
  },
  lifetimes: {
    attached() {
      
    }
  },
  methods: {
    /**
     * 顶部导航改变事件，即被点击了
     * 1、如果2次点击同一个当航，则不做处理
     * 2、需要记录本次点击和上次点击的位置
     */
    topNavChange(e) {
      const nextActiveIndex = e.currentTarget.dataset.currentIndex,
      currentIndex = this.data.currentActiveNavIndex
      if (currentIndex === nextActiveIndex) return
      this.setData({
        currentActiveNavIndex: nextActiveIndex,
        prevActiveNavIndex: currentIndex
      })
      this.scrollTopNav()
      this.triggerEvent('changed', this.data.currentActiveNavIndex)
    },
    /**
     * 滚动顶部的导航栏
     * 1、这个地方是大致估算的
     */
    scrollTopNav() {
      /**
       * 当激活的当航小于4个时，不滚动
       */
      if (this.data.currentActiveNavIndex <= 3 && this.data.scrollLeft >= 0) {
        this.setData({
          scrollLeft: 0
        })
      } else {
        /**
         * 当超过4个时，需要判断是向左还是向右滚动，然后做相应的处理
         */
        const plus = this.data.currentActiveNavIndex > this.data.prevActiveNavIndex ? 120 * (this.data.currentActiveNavIndex - 3) : 120 * (this.data.currentActiveNavIndex - 3)-120 
        this.setData({scrollLeft: plus})
      }
    }
  }
})