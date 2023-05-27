<template>
  <div id="complaint-apply">
    <nav-bar title="投诉详情"/>
    <div class="complaint-apply-container">
      <!-- 投诉表单 -->
      <van-cell-group>
        <van-cell title="投诉主题:" is-link @click="radioTypes.length ? (popupShow = !popupShow) : $message.error('没有投诉主题！')">
          <div class="theme">
            <p>{{currentComplaintTheme.topic_name}}</p>
            <p style="color: #666;">{{currentComplaintTheme.topic_remark}}</p>
          </div>
        </van-cell>
        <van-cell title="投诉内容:" required>
          <van-field v-model="complaintForm.content" :maxlength="200" type="textarea" placeholder="请输入投诉内容" style="padding: 0;" />
        </van-cell>
        <van-cell title="投诉凭证:">
          <div>
            <ul class="image-list">
              <li v-for="image in complaint_images" :key="image.name" @click="previewImage(image)">
                <div class="image-box">
                  <img :src="image.url" />
                </div>
                <van-icon name="clear" class="remove-image" @click="removeImage($event, image)"/>
              </li>
              <li v-show="complaint_images.length < 3">
                <van-icon name="photograph" />
                <van-uploader :after-read="onRead" class="uploader-input"></van-uploader>
              </li>
            </ul>
            <p>凭证限定3张图片</p>
          </div>
        </van-cell>
      </van-cell-group>

      <div class="footer-btn">
        <van-button @click="$router.back()">取消</van-button>
        <van-button type="primary" @click="submitComplaintForm">提交</van-button>
      </div>

      <!-- 选择投诉主题的弹框 -->
      <van-popup v-model="popupShow" position="bottom" :style="{ maxHeight: '70%' }">
        <van-radio-group v-model="complaintForm.complain_topic" @change="popupShow = false">
          <van-radio
            class="van-radio-style"
            v-for="item in radioTypes"
            :name="item.topic_id"
            :key="item.topic_id">
            <div style="display: flex;flex-direction: column;">
              <span style="color: #333; word-break: break-all;">{{item.topic_name}}</span>
              <span style="color: #666; word-break: break-all;">{{item.topic_remark}}</span>
            </div>
          </van-radio>
        </van-radio-group>
      </van-popup>
    </div>
  </div>
</template>

<script>
  import * as API_Complaint from '@/api/complaint'
  import * as Api_Common from '@/api/common'
  import Vue from 'vue'
  import { ImagePreview } from 'vant'
  Vue.use(ImagePreview)
  export default {
    name: 'complaint-apply',
    data() {
      return {
        // 申请表单
        complaintForm: {},
        order_sn: this.$route.query.order_sn,
        radioTypes: [],
        complaint_images: [],
        popupShow: false
      }
    },
    computed: {
      currentComplaintTheme() {
        const defaultComplaintTheme = { topic_name: '请选择投诉主题', topic_remark: '' }
        return this.radioTypes.find(item => item.topic_id == this.complaintForm.complain_topic) || defaultComplaintTheme
      }
    },
    mounted() {
      this.GET_ComplaintTheme()
    },
    methods: {
      /** 提交投诉表单 **/
      submitComplaintForm() {
        let params = JSON.parse(JSON.stringify(this.complaintForm))
        if (!params.content) {
          return this.$message.error('请填写投诉内容!')
        }
        params.images = this.complaint_images.map(item => item.url).join(',')
        params.complain_topic = (this.radioTypes.find(item => item.topic_id == params.complain_topic) || {}).topic_name || ''
        API_Complaint.appendComplaint({ ...params, ...this.$route.query}).then(response => {
          this.$router.replace('/member/my-complaint')
        }).catch(_ => this.$message.error('提交失败!'))
      },
      /** 图片预览 **/
      previewImage(image) {
        ImagePreview([image.url])
      },
      /** 删除图片 **/
      removeImage(e, image) {
        e.stopPropagation()
        this.complaint_images = this.complaint_images.filter(item => item.url !== image.url)
      },
      /** 上传图片 **/
      onRead(value) {
        const param = new FormData();
        param.append("file", value.file);
        Api_Common.uploadImages(param).then(response => {
          this.complaint_images.push(response)
        }).catch(_ => {
          this.$message.error('上传失败!')
        })
      },
      /** 获取投诉主题列表 */
      GET_ComplaintTheme() {
        API_Complaint.getComplaintTheme().then(response => {
          if (Array.isArray(response) && response.length) {
            this.radioTypes = response
          }
        })
      },
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .van-cell--clickable:active {
    background-color: inherit;
  }

  .complaint-apply-container {
    padding-top: 46px;
    .theme {
      p {
        word-break: break-all;
      }
    }
    .van-radio-style {
      padding: 10px;
      display: flex;
      border-bottom: 1px solid #ccc;
    }

    .image-list {
      display: flex;
      flex-wrap: wrap;
      margin-top: 10px;

      li {
        position: relative;
        margin: 0 8px 8px 0;
        box-sizing: border-box;
        .image-box {
          width: 75px;
          height: 75px;
          border-radius: 8px;
          img {
            display: block;
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        .remove-image {
          position: absolute;
          top: -8px;
          right: -8px;
          color: #969799;
          font-size: 18px;
          background-color: #fff;
          border-radius: 100%;
        }

        &:last-child {
          position: relative;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          box-sizing: border-box;
          width: 75px;
          height: 75px;
          margin: 0 8px 8px 0;
          background-color: #f7f8fa;
          border-radius: 8px;
          &:active {
            background-color: #f2f3f5;
          }
          i {
            color: #dcdee0;
            font-size: 24px;
          }
          .uploader-input {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            overflow: hidden;
            cursor: pointer;
            opacity: 0;
          }
        }
      }
    }

    .footer-btn {
      position: fixed;
      bottom: 0;
      left: 0;
      right: 0;
      display: flex;
      justify-content: center;
      margin-top: 10px;
      button {
        border-radius: 0;
        flex: 1;
        &:nth-child(2) {
          background: #f42424;
          border-color: #f42424;
        }
      }
    }

    /deep/ .van-cell-group .van-cell__title {
      flex: 0 0 70px;
    }
    /deep/ .van-cell-group .van-cell__value {
      text-align: left;
    }

  }
</style>
