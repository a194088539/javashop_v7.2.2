import * as API_Home from '../../api/home'
import { Foundation } from '../../ui-utils/index'
import regeneratorRuntime from '../../lib/wxPromise.min.js'

Page({
  data: {
    site_name:'',
    opacity:0,
    banner:[],
    menus:[],
    floorList:[],
    showGoTop:false,
    current:0,
    imgheights:[]
  },
  async onShow(){
    const values = await Promise.all([
      API_Home.getFloorData('WAP'),
      API_Home.getSiteMenu('MOBILE'),
      API_Home.getFocusPictures('WAP')
    ])
    const floor = values[0]
    let page_data = floor.page_data ? JSON.parse(floor.page_data) : []
    page_data.forEach(key => {
      key.blockList.forEach(item => {
        if (item && item.block_value && item.block_value.goods_price) {
          item.block_value.goods_price = Foundation.formatPrice(item.block_value.goods_price)
        }
      })
    })
    this.setData({
      site_name: getApp().globalData.projectName,
      floorList: page_data,
      menus: values[1],
      banner: values[2]
    })
  },
  handleChange(e) {
    this.setData({ current: e.detail.current })
  },
  //获取轮播图高度
  handleImageLoad(e) {
    const ratio = e.detail.width / e.detail.height 
    let imgheight = 750 / ratio
    this.data.imgheights[e.target.dataset.id] = imgheight;
    this.setData({ imgheights: this.data.imgheights })
  },
  //楼层事件
  blockHref(e){
    const block = e.currentTarget.dataset.block
    if(block.block_type === 'GOODS'){
      if(block.block_value){
        wx.navigateTo({ url: '/pages/goods/goods?goods_id=' + block.block_value.goods_id,})
      }
      return
    }
    if(!block || !block.block_opt) return
    const { opt_type,opt_value } = block.block_opt
    switch(opt_type){
      case 'URL': wx.navigateTo({ url: `/pages${opt_value}${opt_value}`})
        break
      case 'GOODS': wx.navigateTo({ url: `/pages/goods/goods?goods_id=${opt_value}` })
        break
      case 'KEYWORD': wx.navigateTo({ url: `/pages/category/category?keyword=${opt_value}` })
        break
      case 'SHOP': wx.navigateTo({ url: `/pages/shop/shop_id/shop_id?id=${opt_value}` })
        break
      case 'CATEGORY': wx.navigateTo({ url: `/pages/category/category?category=${opt_value}` })
        break
      default:wx.reLaunch({url: '/pages/home/home'})
    }
  },
  //滚动监听
  onPageScroll(e) {
    const sy = e.scrollTop
    if(sy>100){
      this.setData({ opacity: 0.8, showGoTop:true})
    }else{
      this.setData({ opacity: sy / 100, showGoTop:false })
    }
  },
  goTop(){wx.pageScrollTo({scrollTop: 0})}
})
