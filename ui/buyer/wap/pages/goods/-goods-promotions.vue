<template>
  <div v-if="selectedSkuProm && showPromotion" class="promotions-container">
    <van-cell-group class="promotions-cells">
      <van-cell is-link @click="showPopup = true">
        <div slot="title" style="display: flex;">
          <span>促销</span>
          <div class="promotions-cell">
            <template v-for="(prom, index) in selectedSkuProm">
              <template v-if="prom.promotion_type === 'FULL_DISCOUNT'">
                <em v-if="prom.full_discount_vo.is_full_minus" :key="index + '-full'" class="hl_red_bg">满减</em>
                <em v-if="prom.full_discount_vo.is_discount" :key="index + '-discount'" class="hl_red_bg">打折</em>
                <em v-if="prom.full_discount_vo.is_send_gift" :key="index + '-gift'" class="hl_red_bg">赠礼</em>
                <em v-if="prom.full_discount_vo.is_send_bonus" :key="index + '-coupon'" class="hl_red_bg">赠券</em>
                <em v-if="prom.full_discount_vo.is_free_ship" :key="index + '-free_ship'" class="hl_red_bg">免邮</em>
                <em v-if="prom.full_discount_vo.is_send_point" :key="index + '-send_point'" class="hl_red_bg">积分</em>
              </template>
              <em :key="index" v-if="prom.promotion_type === 'MINUS'" class="hl_red_bg">单品立减</em>
              <em :key="index" v-if="prom.promotion_type === 'HALF_PRICE'" class="hl_red_bg">第二件半价</em>
            </template>
          </div>
        </div>
      </van-cell>
    </van-cell-group>
    <van-popup v-model="showPopup" position="bottom" style="height:70%">
      <van-nav-bar title="促销信息" @click-right="showPopup = false">
        <i class="iconfont ea-icon-close" slot="right"/>
      </van-nav-bar>
      <van-cell-group class="proms-list">
        <template v-for="(prom, index) in selectedSkuProm">
          <!--满减-->
          <template v-if="prom.promotion_type === 'FULL_DISCOUNT'">
            <van-cell v-if="prom.full_discount_vo.is_full_minus || prom.full_discount_vo.is_discount" :key="index + '-full'">
              <div v-if="prom.full_discount_vo.is_full_minus === 1" slot="title">
                <em class="hl_red_bg">满减</em>
                <em class="hl_red">满{{ prom.full_discount_vo.full_money }}元，立减现金 <span class="price">{{ prom.full_discount_vo.minus_value }}元</span></em>
              </div>
              <div v-if="prom.full_discount_vo.is_discount === 1" slot="title">
                <em class="hl_red_bg">打折</em>
                <em class="hl_red">满{{ prom.full_discount_vo.full_money }}元，立享<span class="price">{{ prom.full_discount_vo.discount_value }}折</span>优惠</em>
              </div>
            </van-cell>
            <van-cell v-if="prom.full_discount_vo.is_send_gift" :key="index + '-gift'">
              <div slot="title">
                <em class="hl_red_bg">赠礼</em>
                <em class="hl_red">
                  赠送价值<span class="price">{{ prom.full_discount_vo.full_discount_gift_do.gift_price }}元</span>的
                  <a :href="prom.full_discount_vo.full_discount_gift_do.gift_img" target="_blank">
                    <img :src="prom.full_discount_vo.full_discount_gift_do.gift_img" class="gift-image">
                  </a>
                </em>
              </div>
            </van-cell>
            <van-cell v-if="prom.full_discount_vo.is_send_bonus" :key="index + '-coupon'">
              <div slot="title">
                <em class="hl_red_bg">赠券</em>
                <em class="hl_red">
                  赠送<span class="price">{{ prom.full_discount_vo.coupon_do.coupon_price }}元</span>优惠券
                </em>
                <em class="hl_red_bg" v-if="prom.full_discount_vo.coupon_do.received_num >= prom.full_discount_vo.coupon_do.create_num">已赠完</em>
              </div>
            </van-cell>
            <van-cell v-if="prom.full_discount_vo.is_free_ship" :key="index + '-free_ship'">
              <div slot="title">
                <em class="hl_red_bg">免邮</em>
                <em class="hl_red">
                  满<span class="price">{{ prom.full_discount_vo.full_money }}元</span>免邮费
                </em>
              </div>
            </van-cell>
            <van-cell v-if="prom.full_discount_vo.is_send_point" :key="index + '-send_point'">
              <div slot="title">
                <em class="hl_red_bg">积分</em>
                <em class="hl_red">
                  赠送<span class="price">{{ prom.full_discount_vo.point_value }}</span>积分
                </em>
              </div>
            </van-cell>
          </template>
          <!--单品立减-->
          <van-cell :key="index" v-if="prom.promotion_type === 'MINUS'">
            <div slot="title">
              <em class="hl_red_bg">单品立减</em>
              <em class="hl_red">单件立减现金<span class="price">{{ prom.minus_vo.single_reduction_value }}</span>元</em>
            </div>
          </van-cell>
          <!--第二件半价-->
          <van-cell :key="index" v-if="prom.promotion_type === 'HALF_PRICE'">
            <div slot="title">
              <em class="hl_red_bg">第二件半价</em>
              <em class="hl_red">第二件半价优惠</em>
            </div>
          </van-cell>
        </template>
      </van-cell-group>
    </van-popup>
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
        showPromotion: false,
        showPopup: false
      }
    },
    watch: {
      selectedSku: function (val) {
        let i = 0
        function next() {
          if (!this.promotions || !this.promotions.length) {
            if (i++ > 5) {
              return
            }
            setTimeout(_ => next.call(this), 500)
            return
          }
          setTimeout(()=>{
            if(!this.promotions || !this.promotions.length) return
            this.selectedSkuProm = []
            this.showPromotion = false
            /** 全部商品参与促销活动 */
            const _promotions = this.promotions.filter(key => !key.sku_id)
            /** 部分商品参与促销活动 */
            const sku_promotions = this.promotions.filter(key => key.sku_id === val.sku_id)
            if (_promotions && _promotions.length) {
              if (_promotions.some(key => key.promotion_type ===  'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE')) {
                this.showPromotion = true
                this.selectedSkuProm.push(..._promotions)
              } else {
                this.showPromotion = false
              }
            }
            if (sku_promotions && sku_promotions.length) {
              sku_promotions.forEach(key => {
                if (key.promotion_type ===  'FULL_DISCOUNT' || key.promotion_type === 'MINUS' || key.promotion_type === 'HALF_PRICE') {
                  this.showPromotion = true
                  this.selectedSkuProm.push(key)
                }
              })
            }
          },500)
        }
        next.call(this)
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  /deep/ {
    .van-nav-bar { position: relative !important; }
    .promotions-cells {
      border-bottom: 10px solid #e8e8ed;;
    }
    .van-cell__title { flex: 5 }
    .van-cell__right-icon {margin-left: 0}
  }
  .promotions-container {
    position: relative;
  }
  .promotions-cell {
    flex: 1;
    padding-left: 6px;
  }
  .proms-list {
    height: calc(100% - 46px);
    overflow-x: hidden;
    overflow-y: scroll;
  }
  .hl_red_bg {
    position: relative;
    padding: 2px 2px;
    margin-right: 5px;
    height: 15px;
    line-height: 15px;
    font-size: 10px;
    color: $color-main;
    background: #fff;
    display: inline-block;
    &::before {
      content: "";
      display: block;
      border: 1px solid $color-main;
      position: absolute;
      top: 0;
      bottom: 0;
      left: 0;
      right: 0;
      pointer-events: none;
      border-radius: 2px;
    }
  }
  .hl_red_bg:last-child {
    margin-right: 0px;
  }
  .gift-image {
    display: inline-block;
    width: 30px;
    height: 30px;
  }
</style>
