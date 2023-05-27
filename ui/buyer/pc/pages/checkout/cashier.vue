<template>
  <div id="cashier">
    <en-header-other title="收银台"/>
    <div class="cashier-box" v-if="!price">
      <div class="cashier-change" v-if="isExistence">
        <h2 class="time-tip" v-if="order.pay_type_text === 'ONLINE' && order.need_pay_price !== 0 && (order.pay_status !== 'PAID_OFF' && order.pay_status !== 'PAY_YES')">
          订单提交成功，请您在<span>24小时</span>内完成支付，否则订单会被自动取消。
        </h2>
        <h2 class="time-tip" v-else-if="order.pay_type_text !== 'COD' || order.pay_status === 'PAID_OFF' || order.pay_status === 'PAY_YES'">订单金额为 <span>￥0.00</span>，您无需支付。</h2>
        <h2 v-if="this.trade_sn">
          交易号：<b>{{ trade_sn }}</b>
          <nuxt-link class="see-order-btn" to="/member/my-order">查看订单</nuxt-link>
        </h2>
        <h2 v-else>订单编号：<b>{{ order_sn }}</b>
          <nuxt-link class="see-order-btn" :to="'/member/my-order/detail?order_sn=' + order_sn">查看订单</nuxt-link>
        </h2>
        <h2>{{ !order ? '' : order.pay_type_text === 'ONLINE' ? '在线支付：' : '货到付款：' }}
          <span v-if="order">￥{{ order.need_pay_price | unitPrice }}</span>
          <span v-else>加载中...</span>
          <i>元</i>
          <span class="pay-tip">订单状态刷新可能会延迟，如果您已付款成功，请勿重复支付！</span>
        </h2>
        <div class="cashier-order-detail">
          <div class="cashier-order-inside">
            <h3><i></i>送货至：
              <template v-if="order">
                <span>{{ order.ship_province }}</span>
                <span>{{ order.ship_city }}</span>
                <span>{{ order.ship_county }}</span>
                <span>{{ order.ship_town || '' }}</span>
                <span>{{ order.ship_addr }}</span>
                <span>{{ order.ship_mobile }}</span>
              </template>
              <span v-else>加载中...</span>
            </h3>
          </div>
        </div>
        <div class="cashier-tools" v-if="order.pay_type_text === 'ONLINE' && order.need_pay_price !== 0 && (order.pay_status !== 'PAID_OFF' && order.pay_status !== 'PAY_YES')">
          <div class="pre-deposit">
            <div class="payment">
              <div class="pm-logo"></div>
              <div class="pm-notice">
                <i></i>
                温馨提醒：如遇退款情况，金额将退还至预存款余额上。
              </div>
            </div>
            <div class="pm-wrap">
              <div>
                <el-checkbox v-model="isCheckPreDeposit" v-bind="{'checked': isCheckPreDeposit ? true : false }" @change="handlePreDeposit" :disabled="order.deposite.balance === 0">使用预存款支付</el-checkbox>
                （可用余额：<span>￥{{ (order.deposite.balance || '0.00') | unitPrice}}</span>，目前还需在线支付<span>￥{{ order.need_pay_price | unitPrice }}</span>。）余额不足？
                <nuxt-link to="/member/account-balance?type=recharge">马上充值</nuxt-link>
              </div>
              <div class="step-tit" v-if="isCheckPreDeposit">
                <h3>支付密码</h3>
                <input class="pm-password" v-model="password" type="password" placeholder="请输入您的支付密码" autocompete="off">
                <div class="step-tip">
                  <i class="el-icon-warning"></i>
                  支付密码与用户登录密码不同，可在用户中心进行安全设置
                  <nuxt-link to="/member/account-safe" v-if="!order.deposite.is_pwd">您还未设置支付密码</nuxt-link>
                  <nuxt-link to="/member/account-safe" v-else>忘记密码?</nuxt-link>
                </div>
              </div>
            </div>
          </div>
          <div class="cashier-tools-inside">
            <div class="cashier-tools-title">
              <h3>支付平台</h3>
            </div>
            <ul class="cashier-pay-list">
              <li v-for="payment in paymentList" :key="payment.plugin_id" :class="['payment-item', payment.selected && 'selected']">
                <img :src="payment.image" @click="initiatePay(payment, 'qr')">
              </li>
            </ul>
          </div>
        </div>
        <div v-show="showPayBox" class="cashier-pay-box">
          <div class="pay-item">
            <div class="pay-left">
              <p v-if="payment_plugin_id !== 'weixinPayPlugin'">使用电脑支付</p>
              <div v-if="payment_plugin_id === 'weixinPayPlugin'" class="pc-pay-img">
                <img src="../../assets/images/background-wechat.jpg">
              </div>
              <div v-else class="pc-pay-img"></div>
              <a class="pay-btn" href="javascript:;" @click="initiatePay(false, 'normal')">立即支付</a>
              <i v-if="payment_plugin_id === 'alipayDirectPlugin'" class="icon-or"></i>
            </div>
            <div v-if="payment_plugin_id === 'alipayDirectPlugin' || payment_plugin_id === 'weixinPayPlugin'" class="pay-right">
              <p v-if="payment_plugin_id === 'alipayDirectPlugin'">使用支付宝钱包扫一扫即可付款</p>
              <p v-if="payment_plugin_id === 'weixinPayPlugin'">使用微信钱包扫一扫即可付款</p>
              <div class="pay-qrcode" id="pay-qrcode">
                <iframe id="iframe-qrcode" width="200px" height="200px" scrolling="no"></iframe>
              </div>
            </div>
          </div>
        </div>
        <div class="same-pay-way bank-pay paybtn">
          <a @click="handlePay" href="javascript:;" v-if="!showPayBox && order.pay_type_text === 'ONLINE' && order.need_pay_price !== 0 && (order.pay_status !== 'PAID_OFF' && order.pay_status !== 'PAY_YES')">立即支付</a>
          <nuxt-link to="/member/my-order" v-if="order.pay_type_text === 'COD' || order.pay_status === 'PAID_OFF' || order.pay_status === 'PAY_YES' || order.need_pay_price === 0 ">查看订单</nuxt-link>
        </div>
      </div>
      <div class="existence" v-else>订单编号不存在</div>
    </div>
    <div class="cashier-box" v-if="price">
      <div class="cashier-box">
        <div class="cashier-change">
          <h2>充值金额:
            <span v-if="recharge">￥{{ recharge.recharge_money | unitPrice }}</span>
            <span v-else>加载中...</span>
            <i>元</i>
            <span class="pay-tip">充值状态刷新可能会延迟，如果您已付款成功，请勿重复支付！</span>
          </h2>
          <div class="cashier-tools">
            <div class="cashier-tools-inside">
              <div class="cashier-tools-title">
                <h3>支付平台</h3>
              </div>
              <ul class="cashier-pay-list">
                <li v-for="payment in paymentList" :key="payment.plugin_id" :class="['payment-item', payment.selected && 'selected']">
                  <img :src="payment.image" @click="initiatePay(payment, 'qr')">
                </li>
              </ul>
            </div>
          </div>
          <div v-show="showPayBox" class="cashier-pay-box">
            <div class="pay-item">
              <div class="pay-left">
                <p v-if="payment_plugin_id !== 'weixinPayPlugin'">使用电脑支付</p>
                <div v-if="payment_plugin_id === 'weixinPayPlugin'" class="pc-pay-img">
                  <img src="../../assets/images/background-wechat.jpg">
                </div>
                <div v-else class="pc-pay-img"></div>
                <a class="pay-btn" href="javascript:;" @click="initiatePay(false, 'normal')">立即支付</a>
                <i v-if="payment_plugin_id === 'alipayDirectPlugin'" class="icon-or"></i>
              </div>
              <div v-if="payment_plugin_id === 'alipayDirectPlugin' || payment_plugin_id === 'weixinPayPlugin'" class="pay-right">
                <p v-if="payment_plugin_id === 'alipayDirectPlugin'">使用支付宝钱包扫一扫即可付款</p>
                <p v-if="payment_plugin_id === 'weixinPayPlugin'">使用微信钱包扫一扫即可付款</p>
                <div class="pay-qrcode" id="pay-qrcode">
                  <iframe id="iframe-qrcode" width="200px" height="200px" scrolling="no"></iframe>
                </div>
              </div>
            </div>
          </div>
          <div class="same-pay-way bank-pay paybtn">
            <a @click="$message.error('请先选择支付方式！')" href="javascript:;" v-if="!showPayBox">立即支付</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import * as API_Trade from '@/api/trade'
  import * as API_Deposit from '@/api/deposit'
  import Vue from 'vue'
  import { Checkbox } from 'element-ui'
  Vue.use(Checkbox);
  export default {
    name: 'cashier',
    layout: 'full',
    middleware: 'auth-user',
    head() {
      return {
        title: `订单支付-${this.site.title}`
      }
    },
    data() {
      return {
        trade_sn: this.$route.query.trade_sn,
        order_sn: this.$route.query.order_sn,
        price: this.$route.query.price,
        // 支付方式列表
        paymentList: [],
        // 订单详情
        order: '',
        // 充值详情
        recharge: '',
        // 显示支付窗口
        showPayBox: false,
        // 支付方式
        payment_plugin_id: '',
        // 显示确认订单完成支付dialog
        showConfirmDialog: false,
        // 订单编号是否存在
        isExistence: true,
        //是否使用预存款支付
        isCheckPreDeposit: false,
        //支付密码
        password: '',
      }
    },
    async mounted() {
      if (!this.price) {
        await Promise.all([
          this.trade_sn
            ? API_Trade.getCashierData({ trade_sn: this.trade_sn })
            : API_Trade.getCashierData({ order_sn: this.order_sn }),
          API_Trade.getPaymentList()
        ]).then(responses => {
          this.order = responses[0]
          console.log(this.order)
          this.paymentList = responses[1].map(item => {
            item.selected = false
            return item
          })
        }).catch(error => {
          this.order = ''
          this.paymentList = []
          this.isExistence = false
          this.$message.error('订单编号不存在')
        })
      } else {
        await Promise.all([
          API_Deposit.getRecharge(this.price),
          API_Trade.getPaymentList()
        ]).then(response => {
          this.recharge = response[0]
          this.paymentList = response[1].map(item => {
            item.selected = false
            return item
          })
        }).catch(error => {
          console.log(error)
        })
      }
    },
    methods: {
      /** 检查微信支付状态 **/
      checkWeixinPayStatus(sn) {
        clearTimeout(weixinPayTimer)
        const weixinPayTimer = setInterval(async () => {
          let result = await API_Trade.getWeChatQrStatus(sn)
          if (result === 'SUCCESS') {
            if (this.recharge) {
              window.parent.location.href = "/member/account-balance"
            } else {
              window.parent.location.href = `/payment-complete?type=${this.trade_sn ? 'TRADE' : 'ORDER'}`
            }
          }
        }, 2000)
        this.$watch('paymentList', (list) => {
          list.forEach(item => item.plugin_id === 'alipayDirectPlugin' && clearTimeout(weixinPayTimer))
        })
        this.$on('hook:beforeDestroy', () => clearTimeout(weixinPayTimer))
      },
      /** 是否选择预存款支付 */
      handlePreDeposit() {
        if (this.isCheckPreDeposit) {
          this.showPayBox = false
          this.paymentList.forEach(item => {
            item.selected = false
            return item
          })
        }
      },
      /** 预存款支付 */
      handlePay() {
        const isSelectedPay = this.paymentList.every(item => item.selected)
        if(!this.isCheckPreDeposit && !isSelectedPay){
          this.$message.error('请先选择支付方式！')
        } else {
          if (!this.password){
            this.$message.error('请输入您的支付密码！')
            return false
          }
          const trade_type = this.trade_sn ? 'TRADE' : 'ORDER'
          const sn = this.trade_sn || this.order_sn
          const password = this.password
          const params = {
            sn,
            trade_type,
            password
          }
          API_Deposit.getBalancePay(trade_type,sn,params).then(response => {
            if(response.need_pay === 0) {
              this.$router.push('/payment-complete?type=' + trade_type)
            } else {
              location.reload()
            }
          })
        }
      },
      /** 发起支付 */
      initiatePay(payment, pay_mode) {
        this.showPayBox = true
        this.isCheckPreDeposit = false
        if (payment) {
          this.$set(this, 'paymentList', this.paymentList.map(item => {
            item.selected = item.plugin_id === payment.plugin_id
            return item
          }))
        } else {
          payment = this.paymentList.filter(item => item.selected)[0]
        }
        this.payment_plugin_id = payment.plugin_id
        const trade_type = this.trade_sn ? 'trade' : 'order'
        const sn = this.trade_sn || this.order_sn || this.recharge.recharge_sn
        const client_type = 'PC'
        const payment_plugin_id = payment.plugin_id
        if (!this.recharge) {
          // 如果是普通模式，就跳新窗口支付
          if (pay_mode === 'normal') {
            window.open(`./cashier-load-pay?trade_type=${trade_type}&sn=${sn}&payment_plugin_id=${payment_plugin_id}`)
            return false
          }
          // 如果是二维码模式，并且支付方式不是支付宝或微信，就跳新窗口支付
          if (pay_mode === 'qr' && (payment_plugin_id !== 'alipayDirectPlugin' && payment_plugin_id !== 'weixinPayPlugin')) {
            window.open(`./cashier-load-pay?trade_type=${trade_type}&sn=${sn}&payment_plugin_id=${payment_plugin_id}`)
            return false
          }
          this.$nextTick(() => {
            document.getElementById('pay-qrcode').innerHTML = `<iframe id="iframe-qrcode" width="200px" height="200px" scrolling="no"></iframe>`
            // 如果是二维码模式，并且支付方式是支付宝或者微信
            API_Trade.initiatePay(trade_type, sn, {
              client_type,
              pay_mode,
              payment_plugin_id
            }).then(response => {
              // 微信支付
              if (payment_plugin_id === 'weixinPayPlugin') {
                let weixinPayQrImage = `<img src="${response.gateway_url}" alt="weixinPayQrImage" />`
                document.getElementById('pay-qrcode').innerHTML = weixinPayQrImage
                this.checkWeixinPayStatus(response.bill_sn)
              } else {
                // 支付宝支付
                let $formItems = ''
                response.form_items && response.form_items.forEach(item => {
                  $formItems += `<input name="${item.item_name}" type="hidden" value='${item.item_value}' />`
                })
                let $form = `<form action="${response.gateway_url}" method="post">${$formItems}</form>`
                const qrIframe = document.getElementById('iframe-qrcode').contentWindow.document
                qrIframe.getElementsByTagName('body')[0].innerHTML = $form
                qrIframe.forms[0].submit()
              }
            })
          })
        } else {
          if (pay_mode === 'normal') {
            window.open(`./cashier-load-pay?sn=${sn}&payment_plugin_id=${payment_plugin_id}`)
            return false
          }
          if (pay_mode === 'qr' && (payment_plugin_id !== 'alipayDirectPlugin' && payment_plugin_id !== 'weixinPayPlugin')) {
            window.open(`./cashier-load-pay?sn=${sn}&payment_plugin_id=${payment_plugin_id}`)
            return false
          }
          // 如果是二维码模式，并且支付方式是支付宝或者微信
          this.$nextTick(() => {
            document.getElementById('pay-qrcode').innerHTML = `<iframe id="iframe-qrcode" width="200px" height="200px" scrolling="no"></iframe>`
            API_Deposit.getRechargeInitiatePay(sn, {
              client_type,
              pay_mode,
              payment_plugin_id
            }).then(response => {
              // 微信支付
              if (payment_plugin_id === 'weixinPayPlugin') {
                let weixinPayQrImage = `<img src="${response.gateway_url}" alt="weixinPayQrImage" />`
                document.getElementById('pay-qrcode').innerHTML = weixinPayQrImage
                this.checkWeixinPayStatus(response.bill_sn)
              } else {
                // 支付宝支付
                let $formItems = ''
                response.form_items && response.form_items.forEach(item => {
                  $formItems += `<input name="${item.item_name}" type="hidden" value='${item.item_value}' />`
                })
                let $form = `<form action="${response.gateway_url}" method="post">${$formItems}</form>`
                const qrIframe = document.getElementById('iframe-qrcode').contentWindow.document
                qrIframe.getElementsByTagName('body')[0].innerHTML = $form
                qrIframe.forms[0].submit()
              }
            })
          })
        }
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .cashier-box {
    width: 100%;
    background: #f5f5f5;
    padding: 20px 0;
  }
  .existence {
    width: 950px;
    margin: 0 auto;
    padding: 30px 50px;
    position: relative;
    box-shadow: 0 2px 5px #ccc;
    background: #fff;
    text-align: center;
    font-size: 20px;
  }
  .cashier-change {
    width: 950px;
    margin: 0 auto;
    padding: 30px 50px;
    position: relative;
    box-shadow: 0 2px 5px #ccc;
    background: #fff;
    h2 {
      width: 950px;
      height: 30px;
      line-height: 30px;
      font-size: 12px;
      font-weight: 200;
      b {
        font-weight: 200;
        font-size: 14px;
        color: #ff6700;
      }
      span {
        font-size: 20px;
        color: $color-main;
        margin: 0 5px 0 0;
        font-weight: 400;
      }
    }
    .pay-tip {
      font-size: 16px;
      color: $color-main;
      font-weight: 600;
      margin-left: 20px;
    }
    .cashier-order-detail {
      width: 950px;
      border: 1px solid #e1e1e1;
      background: #f4f4f4;
      margin: 20px 0 0 0;
    }
    .cashier-tools {
      width: 950px;
      border: 1px solid #e1e1e1;
      background: #f4f4f4;
      margin: 20px 0 20px 0;
      .pre-deposit {
        background: #fff;
        .payment {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 40px 10px 0;
          .pm-logo {
            width: 105px;
            height: 35px;
            background: url('../../assets/images/prepaid-logo.png') no-repeat 0 0;
          }
          .pm-notice {
            color: #4b5b78;
            i {
              display: inline-block;
              width: 12px;
              height: 12px;
              margin-right: 4px;
              vertical-align: middle;
              background: url('../../assets/images/pm_notoce_icon.png') no-repeat 0 0;
            }
          }
        }
        .pm-wrap {
          padding: 10px 35px;
          span {
            font-weight: 700;
          }
          /deep/ {
            .el-checkbox {
              margin-right: 0;
            }
            .el-checkbox__input.is-checked .el-checkbox__inner, .el-checkbox__input.is-focus .el-checkbox__inner{
              background-color: #fff;
              border-color: #DCDFE6;
            }
            .el-checkbox__inner {
              width: 12px;
              height: 12px;
            }
            .el-checkbox__inner::after {
              border-color: #000;
              left: 3px;
              height: 6px;
            }
            .el-checkbox__inner:hover {
              border-color: #DCDFE6;
            }
            .el-checkbox__label {
              color: #666;
              padding-left: 5px;
              font-size: 12px;
            }
          }
          .step-tit {
            padding: 10px 20px;
            color: #000;
            .pm-password {
              color: #777;
              background-color: #FFF;
              vertical-align: top;
              display: inline-block;
              width: 300px;
              min-height: 20px;
              padding: 5px;
              margin: 10px 0;
              border: solid 1px #E6E9EE;
            }
            .step-tip {
              color: #F59C1A;
              a {
                background-color: #F59C1A;
                border-color: #F59C1A;
                color: #FFF;
                padding: 4px 8px;
                border-radius: 5px;
                margin-left: 10px;
              }
            }
          }
        }
      }
      .cashier-tools-inside {
        margin: 3px;
        background: #fff;
      }
      .cashier-tools-title {
        height: 52px;
        line-height: 52px;
        h3 {
          width: 924px;
          height: 52px;
          line-height: 52px;
          font-weight: 200;
          font-size: 12px;
          background: #fcfcfc;
          padding-left: 20px;
        }
      }
      .cashier-pay-list {
        width: 844px;
        overflow: hidden;
        margin: 0 10px;
        padding: 10px 40px;
        .payment-item {
          float: left;
          line-height: 30px;
          margin: 0 8px 10px 0;
          padding: 5px 5px;
          position: relative;
          border: 1px solid #e0e0e0;
          cursor: pointer;
          img {
            width: 150px;
            height: 35px;
          }
          &.selected {
            border: 2px solid #f56a3e;
            padding: 4px;
          }
        }
      }
    }
    .cashier-order-inside {
      margin: 3px;
      background: #fff;
      min-height: 52px;
      overflow: hidden;
      h3 {
        display: flex;
        width: 944px;
        min-height: 52px;
        line-height: 52px;
        font-weight: 200;
        font-size: 12px;
        background: #fcfcfc;
        flex-wrap: wrap;
        i {
          width: 21px;
          height: 21px;
          display: block;
          background: url(../../assets/images/icons-cashier.png) no-repeat -70px 0;
          float: left;
          margin: 15px 10px 0 20px;
        }
        span { margin: 0 5px }
      }
    }
    .paybtn {
      a {
        font-family: Microsoft YaHei;
        width: 180px;
        height: 40px;
        line-height: 40px;
        text-align: center;
        color: #fff;
        font-size: 14px;
        background: $color-main;
        display: block;
        margin: 30px auto 0 auto;
      }
    }
  }
  .cashier-pay-box {
    width: 950px;
    border: 1px solid #e1e1e1;
    background: #f4f4f4;
    margin: 0 0 40px 0;
    padding-top: 3px;
    .pay-item {
      display: flex;
      justify-content: center;
      margin: 0 3px 3px 3px;
      background: #fff;
      overflow: hidden;
      height: 335px;
      .pay-left {
        width: 471px;
        float: left;
        height: 310px;
        position: relative;
        p {
          width: 450px;
          text-align: center;
          height: 30px;
          line-height: 30px;
          margin: 25px 0 0 0;
          font-size: 16px;
        }
        .pc-pay-img {
          height: 92px;
          margin-left: 150px;
          margin-top: 48px;
          width: 165px;
          background: url(../../assets/images/icons-cashier.png) no-repeat 0 -1417px;
        }
        .pay-btn {
          width: 180px;
          height: 40px;
          line-height: 40px;
          text-align: center;
          color: #fff;
          font-size: 14px;
          background: $color-main;
          display: block;
          margin: 30px auto 0 auto;
        }
        .icon-or {
          display: block;
          background: url(../../assets/images/icons-cashier.png) no-repeat -212px -1417px;
          height: 41px;
          left: 464px;
          position: absolute;
          top: 130px;
          width: 31px;
        }
      }
      .pay-right {
        float: left;
        border-left: 1px solid #f4f4f4;
        p {
          width: 472px;
          text-align: center;
          height: 30px;
          line-height: 30px;
          margin: 25px 0 0 0;
          font-size: 16px;
        }
        .pay-qrcode {
          margin: 20px auto;
          height: 200px;
          width: 200px;
          overflow: hidden;
        }
      }
    }
  }
  .see-order-btn {
    margin-left: 20px;
    color: $color-href;
    &:hover { color: $color-main }
  }
  .cashier-change .time-tip {
    font-size: 16px;
    font-weight: 400;
    span {
      color: $color-main;
    }
  }
</style>
