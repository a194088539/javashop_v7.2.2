<template>
  <div v-if="selectedSkuProm && showPromotion" class="promotions-container">
    <div class="pro-list promotions-box" id="promotions-box">
      <div class="pro-title">促销信息</div>
      <div class="pro-content prom">
        <template v-for="(prom, index) in selectedSkuProm">
          <!--满减-->
          <template v-if="prom.promotion_type === 'FULL_DISCOUNT'">
            <!--满减-->
            <div v-if="prom.full_discount_vo.is_full_minus" :key="index + '-full'" class="prom-item">
              <em class="hl_red_bg">满减</em>
              <em class="hl_red">满{{ prom.full_discount_vo.full_money }}元，立减现金 <span class="price">{{ prom.full_discount_vo.minus_value }}元</span></em>
            </div>
            <!--打折-->
            <div v-if="prom.full_discount_vo.is_discount" :key="index + '-full'" class="prom-item">
              <em class="hl_red_bg">打折</em>
              <em class="hl_red">满{{ prom.full_discount_vo.full_money }}元，立享<span class="price">{{ prom.full_discount_vo.discount_value }}折</span>优惠</em>
            </div>
            <!--满赠 赠品-->
            <div v-if="prom.full_discount_vo.is_send_gift" :key="index + '-gift'" class="prom-item">
              <em class="hl_red_bg">赠礼</em>
              <em class="hl_red">
                赠送价值<span class="price">{{ prom.full_discount_vo.full_discount_gift_do.gift_price }}元</span>的
                <a :href="prom.full_discount_vo.full_discount_gift_do.gift_img" target="_blank">
                  <img :src="prom.full_discount_vo.full_discount_gift_do.gift_img" class="gift-image">
                </a>
              </em>
            </div>
            <!--满赠 赠券-->
            <div v-if="prom.full_discount_vo.is_send_bonus" :key="index + '-coupon'" class="prom-item">
              <em class="hl_red_bg">赠券</em>
              <em class="hl_red">
                赠送<span class="price">{{ prom.full_discount_vo.coupon_do.coupon_price }}元</span>优惠券
              </em>
              <em class="hl_red_bg" v-if="prom.full_discount_vo.coupon_do.received_num >= prom.full_discount_vo.coupon_do.create_num">已赠完</em>
            </div>
            <!--满赠 免邮-->
            <div v-if="prom.full_discount_vo.is_free_ship" :key="index + '-free_ship'" class="prom-item">
              <em class="hl_red_bg">免邮</em>
              <em class="hl_red">
                满<span class="price">{{ prom.full_discount_vo.full_money }}元</span>免邮
              </em>
            </div>
            <!--满赠 积分-->
            <div v-if="prom.full_discount_vo.is_send_point" :key="index + '-send_point'" class="prom-item">
              <em class="hl_red_bg">积分</em>
              <em class="hl_red">
                赠送<span class="price">{{ prom.full_discount_vo.point_value }}</span>积分
              </em>
            </div>
          </template>
          <!--单品立减-->
          <div :key="index" v-if="prom.promotion_type === 'MINUS'" class="prom-item">
            <em class="hl_red_bg">单品立减</em>
            <em class="hl_red">单件立减现金<span class="price">{{ prom.minus_vo.single_reduction_value }}</span>元</em>
          </div>
          <!--第二件半价-->
          <div :key="index" v-if="prom.promotion_type === 'HALF_PRICE'" class="prom-item">
            <em class="hl_red_bg">第二件半价</em>
            <em class="hl_red">第二件半价优惠</em>
          </div>
        </template>
      </div>
    </div>
    <div id="promotions-place" class="promotions-place"></div>
  </div>
</template>

<script>
  /**
   * 商品促销模块
   * 包含满减满赠、单品立减、第二件半价
   */
  import * as API_Promotions from '@/api/promotions'
  export default {
    name: 'goods-promotions',
    props: ['promotions', 'selectedSku'],
    data() {
      return {
        selectedSkuProm: [],
        showPromotion: false
      }
    },
    mounted() {
      this.handleCountPlaBoxHeight()
    },
    watch: {
      selectedSku: function (val) {
        if (!this.promotions || !this.promotions.length) return
        /** 全部商品参与促销活动 */
        const _promotions = this.promotions.filter(key => !key.sku_id)
        /** 部分商品参与促销活动 */
        const sku_promotions = this.promotions.filter(key => key.sku_id === val.sku_id)
        let selectedSkuProm  = []
        if (_promotions) {
          if (_promotions.some(key => key.promotion_type ===  'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE')) {
            this.showPromotion = true
            selectedSkuProm = _promotions
          } else {
            this.showPromotion = false
          }
        }
        if (sku_promotions && sku_promotions.length) {
          sku_promotions.forEach(key => {
            if (key.promotion_type ===  'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE') {
              this.showPromotion = true
              selectedSkuProm.push(key)
            }
          })
        }
        this.selectedSkuProm = selectedSkuProm
        this.handleCountPlaBoxHeight()
      }
    },
    methods: {
      /** 重新计算赋值占位box高度 */
      handleCountPlaBoxHeight() {
        this.$nextTick(() => {
          const $proBox = document.getElementById('promotions-box')
          const $plaBox = document.getElementById('promotions-place')
          if (!$proBox || !$plaBox) return
          $plaBox.style.height = $proBox.offsetHeight + 'px'
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .promotions-container {
    position: relative;
  }
  .promotions-box {
    background: url("../../assets/images/background-price.png") 0 -12px repeat-x #efefef;
    padding-bottom: 5px;
    position: absolute;
    z-index: 2;
    top: 0;
    left: 0;
    right: 0;
  }
  .promotions-place {
    position: relative;
    z-index: 1;
  }
  .pro-content {
    em.hl_red_bg {
      padding: 2px 3px;
      color: $color-main;
      border: 1px solid $color-main;
      margin-right: 4px;
    }
    .prom-item:not(:first-child) {
      display: inline-block;
      .hl_red {
        display: none;
      }
    }
  }
  .promotions-box:hover .prom-item {
    display: block;
    .hl_red { display: inline-block }
  }
  .gift-image {
    width: 20px;
    height: 20px;
    vertical-align: middle;
  }
</style>
