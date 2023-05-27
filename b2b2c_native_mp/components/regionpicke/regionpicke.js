import * as API_Address from '../../api/address'
Component({
  properties:{
    obj: {
      type: Object,
      value: {}
    }
  },
  data:{
    id: 0,
    areas: [],//当前级别显示的地区
    selectedAreas: [],//存储当前选中项所属的地区列表
    choosedAreas: [],//选中的地区
    finished: false,//是否选择完毕
  },
  lifetimes:{
    attached(){
      this.getAreasItems()
    }
  },
  observers:{
    finished() {
      if (this.data.finished) {
        this.triggerEvent('closeRegionpicke')
      }
    },
    choosedAreas(newVal) {
      this.triggerEvent('areaschanged', newVal)
    }
  },
  methods:{
    // 已选择的地区变化时触发
    onchangeChoosedItem(e) {
      let item = e.currentTarget.dataset.item
      if (item.region_grade) {
        this.setData({
          areas: this.data.selectedAreas[item.region_grade - 1]
        })
      }
    },
    // 选择列表地区项变化时   
    onchangeItem(e) {
      let item = e.currentTarget.dataset.item
      if (this.data.choosedAreas[item.region_grade - 1] && this.data.areas[0].region_grade === item.region_grade && item.id === this.data.choosedAreas[item.region_grade - 1].id) {     
        return
      } else {
        this.data.choosedAreas.length = item.region_grade - 1
        if (this.data.choosedAreas[item.region_grade - 1]) {
          this.data.choosedAreas[item.region_grade - 1] = item
        } else {
          this.data.choosedAreas.push(item)
        }

        this.data.areas.forEach(key => {
          if (item.id === key.id) { 
            key.selected = true
          }else{
            key.selected = false
          }
        })
        item.selected = true
        this.setData({
          finished: item.region_grade === 4,
          id: item.id,
          areas:this.data.areas,
          choosedAreas: this.data.choosedAreas,
        })
        
        if (item.region_grade === 4) {
          return
        }
        this.getAreasItems()  
      }
    },
    //获取地区列表
    getAreasItems() {
      let that = this
      API_Address.getAreas(this.data.id).then(response => {
        if (response && Array.isArray(response) && response.length) {
          response.forEach(key => {
            key.selected = false
          })
          that.setData({areas: response})
          if (that.data.selectedAreas[response[0].region_grade - 1]) {
            
            if (response[0].region_grade === that.data.selectedAreas[response[0].region_grade - 1][0].region_grade) {
              that.data.selectedAreas[response[0].region_grade - 1] = response
            }
          } else {
            that.data.selectedAreas.push(response)
            that.setData({selectedAreas:this.data.selectedAreas})
          }
          // if (response[0].region_grade === 4) {
          //   that.setData({finished: true})
          // }
        } else {
          that.setData({finished: true})
        }
      })
    }
  }
})
