<template>
  <van-tabbar v-if="!isMiniPrograme" v-model="activeIndex" style="box-shadow: 0 0 10px 0 hsla(0,6%,58%,.6);">

    <van-tabbar-item replace>
      <nuxt-link to="/"><van-icon name="wap-home" size="16px"/><span class="tabbar-text">首页</span></nuxt-link>
    </van-tabbar-item>
    <van-tabbar-item replace>
      <nuxt-link to="/category"><van-icon name="wap-nav" size="18px"/><span class="tabbar-text">分类</span></nuxt-link>
    </van-tabbar-item>
    <van-tabbar-item replace :info="cartBadge ? (cartBadge > 99 ? '99+' : cartBadge) : ''">
      <nuxt-link to="/cart"><van-icon name="cart" size="18px"/><span class="tabbar-text">购物车</span></nuxt-link>
    </van-tabbar-item>
    <van-tabbar-item replace>
      <nuxt-link to="/member"><van-icon name="contact" size="18px"/><span class="tabbar-text">我的</span></nuxt-link>
    </van-tabbar-item>
  </van-tabbar>
</template>

<script>
  import Storage from '@/utils/storage'
  export default {
    name: 'TabBar',
    props: ['active', 'is-mini-programe'],
    data() {
      return {
        activeIndex: this.active || 0
      }
    },
    computed: {
      /** 购物车徽章 */
      cartBadge() {
        return this.$store.getters['cart/allCount']
      }
    },
    mounted() {
      if (Storage.getItem('refresh_token')) {
        const shopList  = this.$store.getters['cart/shopList']
        shopList && !shopList.length && this.$store.dispatch('cart/getCartDataAction')
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  .tabbar-text {
    display: block;
    padding-top: 5px;
  }
  /deep/ {
    .van-tabbar-item__text {
      text-align: center;
    }
    .van-tabbar-item--active {
      color: $color-main;
      a {
        color: $color-main;
      }
    }
  }
</style>
