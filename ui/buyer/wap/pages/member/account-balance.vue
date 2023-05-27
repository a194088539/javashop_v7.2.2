<template>
  <div id="account-balance" style="background: #f7f7f7;">
    <nav-bar
      title="账户余额"
      right-text="明细"
      @click-right="$router.push('/member/balance-detailed')"/>
    <!-- 余额显示 -->
    <div class="account-container">
      <div class="account-balance">
        <h2>¥ {{account_balance | unitPrice}}</h2>
        <p>仅限用于本商城购买商品使用，不能提现</p>
      </div>
      <!-- 充值表单 -->
      <div class="account-from">
        <div class="account-recharge">
          <p class="account-recharge-title">充余额</p>
          <div class="account-recharge-box">
            <label>金额：</label>
            <div class="input-group">
              <input v-model="rechargeNumber" type="text" placeholder="请输入充值金额" class="input-inner">
              <div class="input-append">元</div>
            </div>
          </div>
        </div>
        <!-- 支付方式选择 -->
        <div class="payment-way" v-if="showPayMentWay && !MixinIsWeChatBrowser()">
          <van-radio-group v-model="paymentType">
            <van-cell-group>
              <van-cell clickable @click="cellClickMethod('alipayDirectPlugin')" v-if="!MixinIsWeChatBrowser()">
                <img class="alipay-image" slot="title" src="@/assets/images/icon-alipay.png" alt="">
                <van-radio #right-icon name="alipayDirectPlugin" />
              </van-cell>
              <van-cell clickable @click="cellClickMethod('weixinPayPlugin')" v-if="MixinIsWeChatBrowser()">
                <img class="weixinpay-image" slot="title" src="@/assets/images/icon-wechat.png" alt="">
                <van-radio #right-icon name="weixinPayPlugin" />
              </van-cell>
            </van-cell-group>
          </van-radio-group>
        </div>
        <!-- 提交按钮 -->
        <div class="submit-btn">
          <van-button block @click="submitPayment">立即支付</van-button>
        </div>
        <div class="__pay_form__"></div>
      </div>
    </div>
  </div>
</template>

<script>
  import * as API_deposit from '@/api/deposit'
  export default {
    name: 'account-balance',
    data() {
      return {
        showPayMentWay: false,
        // 预存款余额
        account_balance: '',
        // 充值金额
        rechargeNumber: '',
        // 支付方式
        paymentType: '',
        // 是否是微信浏览器
        isWechatBroswer: false
      }
    },
    watch: {
      rechargeNumber (value) {
        const dotIndex = value.indexOf('.');
        if (dotIndex > -1) {
          value = value.slice(0, dotIndex + 1) + value.slice(dotIndex).replace(/\./g, '').slice(0, 2);
        }
        this.rechargeNumber = value.replace(/[^0-9.]/g, '');
      }
    },
    mounted() {
      this.$nextTick(_ => {
        this.showPayMentWay = true
        this.isWechatBroswer = this.MixinIsWeChatBrowser()
        this.isWechatBroswer && (this.paymentType = 'weixinPayPlugin')
      })
      this.getDepositBalance()
    },
    methods: {
      /** 点击切换支付方式 **/
      cellClickMethod(val) {
        if (val === this.paymentType) {
          this.paymentType = ''
          return
        }
        this.paymentType = val
      },
      /** 立即支付 **/
      submitPayment() {
        if (this.rechargeNumber < 1) {
          return this.$message.error('充值金额最小为1元')
        } else if (!this.paymentType) {
          return this.$message.error('请选择支付方式')
        }
        API_deposit.createRechargeOrder(this.rechargeNumber).then(response => {
          const params = {
            payment_plugin_id: this.paymentType,
            pay_mode: 'normal',
            client_type: 'WAP'
          }
          API_deposit.paymentRechargeOrder(params, response.recharge_sn).then(result => {
            // 微信支付
            if (this.paymentType === 'weixinPayPlugin') {
              if (!this.isWechatBroswer) {
                return this.$message.error('请在微信中支付！')
              } else {
                const params = {}
                if (result.form_items) {
                  result.form_items.forEach(item => {
                    params[item.item_name] = item.item_value
                  })
                } else {
                  Object.keys(result).forEach(key => {
                    if (key !== 'gateway_url') {
                      params[key] = result[key]
                    }
                  })
                }
                WeixinJSBridge.invoke('getBrandWCPayRequest', params, this.payCallbackDialog)
              }
            } else {
              // 支付宝支付
              let $formItems = ''
              result.form_items.forEach(item => {
                $formItems += `<input name="${item.item_name}" type="hidden" value='${item.item_value}' />`
              })
              let $form = `<form action="${result.gateway_url}" method="post">${$formItems}</form>`
              document.getElementsByClassName('__pay_form__')[0].innerHTML = $form
              document.forms[0].submit()
            }
          })
        })
      },
      payCallbackDialog() {
        this.$dialog.confirm({
          title: '提示',
          message: '请确认支付是否完成！',
          confirmButtonText: '支付成功',
          cancelButtonText: '重新支付'
        }).then(() => {
          this.rechargeNumber = ''
          this.getDepositBalance()
          window.scrollTop = 0;
        }).catch(() => {})
      },
      /** 获取预存款余额 **/
      getDepositBalance() {
        API_deposit.getDepositBalance().then(response => {
          this.account_balance = response
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .account-container {
    padding: 46px 0;
    .account-balance {
      background: #fff;
      margin-bottom: 20px;
      h2 {
        font-size: 24px;
        line-height: 250%;
        text-align: center;
      }
      p {
        text-align: center;
        padding-bottom: 40px;
        color: #999;
      }
    }
    .account-from {
      background: #fff;
    }
    .account-recharge {
      .account-recharge-title {
        padding: 20px 16px 0px;
        color: #333;
        font-size: 14px;
      }

      .account-recharge-box {
        padding: 0 15px;
        display: flex;
        align-items: center;
        height: 60px;

        label {
          width: 80px;
          font-size: 14px;
          color: #333;
          text-align: right;
        }

        .input-group {
          display: inline-table;
          width: 200px;
          border-collapse: separate;
          position: relative;
          font-size: 14px;
          .input-inner {
            display: table-cell;
            color: #606266;
            border-radius: 4px;
            border: 1px solid #dcdfe6;
            box-sizing: border-box;
            height: 40px;
            line-height: 1;
            padding: 0 15px;
            transition: border-color .2s cubic-bezier(.645,.045,.355,1);
            width: 100%;
            vertical-align: middle;
            font-size: 14px;
            border-top-right-radius: 0;
            border-bottom-right-radius: 0;
            &:focus {
              border-color: #409eff;
            }
          }
          .input-append {
            display: table-cell;
            position: relative;
            background-color: #f5f7fa;
            color: #909399;
            vertical-align: middle;
            border: 1px solid #dcdfe6;
            border-radius: 4px;
            padding: 0 20px;
            width: 1px;
            white-space: nowrap;
            border-left: 0;
            border-top-left-radius: 0;
            border-bottom-left-radius: 0;
          }
        }

      }
    }

    .payment-way {
      margin-top: 20px;
      /deep/ .van-cell__title {
        height: 35px;
      }
      /deep/ .van-cell__value {
        display: flex;
        justify-content: flex-end;
        align-items: center;
      }
      .alipay-image {
        width: 150px;
        margin-left: -27px;
      }
      .weixinpay-image {
        width: 150px;
        margin-left: -20px;
      }
    }

    .submit-btn {
      margin: 20px 15px 0;
      button {
        background: #f44;
        color: #fff;
        border: none;
        border-radius: 0;
      }
    }
  }
</style>
