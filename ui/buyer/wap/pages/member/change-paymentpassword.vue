<template>
  <div id="change-paymentpassword">
    <nav-bar title="支付密码"/>
    <div class="change-paymentpassword-container"></div>
    <div v-show="step === 1" class="valid-mobile">
      <van-cell-group :border="false">
        <van-field
          v-model="validMobileForm.mobile"
          clearable
          disabled
          placeholder="请输入手机号"
          maxlength="11"
        >
          <span slot="label">已验证手机</span>
        </van-field>
        <div style="padding: 10px 15px" v-if="validateType.validator_type === 'ALIYUN'">
          <div id="slider-valid" class="nc-container" style="padding-top: 0;"></div>
        </div>
        <van-field
          v-else-if="validateType.validator_type === 'IMAGE'"
          v-model="validMobileForm.captcha"
          clearable
          label="图片验证码"
          placeholder="请输入图片验证码"
          maxlength="4"
        >
          <img v-if="valid_img_url" :src="valid_img_url" slot="button" @click="getValidImgUrl" class="captcha-img">
        </van-field>
        <div v-else></div>
        <van-field
          v-if="accountAfsSuccess"
          v-model="validMobileForm.sms_code"
          center
          clearable
          label="短信验证码"
          placeholder="请输入短信验证码"
          maxlength="6"
        >
          <en-count-down-btn slot="button" :start="sendValidMobileSms">发送验证码</en-count-down-btn>
        </van-field>
      </van-cell-group>
      <div class="big-btn">
        <van-button size="large" :disabled="val_disabled_mobile" @click="submitValMobileForm">提交校验</van-button>
      </div>
    </div>
    <div v-show="step === 2" class="change-password">
      <van-cell-group :border="false">
        <van-field
          v-model="changePayMentPasswordForm.password"
          type="password"
          autocompete="off"
          clearable
          placeholder="支付密码只能为6位数字"
          maxlength="6"
        ><span slot="label">新&emsp;密&emsp;码</span></van-field>
        <van-field
          v-model="changePayMentPasswordForm.rep_password"
          type="password"
          autocompete="off"
          clearable
          placeholder="请确认密码"
          maxlength="6"
        ><span slot="label">重&nbsp;复&nbsp;密&nbsp;码</span></van-field>
      </van-cell-group>
      <div class="big-btn">
        <van-button size="large" :disabled="val_disabled_password" @click="submitChangePaymentForm" >修改密码</van-button>
      </div>
    </div>
  </div>
</template>

