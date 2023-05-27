
Component({
  /**
   * 组件的属性列表
   */
  properties: {
    list: {
      type: Array,
      value: ''
    }
  },
  data: {
    treeDataSource:[]
  },
  lifetimes:{
    attached:function(){
      this.getList()
    },
  },
  methods: {
    getList(flag = false) {
      this.initTreeData()
    },
    initTreeData(){
      let tempData = JSON.parse(JSON.stringify(this.data.list))
      let reduceDataFunc = (data,level) =>{
        data.map((m,i)=>{
          m.isExpand = m.isExpand || false
          m.item = m.item || []
          m.level= level
          if(m.item.length>0){
            reduceDataFunc(m.item,level+1)
          }
        })
      }
      reduceDataFunc(tempData,1)
      this.setData({treeDataSource:tempData})
    },
    openExpand(m){
      let _m = m.detail.model
      let index = parseInt(m.detail.num)
      _m.isExpand = !_m.isExpand
      this.data.treeDataSource[index] = _m
      this.setData({treeDataSource:this.data.treeDataSource})
    }
  }
})