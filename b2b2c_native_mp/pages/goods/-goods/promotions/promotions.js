import * as API_Members from '../../../../api/members'
Component({
  properties: {
    promotions: {
      type: Array,
      observer: function (val) {
        if (!val || !val.length || !this.data.selectedSku) return

        /** 全部商品参与促销活动 */
        const _promotions = val.filter(key => !key.sku_id)
        /** 部分商品参与促销活动 */
        const sku_promotions = val.filter(key => key.sku_id === this.data.selectedSku.sku_id)
        this.handPromotion(_promotions, sku_promotions)
      }
    },
    selectedSku:{
      type: Object,
      observer: function (val) {
        if (!val || !this.data.promotions || !this.data.promotions.length) return
        /** 全部商品参与促销活动 */
        const _promotions = this.data.promotions.filter(key => !key.sku_id)
        /** 部分商品参与促销活动 */
        const sku_promotions = this.data.promotions.filter(key => key.sku_id === val.sku_id)
        this.handPromotion(_promotions,sku_promotions)
      }
    }
  },
  data: {
    selectedSkuProm: [],
    showPromotion: false,
    showPopup: false
  },
  methods: {
    handPromotion(_promotions, sku_promotions) {
      this.setData({selectedSkuProm:[], showPromotion:false})
      if (_promotions) {
        if (_promotions.some(key => key.promotion_type === 'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE')) {
          this.setData({
            showPromotion: true,
            selectedSkuProm: _promotions
          })
        } else {
          this.setData({ showPromotion: false })
        }
      }
      if (sku_promotions) {
        sku_promotions.forEach(key => {
          if (key.promotion_type === 'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE') {
            this.data.selectedSkuProm.push(key)
            this.setData({
              showPromotion: true,
              selectedSkuProm: this.data.selectedSkuProm
            })
          }
        })
      } 
    },
    /** 显示弹窗 */
    popup() {
      this.selectComponent('#bottomFrame').showFrame();
    },
    handleToShow() {
      this.setData({ showPopup: true })
    },
    handleToClose() {
      this.setData({ showPopup: false })
    },
    previewImage(e){
      let url = e.currentTarget.dataset.src
      wx.previewImage({urls: [url]})
    }
  }
})