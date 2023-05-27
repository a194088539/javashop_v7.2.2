<template>
  <div id="my-complaint" style="background-color: #f7f7f7">
    <van-nav-bar
      left-arrow
      title="交易投诉"
      @click-left="MixinRouterBack"
    >
    </van-nav-bar>
    <van-tabs v-model="tabActive" :swipe-threshold="4" @change="handleTabChange">
      <van-tab title="全部投诉"/>
      <van-tab title="进行中"/>
      <van-tab title="已完成"/>
      <van-tab title="已撤销"/>
    </van-tabs>
    <div class="complaint-container">
      <empty-member v-if="finished && !complaintList.length">暂无交易投诉</empty-member>
      <van-list
        v-else
        v-model="loading"
        :finished="finished"
        @load="onLoad">
        <div class="complaint-item" v-for="(complaint, index) in complaintList" :key="index">
          <div class="bh-complaint-item">
            <span>投诉编号：{{ complaint.complain_id }}</span>
            <span>主题：{{ complaint.complain_topic }}</span>
          </div>
          <nuxt-link :to="'/shop/' + complaint.seller_id" class="shop-complaint-item">
            <em>{{ complaint.seller_name }}</em>
          </nuxt-link>
          <div class="goods-complaint-item">
            <div class="goods-content">
              <nuxt-link :to="'/goods/' + complaint.goods_id" class="img-info">
                <img :src="complaint.goods_image" :alt="complaint.name">
              </nuxt-link>
              <div class="goods-item">
                <nuxt-link :to="'/goods/' + complaint.goods_id" class="goods-name">{{ complaint.goods_name }}</nuxt-link>
                <p>订单编号：{{ complaint.order_sn }}</p>
              </div>
            </div>
          </div>
          <div class="info-complaint-item">
            <p>状态：<i style="color:#3985ff;">{{ complaint.status | complainStatus }}</i></p>
            <div class="complaint-btns">
              <nuxt-link :to="'/member/my-complaint/detail?id=' + complaint.complain_id" class="detail">查看详情</nuxt-link>
              <a v-if="complaint.status === 'NEW'" @click="handleCancel(complaint.complain_id)">撤销投诉</a>
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script>
  import * as API_Complaint from '@/api/complaint'
  export default {
    name: 'my-complaint-index',
    head() {
      return {
        title: `交易投诉 -${this.site.title}`
      }
    },
    data() {
      const { tag } = this.$route.query
      return {
        // 加载中
        loading: false,
        // 是否全部已加载完成
        finished: false,
        // 列表数据
        complaintList: [],
        // 当前tab的index
        tabActive: this.getParam(tag),
        params: {
          page_no: 0,
          page_size: 10,
          tag: '',
        }
      }
    },
    filters: {
      // 状态过滤器
      complainStatus(status) {
        switch(status) {
          case 'NEW': return '新投诉'
          case 'CANCEL': return '已撤销'
          case 'WAIT_APPEAL': return '待申诉'
          case 'COMMUNICATION': return '对话中'
          case 'WAIT_ARBITRATION': return '等待仲裁'
          case 'COMPLETE': return '已完成'
        }
      }
    },
    methods: {
      /** 撤销投诉 **/
      handleCancel(id) {
        API_Complaint.cancelComplaint(id).then(response => {
          this.$message.success('撤销投诉成功！')
          this.finished = false
          this.complaintList = []
          this.params.page_no = 1
          this.GET_Complaint()
        })
      },
      /** tabIndex发生改变 **/
      handleTabChange(index) {
        this.finished = false
        this.complaintList = []
        this.params.page_no = 1
        this.params.tag = this.getParam(index)
        this.GET_Complaint()
      },
      /** 根据交易投诉状态获取tabActive */
      getParam(param) {
        switch (param) {
          case 0: return ''
          case 1: return 'COMPLAINING'
          case 2: return 'COMPLETE'
          case 3: return 'CANCELED'
          case '': return 0
          case 'COMPLAINING': return 1
          case 'COMPLETE': return 2
          case 'CANCELED': return 3
        }
      },
      /** 加载数据 */
      onLoad() {
        this.params.page_no += 1
        this.GET_Complaint()
      },
      /** 获取交易投诉列表 **/
      GET_Complaint() {
        const _params = JSON.parse(JSON.stringify(this.params))
        API_Complaint.getComplaintList(_params).then(response => {
          const { data } = response
          if(!data || !data.length) {
            this.finished = true
          } else {
            this.complaintList.push(...data)
          }
          this.loading = false
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../../assets/styles/color";
  .complaint-item{
    margin-top: 10px;
    background-color: #fff;
    padding: 0 10px 10px 10px;
    .bh-complaint-item {
      span{
        position: relative;
        height: 45px;
        line-height: 45px;
        font-size: 14px;
        color: #333;
        display: block;
        &:after{
          content: " ";
          position: absolute;
          z-index: 1;
          pointer-events: none;
          background-color: #e5e5e5;
          height: 1px;
          left: 0;
          right: 0;
          bottom: 0;
          margin: 0 -10px;
        }
      }
    }
    .shop-complaint-item {
      display: block;
      position: relative;
      height: 40px;
      line-height: 40px;
      font-size: 14px;
      color: $color-href;
      em {
        display: block;
        position: relative;
        padding-right: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-style: normal;
        &::after {
          position: absolute;
          content: "";
          display: block;
          width: 8px;
          height: 8px;
          border-top: 1px solid #666;
          border-left: 1px solid #666;
          transform-origin: 50%;
          transform: rotate(135deg);
          right: 5px;
          top: 14px;
        }
      }
    }
    .goods-complaint-item {
      padding-bottom: 10px;
      position: relative;
      &:after{
        content: " ";
        position: absolute;
        z-index: 1;
        pointer-events: none;
        background-color: #e5e5e5;
        height: 1px;
        left: 0;
        right: 0;
        bottom: 0;
        margin: 0 -10px;
      }
      .goods-content {
        display: block;
        overflow: hidden;
        position: relative;
        a.img-info {
          & > span {
            display: inline-block;
            position: absolute;
            bottom: 0;
            left: 27px;
            z-index: 9;
            background-color: $color-main;
            color: #fff;
          }
        }
        img {
          position: relative;
          display: block;
          width: 75px;
          height: 75px;
          float: left;
          margin-right: 10px;
        }
        .goods-item {
          .goods-name {
            color:#333;
            font-size: 14px;
            display: block;
            padding-bottom: 15px;
          }
        }
      }
    }
    .info-complaint-item {
      position: relative;
      display: block;
      padding: 12px 0;
      p {
        font-size: 14px;
        color: #333;
      }
      .complaint-btns {
        position: absolute;
        top: 7px;
        right: 0;
        a {
          display: inline-block;
          width: 65px;
          height: 30px;
          margin-left: 5px;
          text-align: center;
          color: #fff;
          line-height: 30px;
          z-index: 1;
          border-radius: 2px;
          background: #e4393c;
          -webkit-tap-highlight-color: rgba(0,0,0,0);
          outline: 0;
          border: none;
        }
      }
    }
  }
</style>
