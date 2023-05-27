<template>
  <div>
	  <van-nav-bar left-arrow @click-left="MixinRouterBack">
      <van-tabs v-model="tabActive" slot="title" @click="handleClickNavTab">
        <van-tab :disabled="goods_off" title="商品"/>
        <van-tab :disabled="goods_off" title="评价"/>
        <van-tab :disabled="goods_off" title="详情"/>
        <van-tab v-if="show_dis" :disabled="goods_off" title="推荐"/>
      </van-tabs>
      <header-shortcut slot="right"/>
    </van-nav-bar>
    <div v-if="goods.goods_off === 0" class="goods-auth">
      <div style="height: 50px"></div>
      <img src="../../assets/images/background-goods-off.jpg" alt="">
      <van-button type="default" size="small" @click="$router.push('/')">去首页</van-button>
      <van-button type="default" size="small" @click="MixinRouterBack">返回上页</van-button>
    </div>
    <template v-else>
      <div class="goods-container">
        <!--商品相册 start-->
        <goods-gallery :data="galleryList" :goods_video="goods.goods_video"/>
        <!--团购、限时抢购 start-->
        <goods-groupbuy-seckill :promotions="promotions" :selectedSku="selectedSku"/>
        <!--团购、限时抢购 end-->
        <!--积分兑换 start-->
        <goods-exchange :promotions="promotions"/>
        <!--积分兑换 end-->
        <!--拼团 start-->
        <goods-assemble :assemble="assemble" :is_assemble="is_assemble"/>
        <!--拼团 end-->
        <!--商品相册 end-->
        <div class="goods-buy">
          <div class="price_wrap">
            <van-cell-group v-if="originPriceShow" :border="false" class="price">
              <van-cell v-if="selectedSku && selectedSku.price" class="goods-price">
                <div slot="title" class="price">
                  ￥<em>{{ selectedSku.price | unitPrice('', 'before') }}</em>.{{ selectedSku.price | unitPrice('', 'after') }}
                </div>
              </van-cell>
              <van-cell v-else class="goods-price">
                <div slot="title" class="price">
                  ￥<em>{{ goods.price | unitPrice('', 'before') }}</em>.{{ goods.price | unitPrice('', 'after') }}
                </div>
              </van-cell>
            </van-cell-group>
            <div class="collect">
              <a href="javascript:" :class="['collect-goods-btn', collected && 'collected']" @click="handleCollectGoods">{{ collected ? '已收藏' : '收藏商品' }}</a>
            </div>
          </div>
          <div class="goods-name">
            <h1 v-html="goods.goods_name">{{ goods.goods_name }}</h1>
          </div>
        </div>
        <span class="separated"></span>
        <!--店铺优惠券 start-->
        <goods-coupons :goods-id="goods.goods_id" :selectedSku="selectedSku"/>
        <!--店铺优惠券 end-->
        <!--促销活动 start-->
        <goods-promotions :promotions="promotions" :selectedSku="selectedSku"/>
        <!--促销活动 end-->
        <!--商品规格 start-->
        <goods-specs
          v-model="showSpecsPopup"
          :goods="goods"
          :show="showSpecsPopup"
          :promotions="promotions"
          @sku-changed="handleGetAssembleDetail"
          @num-changed="(num) => { buyNum = num }"
          @add-cart="handleAddToCart"
          @buy-now="handleBuyNow"
          @close="showSpecsPopup = false"
        />
        <!--商品规格 end-->
        <!--配送地区 start-->
        <goods-ship :goods-id="goods.goods_id" @in-stock-change="handleInStockChange"/>
        <!--配送地区 end-->
        <span class="separated"></span>
        <!--店铺卡片 start-->
        <shop-card :shop-id="goods.seller_id" ref="shop"/>
        <!--店铺卡片 end-->
        <span class="separated"></span>
        <!--拼团订单 start-->
        <goods-assemble-order
          :goods-id="goods.goods_id"
          :sku-id="$route.query.sku_id"
          :is_assemble="is_assemble"
          @to-assemble-buy-now="toAssembleBuyNow"
        />
        <!--拼团订单 end-->
        <!--评价 start-->
        <goods-comments :goods-id="goods.goods_id" :grade="goods.grade"/>
        <!--评价 end-->
        <span class="separated"></span>
        <!--商品咨询 start-->
        <goods-ask :goods-id="goods.goods_id" :selected-sku="selectedSku"/>
        <!--商品咨询 end-->
        <span class="separated"></span>
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
      <div style="height: 50px"></div>
      <van-goods-action style="z-index: 99">
