import * as API_Promotions from '../../../../api/promotions'
import { Foundation } from '../../../../ui-utils/index'
const beh = require('../../../../utils/behavior.js')
Component({
  properties: {
    show: Boolean,
    goodsId:String,
    skuId:String,
    // 拼团Id
    pintuanId: String,
  },
  behaviors: [beh],
  data: {
    showPopup:true,
    // 购买数量
    buyNum: 1,
    // skuMap
    skuMap: new Map(),
    // 规格列表
    specList: [],
    // 被选中规格
    selectedSpec: [],
    selectedSpecVals: [],
    // 规格组合列表
    skusCombination: [],
    // 被选中sku
    selectedSku: '',
    // 被选中的规格图片【如果有】
    selectedSpecImg: '',
    // 没有选中sku，初始化为false
    unselectedSku: false
  },
  // 生命周期
  lifetimes: {
    attached() {
      this.setData({ skuMap: new Map() })
    },
    // 在组件在视图层布局完成后执行 类似vue中的mounted
    ready() {
      this.data.goodsId && this.data.pintuanId && this.GET_AssembleSkuList()   
    }
  },
  // 计算属性
  computed: {
    // 计算完成之后的所选规格
    selected_spec_vals() {
      if (this.data.selectedSpecVals && this.data.selectedSpecVals.length) {
        return this.data.selectedSpecVals.join('-')
      }
      return ''
    }
  },
  // 数据监听器
  observers: {
    goodsId () {
      this.data.goodsId && this.data.pintuanId && this.GET_AssembleSkuList()
    },
    pintuanId() {
      this.data.goodsId && this.data.pintuanId && this.GET_AssembleSkuList()
    },
    show(newVal) {
      if (newVal) this.selectComponent('#bottomFrame').showFrame()
    },
    selectedSku(newVal) {
      this.triggerEvent('skuchanged', newVal)
    },
    buyNum(newVal) {
      this.triggerEvent('numchanged', newVal)
    }
  },
  methods: {
    /** 显示弹窗 */
    hideFrame() {
      this.selectComponent('#bottomFrame').hideFrame();
    },
    toAssembleBuyNow() { this.triggerEvent('to-assemble-buy-now') },
    /** 获取规格信息 */
    GET_AssembleSkuList() {
      // 如果是拼团的 获取此商品参与拼团的sku规格列表
      API_Promotions.getAssembleSkuList(this.data.goodsId,this.data.pintuanId).then(responses => {
        const specList = []
        responses.forEach((sku, skuIndex) => {
          const { spec_list } = sku
          const spec_value_ids = []
          let _combination = []
          if (spec_list) {
            spec_list.forEach((spec, specIndex) => {
              const _specIndex = specList.findIndex(_spec => _spec['spec_id'] === spec.spec_id)
              const _spec = {
                spec_id: spec.spec_id,
                spec_name: spec.spec_name,
                spec_type: spec.spec_type
              }
              const _value = {
                spec_value: spec.spec_value,
                spec_value_id: spec.spec_value_id,
                spec_value_index: skuIndex,
                spec_value_img: {
                  original: spec.spec_image,
                  thumbnail: spec.thumbnail
                }
              }
              _combination.push({ ..._value })
              spec_value_ids.push(spec.spec_value_id)
              if (_specIndex === -1) {
                specList.push({ ..._spec, valueList: [{ ..._value }] })
              } else if (specList[_specIndex]['valueList'].findIndex(_value => _value['spec_value_id'] === spec['spec_value_id']) === -1) {
                specList[_specIndex]['valueList'].push({ ..._value })
              }
            })
            this.data.skusCombination.push(_combination)
            this.data.skuMap.set(spec_value_ids.join('-'), sku)
            this.setData({
              skusCombination: this.data.skusCombination,
              skuMap: this.data.skuMap
            })
          } else {
            this.data.skuMap.set('no_spec', sku)
            this.setData({ skuMap: this.data.skuMap })
          }
        })
        this.setData({ specList: specList })
        // 如果有sku信息，初始化已选规格
        if (this.data.skuId) {
          this.initSpec()
        }
        // 如果没有规格，把商品第一个可选sku给已选择sku
        if (!specList.length) {
          this.setData({ selectedSku: this.data.skuMap.get('no_spec') })
          let _selectedSkuprice = Foundation.formatPrice(this.data.selectedSku.price)
          this.setData({ 'selectedSku.price': _selectedSkuprice })
        }
      })
    },
    /** 初始化规格 */
    initSpec() {
      let sku_id = this.data.skuId
      let selectedSpecs = ''
      if (sku_id) {
        sku_id = Number(sku_id)
        this.data.skuMap.forEach((value, key) => {
          if (value.sku_id === sku_id) {
            selectedSpecs = key.split('-')
          }
        })
      }
      const _selectedSpecVals = []
      this.data.specList.forEach((spec, specIndex) => {
        if (Array.isArray(spec.valueList)) {
          spec.valueList.forEach((val, specValIndex) => {
            if (selectedSpecs) {
              const spec_value_id = val.spec_value_id
              if (selectedSpecs.indexOf(String(spec_value_id)) !== -1) {
                val.selected = true
                this.data.selectedSpec[specIndex] = val.spec_value_id
                this.setData({ selectedSpec: this.data.selectedSpec })
                _selectedSpecVals.push(val.spec_value)
              }
            } else if (specValIndex === 0) {
              val.selected = true
              this.data.selectedSpec[specIndex] = val.spec_value_id
              this.setData({ selectedSpec: this.data.selectedSpec })
              _selectedSpecVals.push(val.spec_value)
            }
          })
        }
      })
      this.setData({ selectedSpecVals: _selectedSpecVals,specList:this.data.specList })
      this.handleSelectedSku()
    },
    /** 选择规格 */
    handleClickSpec(e) {
      const { spec, specindex, spec_val } = e.target.dataset
      if (spec.spec_type === 1) {
        this.setData({ selectedSpecImg: JSON.parse(JSON.stringify(spec_val.spec_value_img)) })
      }
      if (spec_val.selected) return
      spec.valueList.map(item => {
        item.selected = item.spec_value_id === spec_val.spec_value_id
        return item
      })

      this.data.specList[specindex] = spec
      this.data.selectedSpec[specindex] = spec_val.spec_value_id
      this.data.selectedSpecVals[specindex] = spec_val.spec_value
      this.setData({
        specList: this.data.specList,
        selectedSpec: this.data.selectedSpec,
        selectedSpecVals: this.data.selectedSpecVals
      })
      this.handleSelectedSku()
    },
    /** 是否可选 */
    canSelected(specIndex, specList, spec_val) {
      if (specIndex) {
        const selectedList = specList[--specIndex].valueList.filter(key => key.selected)
        if (selectedList.length) {
          const spec_id = selectedList[0].spec_value_id
          const _ids = []
          this.data.skusCombination.forEach(key => {
            key.forEach(item => {
              if (item.spec_value_id === spec_id) {
                _ids.push([...key])
              }
            })
          })
          this.setData({ skusCombination: this.data.skusCombination })
          return _ids.findIndex(key => (key.findIndex(_spec => _spec.spec_value_id === spec_val.spec_value_id) !== -1) === true) !== -1
        }
        return true
      }
      return true
    },
    /** 购买数量增加减少 */
    handleBuyNumChanged(e) {
      const symbol = e.target.dataset.symbol
      if (symbol === '+') {
        const { enable_quantity } = this.data.selectedSku
        if (enable_quantity === 0) {
          wx.showToast({ title: '该规格暂时缺货!', icon: 'noen' })
        } else if (this.data.buyNum >= enable_quantity) {
          wx.showToast({ title: '超过最大库存!', icon: 'noen' })
        } else {
          this.setData({ buyNum: this.data.buyNum + 1 })
        }
      } else {
        if (this.data.buyNum < 2) {
          wx.showToast({ title: '不能再少啦!', icon: 'none' })
        } else {
          this.setData({ buyNum: this.data.buyNum - 1 })
        }
      }
    },
    /** 根据已选规格选出对应的sku */
    handleSelectedSku() {
      let sku
      if (this.data.selectedSpec.length) {
        const spec_vals = []
        this.data.selectedSpec.forEach(item => spec_vals.push(item))
        this.setData({ selectedSpec: this.data.selectedSpec })
        sku = this.data.skuMap.get(spec_vals.join('-'))
      } else {
        sku = this.data.skuMap.get('no_spec')
      }
      if (sku) {
        sku.price = Foundation.formatPrice(sku.price)
        this.setData({
          selectedSku: sku,
          unselectedSku: false,
          goodsInfo: { ...this.data.goodsInfo, ...sku },
          buyNum: sku.enable_quantity === 0 ? 0 : 1
        })
      }
    }
  }
})
