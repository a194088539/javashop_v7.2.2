Component({
  properties:{
    gallery_list:{
      type:Object,
      value:{}
    },
    goods_video:{
      type:String,
      value:''
    }
  },
  methods:{
    //图片预览
    previewImg(e) {
      let index = e.currentTarget.dataset.index
      let imgArr = this.data.gallery_list.map(item => item.original)
      wx.previewImage({
        urls: imgArr,
        current: imgArr[index]
      })
    }
  }
})