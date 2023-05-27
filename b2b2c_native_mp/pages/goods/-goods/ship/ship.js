import * as API_Goods from '../../../../api/goods'
Component({
  properties: {
    goodsId: String
  },
  data: {
    region: '',
    in_store: 2,
    showSelector: false
  },
  methods: {
    //打开地址选择器
    openAddress() {
      this.setData({ showSelector: true })
    },
    popup() {
      this.selectComponent('#bottomFrame').showFrame();
    },
    closeRegionpicke(){
      this.selectComponent('#bottomFrame').hideFrame();
    },
    //地址发生改变
    addressSelectorChanged(object) {
      const item = object.detail
      const area_id = item[item.length - 1].id
      const region = item.map(key => { return key.local_name }).join(' ')
      API_Goods.getGoodsShip(this.data.goodsId, area_id).then(response => {
        this.setData({
          showSelector: false,
          in_store: response,
          region: region
        })
        this.triggerEvent('in-stock-change', response)
      })
    }
  }
})