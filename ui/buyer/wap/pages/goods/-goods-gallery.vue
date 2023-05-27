<template>
  <van-swipe class="goods-swipe" @change="onChange">
    <van-swipe-item v-if="goods_video" style="color: #fff;">
      <div class="goods-video">
        <video ref="video" class="v-video" controls width="100%" height="100%" type="video/mp4" :poster="data[0].original" :src="goods_video" @playing="state.playing = true" x5-playsinline playsinline="" webkit-playsinline="" x-webkit-airplay="true"  x5-video-player-type="h5-page">
        </video>
        <div class="preview-btn" v-if="state.controlBtnShow"><span class="video-icon" @click.stop="handleShowVideo"></span></div>
      </div>
      <div class="progress" ref="progress">
        <span class="progress-loaded" :style="{ width: `${video.loaded}%`}"></span>
      </div>
    </van-swipe-item>
    <van-swipe-item v-for="(gallery, index) in data" :key="index">
      <img style="width: 100%;height: 100%" :src="gallery.original" @click="handlePreviewImage(index)">
    </van-swipe-item>
  </van-swipe>
</template>

<script>
import { ImagePreview } from "vant";
import { throttle } from "@/utils/filters";
const pad = val => {
  val = Math.floor(val);
  if (val < 10) {
    return "0" + val;
  }
  return val + "";
};
const timeParse = sec => {
  let min = 0;
  min = Math.floor(sec / 60);
  sec = sec - min * 60;
  return pad(min) + ":" + pad(sec);
};
export default {
  name: "goods-gallery",
  props: ["data", "goods_video"],
  data() {
    return {
      // video元素
      $video: null,
      // 视频容器元素
      player: {
        $player: null,
        pos: null
      },
      // progress进度条元素
      progressBar: {
        $progress: null, // 进度条DOM对象
        pos: null
      },
      // video控制显示设置
      video: {
        loaded: 0, // 播放长度
        progress: {
          width: 0, // 进度条长度
          current: 0 // 进度条当前位置
        }
      },
      // 播放状态控制
      state: {
        controlBtnShow: false, // 播放按钮
        playing: false //播放状态
      }
    };
  },
  mounted() {
    if (!this.goods_video) return;
    this.init();
    this.clickPlayBtn();
  },
  methods: {
    onChange(index) {
      if( this.$video ){
        if (index != 0) {
          //暂停状态
          this.$video.pause();
        }
      }  
    },
    /** 商品图预览 */
    handlePreviewImage(index) {
      ImagePreview(this.data.map(item => item.original), index);
    },
    handleShowVideo() {
      this.clickPlayBtn();
    },
    init() {
      // 初始化video,获取video元素
      this.$video = this.$el.getElementsByTagName("video")[0];
      this.initPlayer();
    },
    // 初始化播放器容器, 获取video-player元素
    // getBoundingClientRect()以client可视区的左上角为基点进行位置计算
    initPlayer() {
      const $player = this.$el;
      const $progress = this.$el.getElementsByClassName("progress")[0];
      // 播放器位置
      this.player.$player = $player;
      this.progressBar.$progress = $progress;
      this.player.pos = $player.getBoundingClientRect();
      this.progressBar.pos = $progress.getBoundingClientRect();
      this.video.progress.width = Math.round(
        $progress.getBoundingClientRect().width
      );
    },
    // 点击播放 & 暂停按钮
    clickPlayBtn() {
      this.state.playing = !this.state.playing;
      this.state.controlBtnShow = false;
      if (this.$video) {
        // 播放状态
        if (this.state.playing) {
          this.$video.play();
          // 监听播放进度
          this.$video.addEventListener(
            "timeupdate",
            throttle(this.getPlayTime, 100, 1)
          );
          // 监听结束
          this.$video.addEventListener("ended", e => {
            // 重置状态
            this.state.controlBtnShow = true;
            this.state.playing = false;
            this.video.progress.current = 0;
            this.video.loaded = 0;
            this.$video.currentTime = 0;
            this.goods_video = this.goods_video;
          });
        } else {
          //暂停状态
          this.$video.pause();
        }
      }
    },
    // 获取播放时间
    getPlayTime() {
      const percent = this.$video.currentTime / this.$video.duration;
      this.video.progress.current = Math.round(
        this.video.progress.width * percent
      );
      this.video.loaded = percent * 100;
    }
  }
};
</script>

<style type="text/scss" lang="scss" scoped>
.goods-swipe {
  width: 100%;
  object-fit: contain;
  .goods-video {
    position: relative;
    background-color: #000;
    .v-video {
      width: 100%;
      object-fit: cover;
      object-position: center center;
    }
    .preview-btn {
      position: absolute;
      left: 0;
      top: 50%;
      z-index: 5;
      width: 100%;
      display: flex;
      justify-content: center;
      text-align: center;
      .video-icon {
        cursor: pointer;
        width: 50px;
        height: 50px;
        background: url(../../assets/images/main-circles.png) 0 -55px no-repeat;
      }
    }
  }
}
// 控制层
.control {
  width: 100%;
  height: 40px;
  color: #fff;
  display: flex;
  justify-items: center;
  align-items: center;
  overflow: hidden;
  background-color: transparent;
  .play {
    margin: 0 10px;
    a {
      color: #fff;
    }
  }
  &-bar {
    display: flex;
    align-items: center;
    span {
      font-size: 12px;
      color: #fff;
      font-weight: 500;
      white-space: nowrap;
    }

    .progress {
      position: relative;
      margin: 0 12px;
      width: 203px;
      height: 3px;
      background: #fff;
      border-radius: 3px;
      .progress-move {
        width: 100%;
        height: 26px;
        position: absolute;
        z-index: 100;
        left: 0;
        top: 0;
        transform: translateY(-50%);
        background-color: transparent;
      }
      .progress-btn {
        position: absolute;
        left: -10px;
        top: -16px;
        transition: all 16ms;
      }
      // 不会被转义
      .ignore {
        width: 18px;
        height: 18px;
      }
      .progress-loaded {
        background-color: #f44;
        position: absolute;
        left: 0;
        top: 0;
        width: 0;
        height: 100%;
        border-radius: 2px;
        transition: all 16ms;
        span {
          width: 14px;
          height: 14px;
          position: absolute;
          top: -6px;
          left: -8px;
          content: "";
          border-radius: 100%;
          background-color: #fff;
        }
      }
    }
    .full-screen {
      width: 26px;
      height: 26px;
      line-height: 24px;
      text-align: center;
      margin-left: 8px;
      img {
        width: 20px;
        height: 20px;
      }
    }
  }
}
.progress {
  width: 100%;
  height: 5px;
  background: #e8e8ed;
  position: relative;
  span {
    height: 5px;
    background: rgb(255, 68, 68);
    position: absolute;
    left: 0px;
  }
}
</style>
