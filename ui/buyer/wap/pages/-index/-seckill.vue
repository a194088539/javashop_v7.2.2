<template>
  <div v-if="time !== false && goodsList && goodsList.length" class="seckill-container">
    <div class="title-seckill" @click="routerUrl">
	    <a class="seckill-left-link">
		    <strong class="seckill-tit-txt">
          <em>限时抢购</em>
          <img class="seckill-lightning" src="@/assets/images/icon-seckill-lightning.png" alt="">
        </strong>
        <div class="seckill-times">
          <strong class="seckill-nth">{{ timeLine.distance_time == 0 ? (onlyOne ? '距结束' : '距下轮开始') : '距开始' }}</strong>
          <!-- <img class="seckill-shizhong" src="@/assets/images/icon-seckill-shizhong.png" alt=""> -->
          <div class="seckill-timer" id="seckill_time">
            <span>{{ times.hours }}</span>：<span>{{ times.minutes }}</span>：<span>{{ times.seconds }}</span>
<!--            <div class="seckill-time"><span>{{ times.hours }}</span></div>-->
<!--            <span class="seckill-time-separator">:</span>-->
<!--            <div class="seckill-time"><span>{{ times.minutes }}</span></div>-->
<!--            <span class="seckill-time-separator">:</span>-->
<!--            <div class="seckill-time"><span>{{ times.seconds }}</span></div>-->
          </div>
        </div>
		    <span id="seckill_loading"></span>
	    </a>
	    <a class="seckill-more-link" @click.stop="routerUrl">
<!--		    <i class="seckill-more-icon">&gt;</i>-->
        <span>更多商品</span>
        <van-icon name="arrow" />
	    </a>
    </div>
    <div class="seckill-slider">
      <no-ssr>
        <swiper :options="swiperOptions">
          <swiper-slide v-for="(goods, index) in goodsList" :key="index" class="seckill-item">
            <div class="seckill-item-img">
              <a @click="routerGoods(goods)" class="seckill-new-link">
                <img :src="goods.goods_image" class="seckill-photo">
              </a>
            </div>
            <div class="seckill-item-price">
              <span class="new-price"><em>¥</em>{{ goods.seckill_price }}</span>
              <a class="original-price"><em>¥</em>{{ goods.original_price }}</a>
            </div>
          </swiper-slide>
        </swiper>
      </no-ssr>
    </div>
  </div>
</template>

