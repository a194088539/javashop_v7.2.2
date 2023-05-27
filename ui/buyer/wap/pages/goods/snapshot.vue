<template>
  <div id="snapshot">
    <van-nav-bar
      left-arrow
      title="商品快照"
      @click-left="MixinRouterBack"/>
    <!--商品相册 start-->
    <goods-gallery :data="galleryList"/>
    <!--商品相册 end-->
    <!--商品信息 start-->
    <div class="goods-buy">
      <div class="price_wrap">
        <van-cell-group :border="false" class="price">
          <van-cell class="goods-price">
            <div slot="title" class="price">
              ￥<em>{{ goodsPrice | unitPrice('', 'before') }}</em>.{{ goodsPrice | unitPrice('', 'after') }}
            </div>
          </van-cell>
        </van-cell-group>
      </div>
      <div class="goods-name">
        <h1 v-html="goods.name">{{ goods.gname }}</h1>
      </div>
    </div>
    <!--商品信息 end-->
    <!--店铺卡片 start-->
    <shop-card :shopId="goods.seller_id" ref="shop"/>
    <!--店铺卡片 end-->
    <!--商品简介、参数 start-->
    <van-tabs class="params-container" :line-width="100">
      <van-tab title="商品介绍">
        <!-- <div v-html="goods.intro" class="goods-intro"></div> -->
        <div class="goods-intro">
          <div v-for="(item,index) in goods.mobile_intro" :key="index" :class="[item.type === 'text' ? 'm-text' : 'm-image']">
            <span v-if="item.type === 'text'">{{item.content}}</span>
            <img v-else :src="item.content" alt="">
          </div>
        </div>
      </van-tab>
      <van-tab title="商品参数">
        <goods-params :goods-params="goods.param_list"/>
      </van-tab>
    </van-tabs>
    <!--商品简介、参数 end-->
  </div>
</template>

<script>
  import * as goodsComponents from './index'
  import * as API_Trade from '@/api/trade'
  export default {
    name: 'snapshot',
    data() {
      return {
        goods: '',
        // 商品相册
        galleryList: [],
        goodsPrice: ''
      }
    },
    components: goodsComponents,
    mounted() {
      const { id } = this.$route.query
      API_Trade.getSnapshot(id).then(response => {
        if (response.mobile_intro) {
          response.mobile_intro = JSON.parse(response.mobile_intro)
        }
        this.goods = response
        this.galleryList = response.gallery_list || []
        this.goodsPrice = response.price
      })
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .goods-buy {
    position: relative;
  .goods-name {
    padding:2px 10px 10px;
  &::before {
     content: "";
     height: 0;
     display: block;
     border-top: 1px solid #ddd;
     position: absolute;
     left: 0;
     right: 0;
     top: 0;
   }
  h1 {
    font-size: 16px;
    color: #333;
    line-height: 18px;
    font-weight: 400;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    word-wrap: break-word;
  }
  .collection-goods {
    position: absolute;
    width: 50px;
    height: 36px;
    right: 0;
    top: 10px;
    text-align: center;
    border-left: 1px solid #ddd;
  .van-icon {
    display: block;
    font-size: 20px;
  }
  }
  }
  .goods-price {
    font-size: 12px;
    line-height: 1.8;
    padding: 0 10px;
  em {
    font-size: 18px;
    font-weight: 700;
  }
  }
  }

  .goods-intro {
    width: 100%;
    overflow-x: hidden;
    padding: 10px;
    box-sizing: border-box;
    word-break: break-word;
    /deep/ img {
      max-width: 100%;
      height: auto;
      object-fit: contain;
    }
  }
</style>