<!--        <van-goods-action-mini-btn-->
<!--          :icon="collected ? 'like' : 'like-o'"-->
<!--          :text="collected ? '已收藏' : '收藏'"-->
<!--          @click="handleCollectGoods"-->
<!--        />-->
        <van-goods-action-mini-btn icon="chat" text="客服" @click="handleChat"/>
        <van-goods-action-mini-btn icon="cart" :info="cartBadge ? (cartBadge > 99 ? '99+' : cartBadge) : ''" :to="'/cart'" text="购物车"/>
        <van-goods-action-mini-btn icon="shop" text="店铺" :to="'/shop/' + goods.seller_id"/>
        <van-goods-action-big-btn
          v-if="goods.is_auth === 0 || goods.goods_off === 0"
          :text="goods.is_auth === 0 ? '商品审核中' : '商品已下架'"
          class="buy-disabled"
        />
        <template v-if="goods.is_auth !== 0 && goods.goods_off !== 0 && !is_assemble">
          <van-goods-action-big-btn
            text="加入购物车"
            :class="[!in_store && 'buy-disabled']"
            @click="handleAddToCart"
          />
          <van-goods-action-big-btn
            text="立即购买"
            primary
            :class="[!in_store && 'buy-disabled']"
            @click="handleBuyNow"
          />
        </template>
        <template v-if="goods.is_auth !== 0 && goods.goods_off !== 0 && is_assemble">
          <!--单独买 直接执行立即购买-->
          <div class="alone-buy" :class="[!in_store && 'buy-disabled']" @click="handleBuyNow">
            <span>{{ assemble.origin_price | unitPrice('¥') }}</span>
            <span>单独购买</span>
          </div>
          <!--拼团买-->
          <div class="assemble-buy" :class="[!in_store && 'buy-disabled']" @click="handleAssembleBuyNow">
            <span>{{ assemble.sales_price | unitPrice('¥') }}</span>
            <span>{{ assemble.required_num }}人团</span>
          </div>
        </template>
      </van-goods-action>
      <goods-distribution
        v-if="show_dis"
        :show="showDisPopup"
        :goods_name="goods.goods_name"
        :thumbnail="goods.thumbnail"
        @close="showDisPopup = false"/>
    </template>
  </div>
</template>

