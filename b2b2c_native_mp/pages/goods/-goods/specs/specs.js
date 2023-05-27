import * as API_Goods from '../../../../api/goods'
import * as API_Promotions from '../../../../api/promotions'
import { Foundation } from '../../../../ui-utils/index'
const beh = require('../../../../utils/behavior.js')
Component({
  properties: {
    goods: {
      type: Object,
      value: {}
    },
    show: Boolean,
    promotions: {
      type: Object,
      observer: function (newVal) {
        const _promotions = newVal.filter(key => key.sku_id === this.data.selectedSku.sku_id)
        if (_promotions) {
          const _prom = _promotions.filter(key => key.promotion_type === 'SECKILL' || key.promotion_type === 'GROUPBUY' || key.promotion_type === 'EXCHANGE')
          // 如果是限时抢购或者团购促销活动不显示原价格
          if (_prom && _prom[0]) {
            if (_prom[0].promotion_type === 'SECKILL') {
              this.setData({ promotions_price: `￥${Foundation.formatPrice(_prom[0].seckill_goods_vo.seckill_price)}` })
            } else if (_prom[0].promotion_type === 'GROUPBUY') {
              this.setData({ promotions_price: `￥${Foundation.formatPrice(_prom[0].groupbuy_goods_vo.price)}` })
            } else if (_prom[0].promotion_type === 'EXCHANGE') {
              this.setData({ promotions_price: `${_prom[0].exchange.exchange_point}积分+￥${Foundation.formatPrice(_prom[0].exchange.exchange_money)}` })
            }
          } else {
            this.setData({ promotions_price: false })
          }
        }
      }
    },
    // 规格Id
    skuId: String,
    // 拼团Id
    pintuanId: String,
    // 拼团标识
    from_nav: String
  },
  behaviors: [beh],
  data: {
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
    unselectedSku: false,
    promotions_price: false
  },
  // 生命周期
  lifetimes: {
    attached() {
      this.setData({ skuMap: new Map() })
    },
    // 在组件在视图层布局完成后执行 类似vue中的mounted
    ready() {
      if (!this.data.goods.goods_id) return
      this.GET_GoodsSKus()
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
    'goods.goods_id': function () {
      if (this.data.goods.goods_id) this.GET_GoodsSKus()
    },
    show(newVal) {
      if (newVal){
        this.selectComponent('#bottomFrame').showFrame()
      }else{
        this.selectComponent('#bottomFrame').hideFrame();
      }
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
    popup() {
      this.selectComponent('#bottomFrame').showFrame();
    },
    hideFrame(){
      this.selectComponent('#bottomFrame').hideFrame();
    },
    handleAddToCart() { this.triggerEvent('add-acrt') },
    buyNow() { this.triggerEvent('buy-now') },
    /** 获取规格信息 */
    GET_GoodsSKus() {
      // 如果是拼团的 获取此商品参与拼团的sku规格列表
      Promise.all([
        this.data.skuId && this.data.pintuanId && this.data.from_nav === 'assemble' ?
          API_Promotions.getAssembleSkuList(this.data.goods.goods_id, this.data.pintuanId) :
          API_Goods.getGoodsSkus(this.data.goods.goods_id)
      ]).then(responses => {
        const response = responses[0]
        const specList = []
        response.forEach((sku, skuIndex) => {
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
        this.setData({ specList: specList, selectedSku:''})
        // 如果有sku信息，初始化已选规格
        if (this.data.skuId) {
          this.initSpec()
        } else if(this.data.specList) {
          //默认选中第一个sku
          const _selectedSpecVals = [];
          this.data.specList.forEach((spec, specIndex) => {
            if (Array.isArray(spec.valueList)) {
              spec.valueList.forEach((val, specValIndex) => {
                if (specValIndex === 0) {
                  val.selected = true
                  this.data.selectedSpec[specIndex] = val.spec_value_id
                  this.setData({ selectedSpec: this.data.selectedSpec })
                  _selectedSpecVals.push(val.spec_value)
                }
              })
            }
          })
          this.setData({ selectedSpecVals: _selectedSpecVals, specList: this.data.specList })
          this.handleSelectedSku()
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
        // sku_id = Number(sku_id)
        this.data.skuMap.forEach((value, key) => {
          if (String(value.sku_id) === sku_id) {
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
      this.setData({ selectedSpecVals: _selectedSpecVals, specList: this.data.specList})
      this.handleSelectedSku()
    },
    /** 选择规格 */
    handleClickSpec(e) {
      const spec = e.target.dataset.spec
      const specIndex = e.target.dataset.specindex
      const spec_val = e.target.dataset.spec_val
      if (spec.spec_type === 1) {
        this.setData({ selectedSpecImg: JSON.parse(JSON.stringify(spec_val.spec_value_img)) })
      }
      if (spec_val.selected) return
      spec.valueList.map(item => {
        item.selected = item.spec_value_id === spec_val.spec_value_id
        return item
      })

      this.data.specList[specIndex] = spec
      this.data.selectedSpec[specIndex] = spec_val.spec_value_id
      this.data.selectedSpecVals[specIndex] = spec_val.spec_value
      this.setData({
        specList: this.data.specList,
        selectedSpec: this.data.selectedSpec,
        selectedSpecVals: this.data.selectedSpecVals
      })
      this.handleSelectedSku(true)
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
      const symbol = e.target.dataset.symbol || e.detail.value
      if (symbol === '+') {
        const { enable_quantity } = this.data.selectedSku
        if (enable_quantity === 0) {
          wx.showToast({ title: '该规格暂时缺货!', icon: 'none' })
        } else if (this.data.buyNum >= enable_quantity) {
          wx.showToast({ title: '超过最大库存!', icon: 'none' })
        } else {
          this.setData({ buyNum: this.data.buyNum + 1 })
        }
      } else if (symbol === '-'){
        if (this.data.buyNum < 2) {
          wx.showToast({ title: '不能再少啦!', icon: 'none' })
        } else {
          this.setData({ buyNum: this.data.buyNum - 1 })
        }
      }else{
        const _buyNum = parseInt(symbol)
        this.setData({ buyNum: _buyNum })
      }
    },
    handleBuyNumBlur(e) {
      const { enable_quantity } = this.data.selectedSku
      if (isNaN(this.data.buyNum) && typeof (this.data.buyNum) === 'number' || this.data.buyNum < 1) {
        wx.showToast({ title: '输入格式有误，请输入正整数！', icon: 'none' })
        return
      } else if (this.data.buyNum > enable_quantity) {
        wx.showToast({ title: '超过最大库存!', icon: 'none' })
        return
      }
    },
    /** 根据已选规格选出对应的sku */
    handleSelectedSku(falg) {
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
        if(falg) {
          if(!this.data.promotions || !this.data.promotions.length) return
          const _promotions = this.data.promotions.filter(key => key.sku_id === this.data.selectedSku.sku_id)
          if (_promotions) {
            let _prom = _promotions.filter(key => key.promotion_type === 'GROUPBUY' || key.promotion_type === 'SECKILL' || key.promotion_type === 'EXCHANGE')
            if (_prom && _prom[0]) {
              if ( _prom[0].promotion_type === 'GROUPBUY') {
                this.setData({ promotions_price: `￥${Foundation.formatPrice(_prom[0].groupbuy_goods_vo.price)}` })
              } else if ( _prom[0].promotion_type === 'SECKILL') {
                this.setData({ promotions_price: `￥${Foundation.formatPrice(_prom[0].seckill_goods_vo.seckill_price)}` })
              } else if (_prom[0].promotion_type === 'EXCHANGE') {
                this.setData({ promotions_price: `${_prom[0].exchange.exchange_point}积分+￥${Foundation.formatPrice(_prom[0].exchange.exchange_money)}` })
              }
            } else {
              this.setData({ promotions_price: false })
            }
          }
        }
      }
    }
  }
})
