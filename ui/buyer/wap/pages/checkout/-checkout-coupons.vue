<template>
  <van-popup id="coupons" v-model="show" position="bottom" style="height:100%">
    <van-nav-bar title="优惠券" fixed @click-right="$emit('close')">
      <i class="iconfont ea-icon-close" slot="right"/>
    </van-nav-bar>
    <div class="coupons-container">
      <div class="shop-item" 
        v-for="(shop, shopIndex) in inventories"
        :key="shopIndex"
        v-if="!couponList || !couponList.length"
      >
        <div class="content-shop-item">
          <div class="no-coupon"
          >您在 {{shop.seller_name}} 店铺还没有领到优惠券，<nuxt-link :to="'/shop/' + shop.seller_id">[去店铺]</nuxt-link>看看？</div>
        </div>
      </div>
      <div
        v-if="couponList || couponList.length"
        v-for="(coupon, couponIndex) in couponList"
        :key="couponIndex"
        class="bonus-item"
        :class="[coupon.selected && 'checked', coupon.enable === 0 && 'unavailable']"
        @click="useCoupon(coupon)"
      >
        <div class="top-bg-bonus-item"></div>
        <div class="content-bonus-item">
          <div class="check-bonus-item">
            <i class="icon-bonus"></i>
          </div>
          <div class="money-bonus-item">
            <div class="use-money">
              <span class="symbol-yuan">￥</span>
              <span class="bonus-money">{{ coupon.amount | unitPrice }}</span>
            </div>
            <div class="limit-money">
              {{ "满"+parseFloat(coupon.use_term.slice(1,6)).toString()+"可用" }}
            </div>
            <div class="limit-money">
             使用有效期截止：{{ coupon.end_time | unixToDate('yyyy-MM-dd') }}
            </div>
            <div class="limit-money">
              <span class="shop-name" v-if="coupon.use_scope">
                仅限
                <em v-if="coupon.use_scope === 'ALL'">全平台</em>
                <em v-if="coupon.use_scope === 'CATEGORY'">品类</em>
                <em v-if="coupon.use_scope === 'SOME_GOODS'">部分商品</em>
                使用
              </span>
              <span class="shop-name" v-else>
                <em :title="coupon.seller_name">仅限 <nuxt-link :to="'/shop/' + coupon.seller_id">{{ coupon.seller_name }}</nuxt-link> 店铺使用</em>
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </van-popup>
</template>

<script>
  import * as API_Trade from '@/api/trade'
  export default {
    name: 'checkout-coupons',
    props: ['show', 'inventories', 'couponList'],
    data() {
      return {
        shopList: '',
        buyWay: this.$route.query.way
      }
    },
    methods: {
      /** 使用优惠券 */
      async useCoupon(coupon) {
        if (coupon.enable === 0) {
          this.$message.error(coupon.error_msg)
          return
        }
        const used = coupon.selected === 1
        if (used) {
          await API_Trade.useCoupon(coupon.seller_id, 0, this.buyWay)
        } else {
          await API_Trade.useCoupon(coupon.seller_id, coupon.member_coupon_id, this.buyWay)
        }
        this.$emit('changed', used ? '' : coupon)
        this.$emit('close')
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .coupons-container {
    padding-top: 46px;
    height: calc(100vh - 46px);
    overflow-x: hidden;
    overflow-y: scroll;
  }
  .shop-item {
    display: flex;
    flex-direction: column;
    margin: 15px 0;
    .title-shop-item {
      padding-left: 10px;
      height: 20px;
      background-color: #fff;
      font-size: 14px;
      line-height: 20px;
      color: #333;
      border-bottom: 1px solid #e2e2e2;
    }
    .content-shop-item {
      min-height: 50px;
      background-color: #fff;
      padding: 0 10px;
    }
  }
  .bonus-item {
    width: 100%;
    margin: 10px 0;
    background-color: #fff;
    box-shadow: 0 0 20px #ccc;
    transition: all .3s ease-out;
    &.checked {
      box-shadow: 0 10px 20px #ccc;
      .icon-bonus {
        border-color: #fff;
        background: $color-main url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAQAAABKfvVzAAAAUElEQVQ4jWNgGAW4wH9d0pRH///zv4E05f+J1jB0lP9n+b/0fzgJpv8PBUr++R9BgmP+N4C1RJLgdqiWKBK8CtVCUsiAtBCvHKqFFOUjAwAATIhwjZSqeJcAAAAASUVORK5CYII=');
        background-size: 18px 18px;
      }
    }
    &.unavailable {
      background-color: #f1f1f1;
      filter: grayscale(100%);
    }
    .top-bg-bonus-item {
      height: 11px;
      background: url(../../assets/images/icon-checkout-coupon-top-bg.png) repeat-x 0 0;
      background-size: 10px 69px;
    }
    .content-bonus-item {
      display: flex;
      justify-content: space-around;
      width: 100%;
      height: 95px;
    }
    .check-bonus-item {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 10%;
      text-align: center;
    }
    .icon-bonus {
      display: inline-block;
      width: 18px;
      height: 18px;
      border-radius: 100%;
      border: 1px solid #a2a2a2;
      line-height: 80px;
      background-color: #fff;
      color: #fff;
      transition: all .3s ease-out;
    }
    .money-bonus-item {
      width: 90%;
      display: flex;
      flex-direction: column;
      justify-content: center;
    }
    .use-money {
      width: 100%;
      color: $color-main;
      font-weight: bold;
      .symbol-yuan {
        font-size: 16px;
      }
      .bonus-money {
        font-size: 28px;
      }
    }
    .limit-money {
      width: 100%;
      font-size: 12px;
      color: #686868;
    }
  }
  .no-coupon {
    text-align: center;
    margin: 15px 0;
    a {
      color: $color-href;
    }
  }
</style>
