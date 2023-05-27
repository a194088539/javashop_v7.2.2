<template>
  <div id="complaint-detail" style="background-color: #f7f7f7">
    <nav-bar title="投诉详情"/>
    <div class="complaint-container">
      <van-steps
        :active="active">
        <van-step
          v-for="(step, index) in flow"
          :key="index">
          {{ step.text }}
        </van-step>
      </van-steps>
      <div class="complaint-items">
        <div class="complaint-title"><van-icon name="description" />投诉信息</div>
        <div class="complaint-item">
          <span>投诉商品：</span><span>{{ complaintDetail.goods_name }}</span>
        </div>
        <div class="complaint-item">
          <span>投诉主题：</span><span>{{ complaintDetail.complain_topic }}</span>
        </div>
        <div class="complaint-item">
          <span>投诉时间：</span><span>{{ complaintDetail.create_time | unixToDate }}</span>
        </div>
        <div class="complaint-item">
          <span>投诉内容：</span><span>{{ complaintDetail.content }}</span>
        </div>
        <div class="complaint-item">
          <span class="complaint-info">投诉凭证：</span>
          <span v-if="images">
            <img
              class="complaint-img-item"
              :key="index"
              v-for="(image, index) in images"
              :src="image"
              @click="previewImage(image)"
            />
          </span>
        </div>
      </div>
      <div class="complaint-items" v-if="complaintDetail.appeal_content">
        <div class="complaint-title"><van-icon name="description" />商家申诉信息</div>
        <div class="complaint-item">
          <span>申诉时间：</span><span>{{ complaintDetail.appeal_time | unixToDate }}</span>
        </div>
        <div class="complaint-item">
          <span>申诉内容：</span><span>{{ complaintDetail.appeal_content }}</span>
        </div>
        <div class="complaint-item">
          <span class="complaint-info">申诉凭证：</span>
          <span v-if="appeal_images">
            <img
              class="complaint-img-item"
              :key="index"
              v-for="(image, index) in appeal_images"
              :src="image"
              @click="previewImage(image)"
            />
          </span>
        </div>
      </div>
      <div class="complaint-items" v-if="complaintDetail.status === 'COMMUNICATION'|| complaintDetail.status === 'WAIT_ARBITRATION' || complaintDetail.status === 'COMPLETE'">
        <div class="complaint-title"><van-icon name="chat" />对话详情</div>
        <div class="complaint-item">
          <span class="complaint-item-chat-history">对话记录：</span>
          <div v-if="communication_list && communication_list.length" class="communication" ref="communicationBox">
            <p v-for="item in communication_list" :key="item.communication_id">{{item.owner}}[{{item.create_time | unixToDate('yyyy-MM-dd hh:mm:ss')}}]：{{item.content}}</p>
          </div>
          <div class="communication" v-else>
            <p>暂无对话</p>
          </div>
        </div>
      </div>
      <div class="complaint-items" v-if="complaintDetail.arbitration_result">
        <div class="complaint-title"><van-icon name="description" />平台仲裁</div>
        <div class="complaint-item">
          <span>仲裁意见：</span><span>{{ complaintDetail.arbitration_result }}</span>
        </div>
      </div>
      <div class="comment-box" v-if="complaintDetail.status === 'COMMUNICATION' || complaintDetail.status === 'WAIT_ARBITRATION'">
        <van-field v-model="commentValue" type="textarea" placeholder="编辑内容，字数限制200字" maxlength="200"></van-field>
        <van-button @click="submitDialogueForm">发送</van-button>
      </div>

    </div>
  </div>
</template>

<script>
  import * as API_Complaint from '@/api/complaint'
  import Vue from 'vue'
  import { ImagePreview } from 'vant'
  Vue.use(ImagePreview)
  export default {
    name: 'complaint-detail',
    data() {
      return {
        // 交易投诉id
        id: this.$route.query.id,
        // 交易投诉详情数据
        complaintDetail: '',
        // 投诉凭证
        images: [],
        // 商家申诉凭证
        appeal_images: [],
        // 对话记录
        communication_list: [],
        // 流程图数据
        flow: '',
        active: 0,
        // 用户输入的评论  对话表单
        commentValue: ''
      }
    },
    mounted() {
      this.GET_complaintDetail()
    },
    methods: {
      /** 图片预览 **/
      previewImage(image) {
        ImagePreview([image])
      },
      /** 发送对话 **/
      submitDialogueForm() {
        if (!this.commentValue) return this.$message.error('请输入对话内容！')
        if (/^\s+/.test(this.commentValue)) {
          return this.$message.error('不允许以空格开头！')
        }
        API_Complaint.initiationSession(this.id, { content: this.commentValue }).then(response => {
          this.GET_complaintDetail()
          this.commentValue = ''
          this.$refs.communicationBox && (this.$refs.communicationBox.scrollTop = 0)
        })
      },
      /** 获取详情信息 **/
      GET_complaintDetail() {
        API_Complaint.getComplaintDetail(this.id).then(response => {
          this.complaintDetail = response
          this.images = response.images ? response.images.split(',') : []
          this.appeal_images = response.appeal_images ? response.appeal_images.split(',') : []
          this.communication_list = response.communication_list ? response.communication_list.reverse() : []
        })
        /** 交易投诉流程 **/
        API_Complaint.getComplaintFlow(this.id).then(response => {
          this.flow = response
          const index = response.findIndex(item => item.show_status === 0);
          this.active = index === -1 ? response.length - 1 : index - 1
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  /deep/ .van-step--horizontal.van-step--finish {
    color: #06bf04;
  }
  .complaint-container {
    padding-top: 45px;
    .complaint-items {
      background-color: #fff;
      padding: 0 10px;
      margin:10px 0;
      span {
        word-break: break-all;
      }
      .complaint-title {
        font-size: 14px;
        position: relative;
        display: flex;
        align-items: center;
        line-height: 34px;
        i { margin-right: 5px; }
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
      .complaint-item {
        padding: 5px 0;
        font-size: 14px;
        overflow: hidden;
        .complaint-info {
          float: left;
        }
        .complaint-img-item{
          float: left;
          display: block;
          width: 50px;
          height: 50px;
          margin-right: 10px;
          cursor: pointer;
          border: 1px solid #e2e2e2;
          padding: 1px;
          img{
            width: 100%;
            height: 100%;
          }
        }
        .complaint-item-chat-history {
          display: block;
          line-height: 30px;
        }
        .communication {
          background-color: #FFFFFF;
          border: 1px dashed #EEEEEE;
          max-height: 200px;
          word-break: normal;
          word-wrap: break-word;
          padding: 8px;
          overflow-y: scroll;
        }
      }
    }

    .comment-box {
      display: flex;
      margin-bottom: 20px;
      .van-cell {
        flex: 1;
        margin-left: 10px;
        border-radius: 5px;
      }
      button {
        width: 80px;
        margin: 0 10px;
        height: auto !important;
        color: #fff;
        background-color: #e4393c;
        border: 1px solid #e4393c;
      }
    }
  }
</style>
