<template>
  <div id="account-balance">
    <div class="member-nav">
      <ul class="member-nav-list">
        <li :class="[type === 1 && 'active']">
          <nuxt-link to="/member/account-balance">账户余额</nuxt-link>
        </li>
        <li :class="[type === 2 && 'active']" v-if="isRecharge || type === 'recharge'">
          <nuxt-link to="/member/account-balance?type=recharge">充值</nuxt-link>
        </li>
      </ul>
    </div>
    <div class="balance-container" v-show="type !== 'recharge'">
      <div class="balance-tips">
        <div class="balance">
          ￥<em>{{ account_balance | unitPrice('', 'before') }}</em>.{{ account_balance | unitPrice('', 'after') }}
        </div>
        <div class="recharge" @click="handleRecharge">
          <span>充值</span>
        </div>
      </div>
      <div class="balance-information">
        <div class="title">
          <span :class="[ tabActive == 0 ? 'active' : '' ]" @click="tabActive = 0">余额日志</span>
          <span :class="[ tabActive == 1 ? 'active' : '' ]" @click="tabActive = 1">充值明细</span>
        </div>
        <div class="balance-journal" v-if="tabActive === 0">
          <el-table :data="balanceLogList.data" border style="width:100%;">
            <el-table-column label="变更时间" align="center" width="250">
              <template slot-scope="scope">{{ scope.row.time | unixToDate }}</template>
            </el-table-column>
            <el-table-column label="金额" align="center" width="250">
              <template slot-scope="{ row }">
                <span v-if="row.money > 0" style="color: #DF6157;font-weight: 600;">+{{ row.money | unitPrice }}</span>
                <span v-else style="color: #7FCA97;font-weight: 600;">{{ row.money | unitPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="detail" label="变更说明" align="center" />
          </el-table>
          <el-pagination v-if="balanceLogList.data" @current-change="handleCurrentPageChange" layout="total, prev, pager, next" :total="balanceLogList.data_total">
          </el-pagination>
        </div>
        <div class="balance-detailed" v-if="rechargeDetailsList.data">
          <el-table :data="rechargeDetailsList.data" border style="width:100%;">
            <el-table-column label="充值单号" prop="recharge_sn" align="center"></el-table-column>
            <el-table-column label="创建时间" align="center">
              <template slot-scope="scope">{{ scope.row.recharge_time | unixToDate }}</template>
            </el-table-column>
            <el-table-column label="支付方式" prop="recharge_way" align="center" width="150" />
            <el-table-column label="支付状态" align="center">
              <template slot-scope="scope">{{ scope.row.pay_status | typeFilter }}</template>
            </el-table-column>
            <el-table-column label="充值金额" align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.recharge_money | unitPrice('￥') }}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination v-if="rechargeDetailsList.data" @current-change="handleCurrentPageChange" layout="total, prev, pager, next" :total="rechargeDetailsList.data_total">
          </el-pagination>
        </div>
      </div>
    </div>
    <div class="recharge-container" v-show="type === 'recharge'">
      <div class="recharge-box">
        <el-form :model="rechargeFrom" ref="rechargeFrom" :rules="rechargeFromRules" label-width="100px">
          <el-form-item label="充值金额:" prop="price" style="background: #FCFCFC;padding: 20px 30px 20px 20px;">
            <el-input v-model="rechargeFrom.price" auto-complete="off">
              <template slot="append">元</template>
            </el-input>
          </el-form-item>
          <el-form-item style="padding: 15px 0;">
            <el-button type="primary" @click="submitRechargeForm('rechargeFrom')">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>
