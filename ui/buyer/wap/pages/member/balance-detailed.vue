<template>
  <div id="balance-detailed">
    <nav-bar title="明细"/>

    <van-tabs v-model="activeTab"  @change="handleTabChange">
      <van-tab title="余额日志" />
      <van-tab title="充值记录" />
    </van-tabs>

    <div class="order-container">
      <empty-member v-if="finished && !logList.length">暂无记录</empty-member>
      <van-list
        v-else
        v-model="loading"
        :finished="finished"
        @load="onLoad"
      >
        <div class="log-list" v-for="log in logList" :key="log.id">
          <!-- 消费明细 -->
          <template v-if="activeTab === 0">
            <div class="log-desc">
              <p class="detail">{{log.detail}}</p>
              <p class="data-time">{{log.time | unixToDate}}</p>
            </div>
            <div class="log-money">
              <span :style="{color: log.money > 0 ? '#f44' : '#093'}">
                {{log.money > 0 ? '+' : ''}}{{log.money | unitPrice}}
              </span>
            </div>
          </template>
          <!-- 充值记录 -->
          <template v-if="activeTab === 1">
            <div class="log-desc">
              <p class="detail">{{log.recharge_sn}}({{log.pay_status | paymentStatus}})</p>
              <p class="data-time">{{log.recharge_time | unixToDate}}</p>
            </div>
            <div class="log-money">
              <span :style="{color: log.recharge_money > 0 ? '#f44' : '#093'}">
                {{log.recharge_money > 0 ? '+' : ''}}{{log.recharge_money | unitPrice}}
              </span>
            </div>
          </template>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script>
  import * as API_deposit from '@/api/deposit'
  export default {
    name: 'balance-detailed',
    data() {
      return {
        activeTab: 0,
        finished: false,
        loading: false,
        logList: [],
        params: {
          page_no: 0,
          page_size: 10
        }
      }
    },
    filters: {
      /** 支付状态 **/
      paymentStatus(value) {
        switch (value) {
          case 'PAY_YES': return '已支付'
          case 'PAY_NO': return '未支付'
        }
      }
    },
    methods: {
      /** tabIndex发生改变 */
      handleTabChange(index) {
        this.finished = false
        this.logList = []
        this.params.page_no = 1
        this.getBalanceRechargeLogs(this.getParam(this.activeTab))
      },
      /** 根据订单状态获取tabActive */
      getParam(param) {
        switch (param) {
          case 0: return 'depositeLogs'
          case 1: return 'rechargeLogs'
        }
      },
      /** 加载数据 */
      onLoad() {
        this.params.page_no += 1
        this.getBalanceRechargeLogs(this.getParam(this.activeTab))
      },
      /** 获取充值记录 **/
      getBalanceRechargeLogs(status) {
        const params = JSON.parse(JSON.stringify(this.params))
        API_deposit[status === 'rechargeLogs' ? 'balanceRechargeLogs' : 'getDepositLogsList'](params).then(response => {
          const { data, page_no } = response
          if(!data || !data.length) {
            this.finished = true
          } else {
            if (page_no === 1) {
              this.logList = [...data]
            } else {
              this.logList.push(...data)
            }
          }
          this.loading = false
        })
      }
    }
  }
</script>

<style scoped lang="scss">
.log-list {
  display: flex;
  justify-content: space-between;
  padding: 0 10px;
  border-bottom: 1px solid #ccc;

  .log-desc {
    .detail {
      line-height: 200%;
    }
    .data-time {
      color: #999;
      margin-bottom: 5px;
    }
  }

  .log-money {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: 600;
  }
}
</style>