<script>
  import Vue from 'vue'
  import { mapGetters } from 'vuex'
  import { Tabs, Tab, Swipe, SwipeItem, Cell, CellGroup, GoodsAction, GoodsActionBigBtn, GoodsActionMiniBtn } from 'vant'
  Vue.use(Tabs).use(Tab).use(Swipe).use(SwipeItem).use(Cell).use(CellGroup).use(GoodsAction).use(GoodsActionBigBtn).use(GoodsActionMiniBtn)
  import * as API_Goods from '@/api/goods'
  import * as API_Trade from '@/api/trade'
  import * as API_Common from '@/api/common'
  import * as API_Members from '@/api/members'
  import * as API_Promotions from '@/api/promotions'
  import * as API_distribution from '@/api/distribution'
  import * as goodsComponents from './index'
  import Storage from '@/utils/storage'
  let wx
  if (process.browser) wx = require('weixin-js-sdk')
  export default {
    name: 'goods-detail',
    watchQuery: ['params.id'],
    validate({ params }) {
      return /^\d+$/.test(params.id)
    },
    async asyncData({ params, error }) {
      let goods = {}
      try {
        goods = await API_Goods.getGoods(params.id)
      } catch (e) {
        error({ statusCode: 500, message: '商品已不存在' })
      }
      return {
        // 商品信息
        goods,
        // 商品相册
        galleryList: goods.gallery_list || [],
        // 当前商品是否可以浏览
        canView: goods.is_auth !== 0 && goods.goods_off === 1,
        // 当前商品是否下架
        goods_off: goods.goods_off === 0
      }
    },
    head() {
      const { goods, site } = this
      return {
        title: `${goods.page_title || goods.goods_name || '商品详情'}-${site.title}`,
        meta: [
          { hid: 'keywords', name: 'keywords', content: goods.meta_keywords },
          { hid: 'description', name: 'description', content: `${goods.meta_description}-${goods.title}` }
        ]
      }
    },
    components: goodsComponents,
    data() {
      return {
        // 显示分销分享按钮
        show_dis: process.env.distribution,
        // 商品id
        goods_id: this.$route.params.id,
        // 商品信息
        goods: '',
        // 当前tab
        tabActive: 0,
        // 商品是否已被收藏
        collected: false,
        // 已选sku
        selectedSku: '',
        // 购买数量
        buyNum: 1,
        // 距离顶部距离
        offsetTop: {
          cm: 0,
          pa: 0
        },
        // 锁住滚动出发事件
        lockScroll: false,
        // 促销信息
        promotions: '',
        // 是否显示原价格
        originPriceShow: true,
        // 拼团信息
        assemble: '',
        // 待成团订单信息
        assembleOrderList: '',
        // 展示分销弹框
        showDisPopup: false,
        // 显示规格弹框
        showSpecsPopup: false,
        // 配送地区是否为有货状态
        in_store: true,
        // 是否是拼团 默认不是
        is_assemble: false,
        // 单独购买
        alone_buy_text: `¥158.00\n单独购买`,
        // 活动id
        activity_id: '',
        // 活动类型
        promotion_type: ''
      }
    },
    async mounted() {
      const { goods_id, seller_id, mobile_intro } = this.goods
      // 如果商品可以查看
      if (this.canView) {
        // 记录浏览量统计【用于统计】
        API_Common.recordViews(window.location.href)
        // 如果页面是被分享的 则调用分销参数回传API
        this.$route.query.su && API_distribution.accessShortLink({su: this.$route.query.su })
        // 如果存在sku_id 并且带有拼团标识
        if (this.$route.query.sku_id && this.$route.query.from_nav === 'assemble') {
          this.is_assemble = true
          this.assemble = await API_Promotions.getAssembleDetail(this.$route.query.sku_id)
        } else {
          // 获取促销信息
          this.promotions = await API_Promotions.getGoodsPromotions(goods_id)
          const _promotions = this.promotions.filter(key => key.sku_id === this.selectedSku.sku_id)
          // 如果是限时抢购或者团购促销活动不显示原价格
          if (_promotions) {
            if (this.promotions.some(key => key.sku_id === this.selectedSku.sku_id) && _promotions.some(key => key.promotion_type ===  'GROUPBUY' || key.promotion_type === 'SECKILL')) {
              this.originPriceShow = false
            } else {
              this.originPriceShow = true
            }
          }
        }
        // 移动端详情
        if (mobile_intro) {
          this.goods.mobile_intro = JSON.parse(this.goods.mobile_intro)
        }
        // 滚动监听
        window.addEventListener('scroll', this.handleCountOffset)
      }
      // 如果用户已登录，加载收藏状态，加载购物车
      if (Storage.getItem('refresh_token')) {
        // 获取收藏状态
        API_Members.getGoodsIsCollect(goods_id).then(response => {
          this.collected = response.message
        })
        // 获取购物车数据
        this.$store.dispatch('cart/getCartDataAction')
      }
    },
    watch: {
      selectedSku: function (val) {
        setTimeout(() => {
          if(!this.promotions || !this.promotions.length) return
          const _promotions = this.promotions.filter(key => key.sku_id === val.sku_id)
          // 如果是限时抢购或者团购促销活动不显示原价格
          if (_promotions) {
            if (_promotions.some(key => key.promotion_type ===  'GROUPBUY' || key.promotion_type === 'SECKILL')) {
              this.originPriceShow = false
            } else {
              this.originPriceShow = true
            }
          }
        },500)
      }
    },
    computed: {
      /** 购物车徽章 */
      cartBadge() {
        return this.$store.getters['cart/allCount']
      },
      /** 是否隐藏价格 */
      hiddenPrice() {
        const { promotions = prom } = this
        if (!promotions || !promotions.length) return false
        // 如果有团购，隐藏价格
        if (promotions.filter(item => item.groupbuy_goods_vo)[0]) return true
        // 如果有限时抢购，隐藏价格
        if (promotions.filter(item => item.seckill_goods_vo)[0]) return true
        // 如果有积分兑换，隐藏价格
        if (promotions.filter(item => item.exchange)[0]) return true
        return false
      }
    },
    methods: {
      /** 收藏商品 */
      handleCollectGoods() {
        if (!Storage.getItem('refresh_token')) {
          this.$message.error('您还未登录！')
          return false
        }
        const { goods_id } = this.goods
        if (this.collected) {
          API_Members.deleteGoodsCollection(goods_id).then(() => {
            this.$message.success('取消收藏成功！')
            this.collected = false
          })
        } else {
          API_Members.collectionGoods(goods_id).then(() => {
            this.$message.success('收藏成功！')
            this.collected = true
          })
        }
      },
      isPC() {
        let userAgentInfo = navigator.userAgent;
        let Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPod", "iPad"];
        let flag = true;
        for (let i = 0; i < Agents.length; i++) {
          if (userAgentInfo.indexOf(Agents[i]) > 0) {
            flag = false;
            break;
          }
        }
        return flag;
      },
      isWeiXin() {
        let ua = window.navigator.userAgent.toLowerCase();
        if(ua.match(/MicroMessenger/i)){
          return true;
        } else {
          return false;
        }
      },
      handleChat() {
        if (this.isPC()) {
          window.open("http://wpa.qq.com/msgrd?v=3&uin=" + this.$refs.shop.shop.shop_qq + "&site=qq&menu=yes")
        } else {
          if (this.isWeiXin()) {
            window.open("http://wpa.qq.com/msgrd?v=3&uin=" + this.$refs.shop.shop.shop_qq + "&site=qq&menu=yes")
          } else {
            window.open("mqqwpa://im/chat?chat_type=wpa&uin=" + this.$refs.shop.shop.shop_qq + "&version=1&src_type=web")
          }
        }
      },
      /** 立即购买 */
      handleBuyNow() {
        if (!this.in_store || !this.isLogin()) return
        this.getActivityId()
        const { buyNum, activity_id, promotion_type } = this;
        const { sku_id } = this.selectedSku
        API_Trade.buyNow(sku_id, buyNum, activity_id, promotion_type).then(response => {
          this.$store.dispatch('cart/getCartDataAction')
          this.$router.push(`/checkout?way=BUY_NOW`)
        })
      },
      /** 拼团买 创建拼团 */
      handleAssembleBuyNow() {
        if (!this.in_store || !this.isLogin()) return
        const { buyNum } = this
        const { sku_id } = this.selectedSku
        API_Trade.addToAssembleCart(sku_id, buyNum).then(response => {
          this.$store.dispatch('cart/getAssembleCartAction')
          this.$router.push(`/checkout?from_nav=assemble`)
        })
      },

	    /** sku变化 */
      handleGetAssembleDetail(sku) {
        if (this.$route.query.sku_id && this.$route.query.from_nav === 'assemble') {
          this.is_assemble = true
          /** 查询获取某个拼团的sku详情 */
          const { sku_id } = sku
          API_Promotions.getAssembleDetail(sku_id).then(res => { this.assemble = res })
        }
        this.selectedSku = sku
      },

      /** 参与拼团 */
      toAssembleBuyNow(order) {
        if (!this.in_store || !this.isLogin()) return
        const { buyNum } = this
        const { sku_id } = this.selectedSku
        API_Trade.addToAssembleCart(sku_id, buyNum).then(response => {
          this.$store.dispatch('cart/getAssembleCartAction')
          this.$router.push(`/checkout?order_id=${order.order_id}&from_nav=assemble`)
        })
      },

      /** 加入购物车 */
      handleAddToCart() {
        if (!this.in_store || !this.isLogin()) return
        this.getActivityId()
        const { buyNum, activity_id, promotion_type } = this;
        const { sku_id } = this.selectedSku
        API_Trade.addToCart(sku_id, buyNum, activity_id, promotion_type).then(response => {
          this.$store.dispatch('cart/getCartDataAction')
          this.$confirm('加入购物车成功！要去看看吗？', () => {
            this.$router.push({ path: '/cart' })
          })
        })
      },
      /** 是否已登录 */
      isLogin() {
        if (!this.selectedSku) {
          this.$message.error('请选择商品规格！')
          this.showSpecsPopup = true
          this.unselectedSku = true
          return false
        }
        if(Storage.getItem('refresh_token')) {
          return true
        } else {
          this.$confirm('您还未登录，要现在去登录吗？', () => {
            let _forward = `${this.$route.path}?sku_id=${this.selectedSku.sku_id}`
            if (this.$route.query.from_nav === 'assemble') {
              _forward = `${this.$route.path}?sku_id=${this.selectedSku.sku_id}&from_nav=assemble`
            }
            this.$router.push({ path: '/login', query: { forward:_forward } })
          })
          return false
        }
      },
      /** 检查是否有积分兑换、团购、限时抢购活动 */
      getActivityId() {
        const { promotions } = this;
        if (!promotions || !promotions.length) return '';
        let pro;
        for (let i = 0; i < promotions.length; i++) {
          let item = promotions[i];
          if (
            item.seckill_goods_vo ||
            item.groupbuy_goods_vo ||
            item.exchange ||
            item.minus_vo ||
            item.half_price_vo
          ) {
            pro = item;
            break
          }
        }
        if (!pro) return '';
        this.activity_id = pro.activity_id
        this.promotion_type = pro.promotion_type
      },
      /** 导航栏tab发生改变 */
      handleClickNavTab(index) {
        const { cm, pa } = this.offsetTop
        this.lockScroll = true
        if (index === 0) this.MixinScrollToTop(0)
        if (index === 1) this.MixinScrollToTop(cm)
        if (index === 2) this.MixinScrollToTop(pa)
        if (index === 3) this.showDisPopup = true
      },
      /** 计算滚动offset */
      handleCountOffset() {
        if (this.lockScroll) {
          setTimeout(() => {
            this.lockScroll = false
          }, 300)
          return
        }
        const sy = window.scrollY
        const { cm, pa } = this.offsetTop
        if (sy >= cm && sy < pa) {
          this.tabActive = 1
        } else if (sy >= pa) {
          this.tabActive = 2
        } else {
          this.tabActive = 0
        }
      },
      /** 配送地区是否有货发生改变 */
      handleInStockChange(in_store) {
        this.in_store = in_store
      }
    },
    updated() {
      this.$nextTick(() => {
        this.offsetTop = {
          cm: $('#goods-comments').offset().top - 54,
          pa: $('.params-container').offset().top - 54
        }
      })
    },
    destroyed() {
      window.removeEventListener('scroll', this.handleCountOffset)
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .price_wrap{
    overflow: hidden;
    padding:10px;
  }
  .price{
    float: left;
  }
    .collect-goods-btn {
      display: inline-block;
      padding-top: 14px;
      float: right;
      color: #999;
      font-size:12px;
      background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABrElEQVQ4T62TP0hcQRCHf7P7zk64BITEPliJtQQMCJaJpLjCRu723u6BMYgpgmArmkaCkCI7U5xw5StsgoJaXGMn6ZLiSJr8wSbkVeHgMLfhyT05L4cIuuWPmW/Yb2cJtzx0y35cARhjJrTWNQAPlVKJ934vG2CtLQF4DiAAOBCRRj74EuCcyxrjEAIT0ZcQwisAPwFERPSIiHa73W4EYJ2I9pl5KYNcAJxzkwAkTdPHSZL8zenW2n0iOmfmZ3lmjBnXWn9USs167z/ngOUQwqiIbPU7qdVqU51O53e9Xv/en8dxvEFEP0TkfQ54EUK4JyIbN5Fqrd0G8E1Edi4A1Wr1vtb6JE3TqSRJOtdBSqXSSLFY/EREM8x81i9xNbPPzK+vAzjn3gL4yszvLiX2Gsg5dxRC2BaRg2EQa60BsCAic/89YxYYY0a11odKqZfe+9MBcYtKqZV2uz3TaDT+DAVkYblcflAoFD4AWBOR4yyL43heKbUJ4Akz/+oHD13lSqUyFkVRBjnpFU8T0dPB5kEHV67ds/2GiLqtVmut2WyeD/Nyt5/pJks0WPMPjeaXEW5C95kAAAAASUVORK5CYII=") no-repeat 16px 0px;
      &.collected {
        background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABd0lEQVQ4T6WSPUvDcBDGn2uVxjRpQVqngqXJomBnaUEcHBXBTfFbuCouOjn7CURw081BN18KgksHoZg/aSeHikvTSrXJSVMT+pJKgzfey4977h7CP4P651nXYy1gkZjlGSEeCWCvzpnMbEuS5mXghQyj7eV9gKVpeyA6BhBzi8xlcpz9KebX72j0FMAqiCJg/gRwqAhx0m1zAVYut4NI5DxATRvMNojk4Rozb6tCXLiAhq6XCFgOcw5mLqlCFDxAnYBUKADwrhpGuidB025AtBYKwHynCrHSA+j6LoCzMABiPogLceR/oaFpV0S0OQmEmSuKEHkCvnwAp9NKM5F4AFH+LwgDH9O2XZBMs+K/sd8sliTdE7AQBGHAItsuKqZZHjGSl2ho2hwBtyBaGnApUI/a9rpsmk/9+QEr+5ukUqqVTF4TUfHXlc/U6WzEa7W34c0CAa6TdT3WZL4E4MSF2OoeLEjWWIALyWYlVKvOuOGRI07ywuGeH8P8iBFSzfHsAAAAAElFTkSuQmCC") no-repeat 12px 0px;
      }
    }
  .separated {
    display: block;
    width: 100%;
    height: 10px;
    background-color: #e8e8ed;
  }
  /deep/ {
    .van-cell {
      padding: 10px 6px;
    }
    .van-nav-bar {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      z-index: 99 !important;
    }
    .van-nav-bar__left .van-nav-bar__arrow { color: #666 }
    .van-tabs__wrap::after {
      border-width: 0;
    }
  }
  .goods-container {
    margin-top: 46px;
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
    .params-container {
      z-index: 1;
      /deep/ .van-tabs__wrap {
        border-bottom: 1px solid #f5f5f5;
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
    }
  }
  /deep/ .van-goods-action {
    a:hover {
      color: #666
    }
    .van-icon-like {
      color: $color-main
    }
  }
  /*禁止购买*/
  /deep/ .buy-disabled {
    background-color: #e5e5e5 !important;
    pointer-events:none;
  }
  /*单独购买*/
  .alone-buy, .assemble-buy {
    flex: 1;
    padding: 0;
    background-color: #FD7E88;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    span {
      color: #fff;
      font-size: 14px;
    }
  }
  .assemble-buy {
    flex: 1;
    padding: 0;
    background-color: $color-main;
  }
  /*拼团买*/
  .goods-auth {
    text-align: center;
    /deep/ .van-button {
      margin-right: 20px;
    }
    img {
      margin-top: 50px;
      width: 100%;
    }
  }
</style>
