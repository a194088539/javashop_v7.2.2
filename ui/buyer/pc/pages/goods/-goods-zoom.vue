<template>
   <div id="goods-zoom" class="goods-zoom-container">
    <div class="swiper-container-zoom">
      <div class="swiper-wrapper">
        <a
          v-for="item in images"
          :key="item.small"
          class="swiper-slide"
          :class="[item.active && 'active']"
          @mouseenter="handleHoverTumb($event, item)"
          @mouseleave="handleHoverTumb($event, item)"
        >
          <img :src="item.small">
          <i class="icon-thumb-focus"></i>
        </a>
      </div>
      <template v-if="images.length > 5">
        <div class="swiper-btn swiper-btn-prev">
          <i class="iconfont ea-icon-arrow-up"></i>
        </div>
        <div class="swiper-btn swiper-btn-next">
          <i class="iconfont ea-icon-arrow-down"></i>
        </div>
      </template>
    </div>
    <div class="zoom-box">
      <pic-zoom v-if="!showGoodsVideo" :url="current.original" :big-url="current.original" :show-eidt="true"  style="width: 400px;height: 400px"></pic-zoom>
      
      <div class="zoom-video-btn" v-if="!showGoodsVideo && goodsVideo">
        <div class="preview-btn"><span class="video-icon" @click="showGoodsVideo = true"></span></div>
      </div>
      <div class="zoom-video" v-if="showGoodsVideo">
        <video 
          class="v-video"
          autoplay
          controls 
          width="100%" 
          height="100%" 
          @ended="showGoodsVideo = false"
        >
          <source 
            type="video/mp4" 
            :src="goodsVideo">
        </video>
        <i class="iconfont el-icon-close" @click.stop="showGoodsVideo = false"></i>
      </div>
    </div>
  </div>
</template>

<script>
  /**
   * 商品相册模块
   * 缩略图啥的
   */
  // import Swiper from 'swiper'
  import Swiper from '../../node_modules/swiper/dist/js/swiper.min.js'
  export default {
    name: "goods-zoom",
    props: ['images', 'specImg', 'goodsVideo'],
    data() {
      const _current = this.images[0]
      _current.active = true
      return {
        current: _current,
        /** 是否显示商品视频 */
        showGoodsVideo: false
      }
    },
    mounted() {
      const _current = this.images[0]
      if (this.goodsVideo) {
        this.images.unshift({
          active: true,
          small: _current.original,
          goods_video: this.goodsVideo
        })
      } else {
        _current.active = true
        this.current = _current
      }
      this.$nextTick(() => {
        this.initZoomSwiper()
      })
    },
    watch: {
      images(newVal) {
        this.current = { original: newVal[0].original }
      },
      specImg(newVal, oldVal) {
        this.current = { original: newVal.original }
      }
    },
    methods: {
      /** 初始化图片相册 */
      initZoomSwiper() {
        this.zoomSwiper = new Swiper('.swiper-container-zoom', {
          direction : 'vertical',
          loop: false,
          slidesPerView: 5,
          spaceBetween: 0,
          simulateTouch: false,
          allowTouchMove: false,
          navigation: {
            nextEl: '.swiper-btn-next',
            prevEl: '.swiper-btn-prev'
          }
        })
      },
      /** 鼠标悬浮在左侧缩略图上，切换放大镜图片 */
      handleHoverTumb(event, image) {
        this.showGoodsVideo = false
        if (event.type === 'mouseenter') {
          this.images.map(item => {
            item.active = false
            return item
          })
            image.active = true
            this.current = image
    
        }
      },
    },
    destroyed() {
      // 当组件销毁时，移除放大镜的canvas节点
      const mcc = document.querySelector('.mouse-cover-canvas')
      mcc && mcc.remove()
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  img { width: 100%; height: 100% }
  .goods-zoom-container {
    display: flex;
    width: 480px;
    height: 400px;
    border: 1px solid #ededed;
  }
  .swiper-container-zoom {
    position: relative;
    overflow: hidden;
    width: 80px;
    height: 400px;
    border-right: 1px solid #ededed;
    .swiper-wrapper { flex-direction: column }
    .swiper-slide {
      display: block;
      position: relative;
      width: 80px;
      height: 80px;
      cursor: pointer;
      &.active .icon-thumb-focus {
        display: block;
      }
    }
    .icon-thumb-focus {
      display: none;
      width: 80px;
      height: 80px;
      position: absolute;
      left: 0;
      top: 0;
      background: url(../../assets/images/icons-goods.png) no-repeat -141px -238px;
    }
    .swiper-btn {
      position: absolute;
      width: 100%;
      height: 20px;
      background-color: rgba(0, 0, 0, 0.6);
      z-index: 10;
      text-align: center;
      cursor: pointer;
      opacity: 0;
      transition: all ease-out .2s;
      .iconfont {
        color: #fff;
        font-size: 22px;
        line-height: 20px;
      }
    }
    &:hover .swiper-btn { opacity: 1 }
    .swiper-btn-prev { top: 0 }
    .swiper-btn-next { bottom: 0 }
  }
  .zoom-box {
    width: 400px;
    height: 400px;
    /deep/ img {
      width: 100% !important;
      height: 100% !important;
    }
    .zoom-video-btn {
      position: relative;
      .preview-btn {
        position: absolute;
        left: 0;
        top: -200px;
        z-index: 5;
        width: 100%;
        display: flex;
        justify-content: center;
        text-align: center;
        .video-icon{
          cursor: pointer;
          width: 50px;
          height: 50px;
          background: url(../../assets/images/main-circles.png) 0 -55px no-repeat;
        }
      }
    }
    .zoom-video {
      position: relative;
      background: #000;
      color: #fff;
      height: 100%;
      display: flex;
      justify-content: center;
      text-align: center;
      overflow: hidden;
      .video-player {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
      }
      i {
        position: absolute;
        top: 10px;
        right: 10px;
        z-index: 10;
      }
    }
  }
</style>