<script>
  import * as API_Common from '@/api/common'
  import * as API_Safe from '@/api/safe'
  import * as API_Deposit from '@/api/deposit'
  import AliyunAfs from '@/components/AliyunAfs'
  import md5 from 'js-md5'

  export default {
    name: 'change-paymentpassword',
    head() {
      return {
        title: `支付密码-${this.site.title}`
      }
    },
    data() {
      return {
        /** 步骤 */
        step: 1,
        /** 验证手机 表单 */
        validMobileForm: {
          scene: 'SET_PAY_PWD',
          mobile: '',
          uuid: ''
        },
        /** 图片验证码URL */
        valid_img_url: '',
        /** 修改密码 表单 */
        changePayMentPasswordForm: {
          password: '',
          rep_password: ''
        },
        /** 验证方式 阿里云 或 图片验证码 **/
        validateType: {},
        /** 阿里云验证条实例 **/
        afs: null,
        /** 当滑动验证成功之后返回的参数 **/
        afsParam: {
          c_sessionid: '',
          sig: '',
          nc_token: ''
        },
        /** 阿里云滑动验证是否通过 && 如果不是 阿里云验证则直接设置为 true **/
        accountAfsSuccess: false
      }
    },
    computed: {
      /** 校验手机号 按钮是否禁用 */
      val_disabled_mobile() {
        const { captcha, sms_code } = this.validMobileForm
        if(this.afs) {
          return !(this.accountAfsSuccess && sms_code)
        } else {
          return !(captcha && sms_code)
        }
      },
      /** 修改密码 按钮是否禁用 */
      val_disabled_password() {
        const { password, rep_password } = this.changePayMentPasswordForm
        return !(password && rep_password)
      }
    },
    mounted() {
      this.GET_MemberAccount()
      /** 获取验证方式**/
      API_Common.getValidator().then(res => {
        this.validateType = res
        const { validator_type, aliyun_afs } = res
        if (validator_type === 'ALIYUN') {
          this.afs = new AliyunAfs('#slider-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
          this.afs.init()
        } else {
          this.accountAfsSuccess = true
        }
      })
    },
    methods: {
      /** 阿里云滑动验证 通过后的回调函数 **/
      initCaptcha(data) {
        this.afsParam.c_sessionid = data.csessionid;
        this.afsParam.sig = data.sig;
        this.afsParam.nc_token = data.nc_token;
        this.accountAfsSuccess = true
      },
      /** 获取账户信息 **/
      GET_MemberAccount() {
        API_Deposit.getMemberAccount().then(response =>{
          this.validMobileForm.mobile = response.mobile
          this.validMobileForm.uuid = response.uuid
          this.getValidImgUrl()
        })
      },
      /** 获取图片验证码URL */
      getValidImgUrl() {
        this.valid_img_url = API_Common.getValidateCodeUrl(this.validMobileForm.uuid, 'SET_PAY_PWD')
      },
      /** 发送验证手机验证码 */
      sendValidMobileSms() {
        return new Promise((resolve, reject) => {
          if(this.afs) {
            const params = {
              uuid: this.validMobileForm.uuid,
              c_sessionid: this.afsParam.c_sessionid,
              sig: this.afsParam.sig,
              nc_token: this.afsParam.nc_token
            }
            API_Safe.sendMobileSmsForPayment(params).then(() => {
              this.$message.success('验证码发送成功，请注意查收！')
              resolve()
            }).catch(() => {
              this.afs.reload()
              this.accountAfsSuccess = false
              reject()
            })
          } else {
            const params = {
              scene: this.validMobileForm.scene,
              uuid: this.validMobileForm.uuid,
              captcha: this.validMobileForm.captcha
            }
            if (!params.captcha) {
              return this.$message.error('请填写图片验证码！')
            }
            API_Safe.sendMobileSmsForPayment(params).then(() => {
              this.$message.success('验证码发送成功，请注意查收！')
              resolve()
            }).catch(reject)
          }
        })
      },
      /** 手机验证 */
      submitValMobileForm() {
        const { sms_code } = this.validMobileForm
        if (!sms_code) {
          this.$message.error('请填写短信验证码！')
          return false
        }
        if (this.afs) {
          API_Safe.validChangePaymentPasswordSms(sms_code).then(() => {
            this.step = 2
          }).catch(() => {
            this.afs.reload()
            this.accountAfsSuccess = false
          })
        } else {
          API_Safe.validChangePaymentPasswordSms(sms_code).then(() => {
            this.step = 2
          }).catch(this.getValidImgUrl)
        }
      },
      /** 修改密码 */
      submitChangePaymentForm() {
        const { password, rep_password } = this.changePayMentPasswordForm
        const { uuid } = this.validMobileForm
        const params = {
          uuid,
          password: md5(password)
        }
        if (!/^\d{6}$/g.test(password)) {
          this.$message.error('密码格式不正确！')
          return false
        }
        if (password !== rep_password) {
          this.$message.error('两次密码输入不一致！')
          return false
        }

        API_Safe.bindPaymentPassword(params).then(() => {
          this.$message.success('密码修改成功！')
          setTimeout(() => {
            this.$router.replace('/member/account-safe')
          }, 200)
        }).catch(this.getValidImgUrl)
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .change-paymentpassword-container {
    padding-top: 46px;
  }
  /deep/ .van-field__control {
    font-size: 14px;
  }
</style>
