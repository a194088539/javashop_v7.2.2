<template>
  <div id="log-detail">
    <div class="container">
      <div class="log-title">
        <div class="left">
          <div class="conditions">
            <span>服务名：</span>
            <el-select
              v-model="params.service_name"
              placeholder="请选择服务名">
              <el-option
                v-for="item in serviceNameList"
                :key="item.name"
                :label="item.name"
                :value="item.name"></el-option>
            </el-select>
          </div>
          <div class="conditions">
            <span>位于：</span>
            <el-select
              v-model="params.uuid"
              placeholder="请选择">
              <el-option
                v-for="item in instancesList"
                :key="item.uuid"
                :label="item.uuid"
                :value="item.uuid"></el-option>
            </el-select>
          </div>
          <div class="conditions" style="margin-left: 80px;">
            <span>日期：</span>
            <el-date-picker
              v-model="params.time"
              type="date"
              placeholder="选择日期"
              value-format="timestamp"
              :picker-options="{ disabledDate (time) { return time.getTime() > Date.now() }}">
            </el-date-picker>
          </div>
        </div>
        <div class="right">
          <div class="conditions" style="margin-right: 150px;">
            <span>自动刷新：</span>
            <el-switch
              v-model="params.auto_refresh_status"
              active-color="#13ce66"
              inactive-color="#f5f5f5">
            </el-switch>
          </div>
          <!-- <div class="conditions">
            <i class="icon iconfont el-icon-download" @click="downloadLogDetail"></i>
          </div> -->
        </div>
      </div>
      <!-- 日志详情 -->
      <div class="log-detail">
        <div v-for="(item,index) in logDetails">
          <p>{{ item }}</p>
        </div>
      </div>
      <div class="pagination">
        <span :class="{'disabled': params.page_no === 1}" class="first-page" @click="jumpPage(1)">首页</span>
        <span :class="{'disabled': params.page_no === 1}" @click="prevPage"><i class="iconfont el-icon-arrow-left"></i></span>
        <span :class="{'disabled': params.page_no === totalPage || !totalPage}" @click="nextPage"><i class="iconfont el-icon-arrow-right"></i></span>
        <span :class="{'disabled': params.page_no === totalPage || !totalPage}" class="last-page" @click="jumpPage(totalPage)">尾页</span>
      </div>
    </div>
  </div>
</template>

<script>
  import * as API_Logs from '@/api/logs'
  import { Foundation } from '~/ui-utils'
  export default {
    name: 'logDetails',
    data() {
      const { service_name } = this.$route.query
      return {
        // 列表loading状态
        loading: false,

        params: {
          page_no: 1,
          page_size: 100,
          time: Date.now(),
          service_name: service_name || '',
          uuid: '',
          auto_refresh_status: false
        },

        // 总页数
        totalPage: '',
        // 服务器名
        serviceNameList: [],
        // 位于
        instancesList: [],
        logDetails: [],
        interval: null
      }
    },
    watch: {
      'params.auto_refresh_status': function(newval) {
        if (newval) {
          this.interval = setInterval(() => {
            this.GET_LogDetails()
          }, 10000)
        } else {
          if (this.interval) {
            clearInterval(this.interval)
          }
        }
      },
      'params.service_name': function(newval) {
        if (newval) {
          this.params.service_name = newval
          this.params.page_no = 1
          API_Logs.getInstances(this.params.service_name).then(response => {
            this.instancesList = response
            this.params.uuid = response[0].uuid
            this.GET_LogDetails()
          })
        }
      },
      'params.time': function(newval) {
        this.logDetails = []
        this.totalPage = ''
        this.params.time = newval
        this.params.page_no = 1
        this.GET_LogDetails()
      }
    },
    activated() {
      if (!this.$route.query.service_name) return
      this.params.service_name = this.$route.query.service_name
    },
    beforeRouteUpdate(to, from, next) {
      if (!this.$route.query.service_name) return
      this.params.service_name = this.$route.query.service_name
      next()
    },
    created() {
      /** 页面刷新时保留当前页面数据 */
      window.addEventListener('beforeunload', () => {
        localStorage.setItem('params', JSON.stringify(this.params))
      })
      if (localStorage.getItem('params')) {
        this.params = JSON.parse(localStorage.getItem('params'))
      }
    },
    mounted() {
      this.GET_LogServices()
    },
    methods: {
      /** 上一页 */
      prevPage() {
        if (this.params.page_no > 1) {
          this.jumpPage(this.params.page_no - 1)
          this.GET_LogDetails()
        }
      },
      /** 下一页 */
      nextPage() {
        if (this.params.page_no < this.totalPage) {
          this.jumpPage(this.params.page_no + 1)
          this.GET_LogDetails()
        }
      },
      /** 首页尾页 */
      jumpPage(pageNumber) {
        if (pageNumber && this.params.page_no !== pageNumber) {
          this.params.page_no = pageNumber
          this.GET_LogDetails()
        }
      },
      /** 服务名 */
      GET_LogServices() {
        API_Logs.getLogServices().then(response => {
          this.serviceNameList = response
          if (!this.params.service_name) {
            this.params.service_name = response[0].name
          }
          API_Logs.getInstances(this.params.service_name).then(res => {
            this.instancesList = res
            this.params.uuid = res[0].uuid
            this.GET_LogDetails()
          })
        })
      },
      /** 获取日志详情 */
      GET_LogDetails() {
        const { service_name, uuid } = this.params
        const _params = {
          page_no: this.params.page_no,
          page_size: this.params.page_size,
          time: this.params.time
        }
        API_Logs.getLogDetails(service_name, uuid, _params).then(response => {
          this.logDetails = response.data
          this.totalPage = Math.ceil(response.data_total / this.params.page_size)
        })
      }
    },
    destroyed() {
      /** 清除定时器 */
      if (this.interval) {
        clearInterval(this.interval)
      }
      /** 清除缓存 */
      localStorage.removeItem('params')
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .container {
    .log-title {
      height: 70px;
      padding: 20px 0;
      display: flex;
      align-items: center;
      justify-content: space-between;
      border-bottom: 1px solid #e6ebf5;
      .left, .right {
        display: flex;
        align-items: center;
        .conditions {
          margin: 0 30px;
          position: relative;
          .icon {
            font-size: 30px;
            font-weight: 600;
          }
        }
      }
    }
    .log-detail {
      height: 1000px;
      max-height: 685px;
      background-color: #000;
      color: #fff;
      overflow-y: scroll;
      p {
        padding: 0 10px;
      }
    }
    .pagination {
      display: flex;
      justify-content: flex-end;
      padding-top: 15px;
      text-align: right;
      span {
        color: #303133;
        padding: 5px 8px;
        border-radius: 4px;
        cursor: pointer;
        margin-right: 20px;
        i {
          font-weight: 700;
        }
        &:hover {
          color: #409EFF;
        }
        &.first-page, &.last-page {
          border: 1px solid #dcdfe6;
        }
        &.first-page:hover, &.last-page:hover {
          border: 1px solid #409EFF;
          color: #303133;
        }
        &.disabled {
          color: #c0c4cc;
          cursor: not-allowed;
        }
        &.disabled.first-page, &.disabled.last-page {
          color: #c0c4cc;
          border: 1px solid #dcdfe6;
        }
      }
    }
  }
</style>
