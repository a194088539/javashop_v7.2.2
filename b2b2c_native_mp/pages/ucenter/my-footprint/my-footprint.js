import * as API_Members from '../../../api/members.js'
import { Foundation } from '../../../ui-utils/index.js'
let util = require('../../../utils/util.js')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    footPrintList:[], // 足迹列表
    finished:false, // 是否加载完毕
    params:{
      page_no:1,
      page_size:10
    },
    isEdit:false, // 是否是编辑状态
    checkedAll: false, // 是否全选
    scrollHeight: '',
    scrollTop: 0,//滚动高度
    showGoTop: false,//显示返回顶部按钮
    pageCount:0,
    delBtnWidth:120
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.getFootprint()
    this.setData({ scrollHeight: wx.getSystemInfoSync().windowHeight - 70,  delBtnWidth: util.getEleWidth(this.data.delBtnWidth)})
  },
  getFootprint(){
    API_Members.queryHistoryList(this.data.params).then(response=>{
      let _pageCount = Math.ceil(response.data_total / this.data.params.page_size)
      this.setData({ pageCount: _pageCount })
      const {data} = response
      if(data && data.length){
        data.forEach(key=>{
          key.time = Foundation.unixToDate(key.time, 'yyyy-MM-dd')
          key.history.forEach(item=>{
            item.txtStyle = 'left:0px'
            item.goods_price = Foundation.formatPrice(item.goods_price)
            if(!item.checked){
              item.checked = false
            }
          })
        })
        this.data.footPrintList.push(...data)
        this.setData({footPrintList:this.data.footPrintList})
      }else{
        this.setData({finished:true})
      }
    })
  },
  //根据选择的id删除
  deleteFootPrintsId: util.throttle(function(e) {
    const goods = e.currentTarget.dataset.goods
    API_Members.deleteHistoryListId(goods.id).then(() => {
      this.setData({
        'params.page_no':1,
        footPrintList:[]
      })
      this.getFootprint()
    })
  }),
  //根据所以选择的id删除
  deleteAll: util.throttle(function(){
    this.data.footPrintList.forEach(key =>{
      key.history.forEach(item => {
        if(item.checked){
          API_Members.deleteHistoryListId(item.id).then(()=>{
            this.setData({
              'params.page_no': 1,
              footPrintList: []
            })
            this.getFootprint()
          })
        }
      })
    })
    this.finishedFootprints()
  }),
  //单选
  handleChangeCheck(e){
    let goods = e.currentTarget.dataset.goods
    this.data.footPrintList.forEach(key=>{
      key.history.forEach(item=>{
        if(goods.id === item.id){
          item.checked = !item.checked
        }
      })
    })
    const isCheckedAll = this.data.footPrintList.every(key=>{
      return key.history.every(item => item.checked)
    })
    this.setData({ checkedAll: isCheckedAll, footPrintList: this.data.footPrintList})
  },
  //全选
  handleChangeCheckAll(){
    this.setData({checkedAll:!this.data.checkedAll})
    this.data.footPrintList.forEach(key=>{
      key.history.forEach(item=>{
        item.checked = this.data.checkedAll
      })
    })
    this.setData({
      footPrintList: this.data.footPrintList
    })
  },
  // 清空足迹
  clearFootprints(){
    API_Members.clearHistoryList().then(()=>{
      this.setData({
        footPrintList:[],
        finished:true,
        'params.page_no':1,
        'params.page_size':10
      })
    })
  },
  //编辑浏览足迹
  editFootprints(){
    this.data.footPrintList.forEach(key => {
      key.history.forEach(item => {
        item.txtStyle = ''
      })
    })
    this.setData({ footPrintList: this.data.footPrintList, isEdit: true })  
  },
  // 完成编辑
  finishedFootprints(){
    this.data.footPrintList.forEach(key=>{
      key.history.forEach(item=>{
        item.checked = false
        item.txtStyle = ''
      })
    })
    this.setData({ footPrintList: this.data.footPrintList, isEdit: false, checkedAll: false })
  },
  loadMore() {
    if (!this.data.finshed) {
      this.setData({ "params.page_no": this.data.params.page_no += 1 })
      if (this.data.pageCount >= this.data.params.page_no) {
        this.getFootprint()
      }
    }
  },
  scroll(e) {
    if (e.detail.scrollTop > 200) {
      this.setData({ showGoTop: true })
    } else {
      this.setData({ showGoTop: false })
    }
  },
  //返回顶部
  goTop() { this.setData({ scrollTop: 0 }) },
  touchS(e) {if (e.touches.length == 1) {this.setData({startX: e.touches[0].clientX})}},
  touchM(e) {
    if (e.touches.length == 1) {
      var moveX = e.touches[0].clientX;
      var disX = this.data.startX - moveX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = "";
      if (disX == 0 || disX < 0) {
        txtStyle = "left:0px";
      } else if (disX > 0) {
        txtStyle = "left:-" + disX + "px";
        if (disX >= delBtnWidth) {
          txtStyle = "left:-" + delBtnWidth + "px";
        }
      }
      var goodsIndex = e.currentTarget.dataset.goodsindex
      var goodIndex = e.currentTarget.dataset.goodindex
      var list = this.data.footPrintList;
      list.forEach(key => { key.history.forEach(item => { item.txtStyle = 'left:0px' }) })
      list[goodsIndex].history[goodIndex].txtStyle = txtStyle;
      this.setData({ footPrintList: list })
    }
  },
  touchE: function (e) {
    if (e.changedTouches.length == 1) {
      var endX = e.changedTouches[0].clientX;
      var disX = this.data.startX - endX;
      var delBtnWidth = this.data.delBtnWidth;
      var txtStyle = disX > delBtnWidth / 2 ? "left:-" + delBtnWidth + "px" : "left:0px";
      var goodsIndex = e.currentTarget.dataset.goodsindex
      var goodIndex = e.currentTarget.dataset.goodindex
      var list = this.data.footPrintList;
      list.forEach(key => { key.history.forEach(item => { item.txtStyle = 'left:0px' }) })
      list[goodsIndex].history[goodIndex].txtStyle = txtStyle;
      this.setData({ footPrintList: list })
    }
  }
})