<script>
  import * as API_Promotions from '@/api/promotions'
  import { Foundation } from '@/ui-utils'
  let wx
  if (process.browser) wx = require('weixin-js-sdk')
  export default {
    name: 'index-seckill',
    props: ['is-mini-programe'],
    data() {
      return {
        // 时间
        time: false,
        // 商品列表
        goodsList: '',
        // 时间
        times: {
          hours: '00',
          minutes: '00',
          seconds: '00'
        },
	      // 当前这一场的时刻信息
        timeLine: '',
        // 是否只有一场
        onlyOne: false,
        // swiper配置
        swiperOptions: {
          autoplay: false,
          slidesPerView: 'auto'
        }
      }
    },
    mounted() {
      this.GET_TimeLine()
    },
    methods: {
      routerUrl() {
        if (this.isMiniPrograme) {
          wx.miniProgram.navigateTo({ url: `/pages/seckill/seckill` })
        } else {
          this.$router.push('/seckill')
        }
      },
      routerGoods(goods) {
        if (this.isMiniPrograme) {
          wx.miniProgram.navigateTo({ url: `/pages/goods/goods?goods_id=${goods.goods_id}` })
        } else {
          this.$router.push(`/goods/${goods.goods_id}?sku_id=${goods.sku_id}`)
        }
      },
      /** 开始倒计时 */
      startCountDown() {
        this.interval = setInterval(() => {
          let { time } = this
          if (time <= 0) {
            clearInterval(this.interval)
            this.$alert(this.onlyOne ? '本轮限时抢购已结束！' : '下一轮限时抢购已经开始啦！请确认查看', function () {
              location.reload()
            })
            return false
          }
          time -= 1
          const timeText = Foundation.countTimeDown(time)
          this.$set(this, 'times', Foundation.countTimeDown(time))
          this.$set(this, 'time', time)
        }, 1000)
      },
      /** 获取时间段 */
      GET_TimeLine() {
        API_Promotions.getSeckillTimeLine().then(response => {
          if (!response || !response.length) {
            return
          }
          response = response.sort((x, y) => (Number(x.time_text) - Number(y.time_text)))
          const onlyOne = response.length === 1
          this.onlyOne = onlyOne
          this.time = response[0].distance_time != 0 ? response[0].distance_time : onlyOne ? Foundation.theNextDayTime() : response[1].distance_time
          this.startCountDown()
	        this.timeLine = response[0]
          this.GET_GoodsList(response[0].time_text)
        })
      },
      /** 获取商品列表 */
      GET_GoodsList(range_time) {
        API_Promotions.getSeckillTimeGoods({ range_time }).then(response => {
          this.goodsList = response.data
        })
      }
    },
    destroyed() {
      this.interval && clearInterval(this.interval)
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .seckill-container {
    border:1px solid #dfdfdf;
    border-radius: 5px;
    box-shadow: 0 1px 1px #f2f2f2;
    background: #ff4f04;
    margin: 18px 12px;
  }
  .title-seckill {
    display: flex;
    height: 34px;
    line-height: 34px;
    vertical-align: middle;
    padding:10px 0px;
    padding-right: 6px;
    // border-bottom:1px solid #dfdfdf;
    .seckill-left-link {
      display: flex;
      width: 75%;
    }
    .seckill-more-link{
      width:25%;
      text-align: end;
      span{
        color:#fff;
        margin-right:5px;
      }
    }
    .seckill-tit-txt {
      margin: 0 6px 0 6px;
      display: inline-block;
      vertical-align: middle;
      font-size: 16px;
      color: #fff;
      font-weight: 700;
      padding-left:5px;
      em{
        font-style: italic;
      }
      .seckill-lightning {
        vertical-align: middle;
        width: 18px;
        margin-left: 5px;
      }
    }
    .seckill-times{
      /*display: flex;*/
      // background: #ff5f2e;
      padding: 0 2px;
      border-radius: 5px;
      color: #fff;
      height: 22px;
      line-height: 22px;
      margin: 0;
      position: relative;
      transform: translate(0, 7px);
      .seckill-shizhong{
        width: 14px;
        height: 14px;
        vertical-align: text-bottom;
        margin-left: 6px;
      }
    }
    .seckill-nth {
      font-size: 12px;
      display: inline-block;
      em {
        font-size: 13px;
        position: relative;
        top: 1px;
      }
    }
    .seckill-timer {
      display: inline-block;
      /*vertical-align: middle;*/
      margin-left: 6px;
      color:#fff;
      -moz-margin:-2px 0 0 0;
      span{
        background:#000000;
        padding: 2px 4px;
        border-radius: 2px;
      }
    }
    .seckill-time, .seckill-time-separator {
      float: left;
      display: inline-block;
      line-height: 16px;
      height: 16px;
      font-size: 12px;
      text-align: center;
    }
    .seckill-time-separator {
      width: 6px;
    }
    .seckill-more-link {
      .van-icon-arrow {
        width: 18px;
        height: 18px;
        background-color: #fff;
        color: #ff5f2e;
        border-radius: 100%;
        display: inline-block;
        font-size: 10px;
        vertical-align: middle;
        text-align: center;
        line-height: 19px;
      }
    }
  }
  @media screen and (max-width: 376px){
    .title-seckill{
      .seckill-tit-txt{
        margin: 0;
        .seckill-lightning{
          width: 12px;
        }
      }
      .seckill-timer{
        span{
          padding:2px 3px;
        }
      }
    }
  }
  @media screen and (max-width: 320px) {
    .title-seckill {
      padding-right: 3px;
      .seckill-tit-txt {
        width: 65px;
        font-size: 12px;
        margin: 0;
        em{
          font-style: italic;
        }
        .seckill-lightning {
          margin-left: 2px;
          width: 11px;
        }
      }
      .seckill-left-link {
        width: calc(100% - 82px);
        .seckill-times {
          margin: 0;
          padding: 0 3px;
          font-size: 12px;
          .seckill-shizhong {
            margin-left: 3px;
          }
          .seckill-timer {
            margin-left: 3px;
            span{
              padding:0;
            }
          }
        }
      }
      .seckill-more-link {
        margin-left: 2px;
      }
    }
    .seckill-slider{
      .seckill-item-img{
        height:60px;
      }
    }
    
  }
  .seckill-slider {
    width: 100%;
    height: 100%;
    overflow: hidden;
    font-size: 0;
    .swiper-container{
      padding:0 2% 2% 2%;
    }
  }
  .swiper-wrapper {
    margin-left: -1px;
    .seckill-item:nth-child(4){
      margin-right:0;
    }
  }
  .seckill-item {
    width:23.5%;
    // border-left: 1px solid #e3e3e3;
    background:#fff;
    overflow: hidden;
    margin-right:2%;
    border-radius: 5px;
  }
  .seckill-item-img {
    width: 100%;
    height:70px;
    padding: 5px 10px;
    box-sizing: border-box;
    margin-top: 5px;
    .seckill-new-link {
      display: block;
      position: relative;
      width:100%;
      height:100%;
    }
    img {
      width: 100%;
      height: 100%;
      overflow: hidden;
    }
  }
  .seckill-item-price {
    margin: 0 auto;
    text-align: center;
    .new-price {
      margin-top: 10px;
      display: block;
      color: #000000;
      font-size: 14px;
      line-height: 16px;
      height: 16px;
      text-align: center;
      font-weight: 700;
      em {
        font-size: 11px;
        padding-right: 2px;
      }
    }
    .original-price {
      color: #686868;
      font-size: 12px;
      line-height: 12px;
      margin: 4px 0 11px;
      text-align: center;
      padding: 0 2px;
      display: inline-block;
      position: relative;
      text-decoration: line-through;
      word-break:break-word;
      em {
        font-size: 9px;
        position: relative;
        padding-right: 2px;
      }
    }
  }
</style>