<script>
  import * as API_Deposit from '@/api/deposit'
  import { RegExp } from '@/ui-utils'
  import { mapState } from 'vuex'
  export default {
    name: 'account-balance',
    head() {
      return {
        title: `账户余额-${this.site.title}`
      }
    },
    data() {
      const checkRechargeAmount = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('充值金额不能为空且不可为0'))
        }
        setTimeout(() => {
          if (!RegExp.money.test(value)) {
            callback(new Error('请输入正整数或者两位小数'))
          } else if (parseInt(value) < 1) {
            callback(new Error('充值金额最少1元'))
          } else {
            callback()
          }
        }, 1000)
      };
      return {
        type: this.$route.query.type,
        tabActive: 0,
        params: {
          page_no:1,
          page_size:10
        },
        /** 账户余额 */
        account_balance:'',
        /** 余额日志 */
        balanceLogList: [],
        /** 充值明细 */
        rechargeDetailsList: [],
        /** 是否充值账户 */
        isRecharge: false,
        /** 充值表单 */
        rechargeFrom: {
          price: ''
        },
        /** 提交校验 */
        rechargeFromRules: {
          price: [
            { required: true, message: '充值金额不能为空', trigger: 'blur' },
            { validator: checkRechargeAmount, trigger: 'blur' }
          ]
        }
      }
    },
    watch: {
      $route() {
        this.type = this.$route.query.type
      },
      'tabActive': function (newVal) {
        this.params.page_no = 1
        this.balanceLogList = []
        this.rechargeDetailsList = []
        if (newVal === 0) {
          this.GET_DepositLogList()
        } else {
          this.GET_RechargeList()
        }
      }
    },
    filters: {
      typeFilter(val) {
        switch (val) {
          case 'PAY_YES': return '已支付'
          case 'PAY_NO': return '未支付'
        }
      }
    },
    mounted() {
      this.GET_DepositBalance()
      this.tabActive === 0 ? this.GET_DepositLogList() : this.GET_RechargeList()
    },
    methods: {
      /** 获取账户余额 */
      GET_DepositBalance() {
        API_Deposit.getDepositBalance().then(response => {
          this.account_balance = response
        })
      },
      /** 获取余额日志 */
      GET_DepositLogList() {
        API_Deposit.getDepositLogsList(this.params).then(response => {
          this.balanceLogList = response
        })
      },
      /** 获取充值明细 */
      GET_RechargeList() {
        API_Deposit.getRechargeList(this.params).then(response => {
          this.rechargeDetailsList = response
        })
      },
      /** 页面发生改变时 */
      handleCurrentPageChange(page) {
        this.params.page_no = page
        if (this.tabActive === 0) {
          this.GET_DepositLogList()
        } else {
          this.GET_RechargeList()
        }
      },
      /** 去充值 */
      handleRecharge() {
        this.isRecharge = true
        this.$router.push('/member/account-balance?type=recharge')
      },
      /** 提交充值 */
      submitRechargeForm(formName) {
        this.$refs[formName].validate((valid) => {
          if(valid) {
            const { price } = this.rechargeFrom
            this.$router.push({ path: '/checkout/cashier?price=' + price })
          } else {
            return false
          }
        })
      }
    }
  }
</script>
<style type="text/scss" lang="scss" scoped>
  .balance-container {
    padding-top: 15px;
    .balance-tips {
      padding:0 0 20px 20px;
      overflow: hidden;
      height: 100px;
      line-height: 100px;
      display: flex;
      .balance {
        font-size: 25px;
        color: #474E5D;
        font-weight: bold;
        cursor: pointer;
        em {
          font-size: 50px;
        }
      }
      .recharge {
        margin-left: 35px;
        cursor: pointer;
        span {
          border: 1px solid #000;
          border-radius: 5px;
          padding: 13px 60px;
        }
      }
    }
    .balance-information {
      .title {
        margin-bottom: 15px;
        span {
          margin-left:20px;
          cursor: pointer;
        }
        span.active {
          color: #f42424;
          border-bottom: 2px solid #f42424;
        }
      }
      /deep/ .el-pagination {
        text-align: right;
        margin-top: 15px;
      }
    }
  }
  .recharge-container {
    padding: 30px 20px;
    width: 300px;
    .recharge-box {
      /deep/ .el-form-item {
        margin: 0;
      }
      /deep/ .el-button--primary {
        background-color: #48CFAE;
        border-color: #48CFAE;
        width: 100px;
      }
    }
  }
</style>
