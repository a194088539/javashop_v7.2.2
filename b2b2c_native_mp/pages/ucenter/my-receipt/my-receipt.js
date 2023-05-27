import * as API_Members from '../../../api/members.js'
import { Foundation } from '../../../ui-utils/index.js'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    receiptList:[],
    params:{
      page_no:1,
      page_size:10
    },
    finished:false,
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop:false,
    pageCount: 0,
    msg: ''
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      scrollHeight: wx.getSystemInfoSync().windowHeight
    })
    this.getReceiptData()
  },
  loadMore(){
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getReceiptData();
      } else {
        this.setData({ msg: '已经到底了~' })
      }
    }
  },
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({showGoTop: true})
    } else {
      this.setData({showGoTop: false})
    }
  },
  //返回顶部
  goTop() {this.setData({scrollTop: 0})},
  getReceiptData(){
    API_Members.queryReceiptInfoList(this.data.params).then(response=>{
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const {data} = response
      if(data && data.length){
        data.forEach(key=>{
          key.add_time = Foundation.unixToDate(key.add_time)
          key.receipt_type = this.receiptType(key.receipt_type)
          key.goods_json = JSON.parse(key.goods_json)
          if (key.goods_json && key.goods_json.length) {
            key.goods_json.forEach(item => {
              if (!item.skuName) item.skuName = this.formatterSkuSpec(item)
            })
          }
        })
        this.data.receiptList.push(...data)
        this.setData({receiptList:this.data.receiptList})
      }else{
        this.setData({finished:true})
      }
    })
  },
  receiptType(type){
    switch (type){
      case 'VATORDINARY' : return '增值税普通发票'
      case 'ELECTRO' : return '电子普通发票'
      case 'VATOSPECIAL' : return '增值税专用发票'
      default : return ''
    }
  },
  /** 规格格式化显示 */
  formatterSkuSpec(sku) {
    if (!sku.spec_list || !sku.spec_list.length) return ''
    return sku.spec_list.map(spec => spec.spec_value).join(' - ')
  }
})