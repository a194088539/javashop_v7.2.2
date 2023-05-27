<template>
  <div v-if="promotion && showPromotion" id="goods-groupbuy" class="groupbuy-container">
    <!--团购活动-->
    <template v-if="promotion.promotion_type === 'GROUPBUY'">
      <goods-prom-bar
        title="团购活动"
        :end-time="promotion.end_time - parseInt(new Date() / 1000)"
        @count-end="handleCountEnd"
      >
        <i class="iconfont ea-icon-prom-tag" slot="icon"></i>
      </goods-prom-bar>
      <div class="pro-list">
        <div class="pro-title">团购价</div>
        <div class="pro-content price">
          <span>￥</span>
          <strong>{{ promotion.groupbuy_goods_vo.price | unitPrice }}</strong>
        </div>
      </div>
    </template>
    <!--限时抢购-->
    <template v-else>
      <goods-prom-bar
        title="限时抢购"
        :end-time="promotion.seckill_goods_vo.distance_end_time"
        @count-end="handleCountEnd"
      >
        <i class="iconfont ea-icon-time seckill" slot="icon"></i>
      </goods-prom-bar>
      <div class="pro-list">
        <div class="pro-title" style="width: 65px">限时抢购价</div>
        <div class="pro-content price">
          <strong>{{ promotion.seckill_goods_vo.seckill_price | unitPrice('￥') }}</strong>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
  /**
   * 商品页团购模块
   */
  export default {
    name: 'goods-groupbuy-seckill',
    props: ['promotions', 'selectedSku'],
    data() {
      return {
        promotion: '',
        showPromotion: true
      }
    },
    watch: {
      selectedSku: function (val) {
        if (!this.promotions || !this.promotions.length) return
        const prom = this.promotions.filter(item => item.groupbuy_goods_vo || item.seckill_goods_vo)
        const _prom = prom.filter(key => key.sku_id === val.sku_id)
        if (_prom && _prom[0]) {
          this.showPromotion = true
          if (_prom[0].promotion_type === 'GROUPBUY') {
            this.promotion = _prom[0]
          } else {
            if (_prom[0].seckill_goods_vo.distance_start_time < 0) {
              this.showPromotion = false
            } else {
              this.showPromotion = true
            }
            this.promotion = _prom[0]
          }
        }
        // 如果都没有，返回false
        if (!_prom || !_prom[0]) {
          this.showPromotion = false
          return
        }
      }
    },
    methods: {
      handleCountEnd() {
        this.showPromotion = false;
        this.$alert('活动已结束，商品已恢复原价。')
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .groupbuy-container {
    position: relative;
	  margin-bottom: 40px;
    .iconfont {
      display: inline-block;
      width: 20px;
      height: 20px;
      margin-right: 5px;
      vertical-align: -1px;
      color: #fff;
      &.seckill {
        font-size: 20px;
      }
    }
    .pro-list {
      position: absolute;
      z-index: 3;
      top: 32px;
      left: 0;
      right: 0;
      background-color: #f3f3f3;
    }
  }
</style>
