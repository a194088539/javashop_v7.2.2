<template>
  <div id="find-password">
    <nav-bar title="找回密码"></nav-bar>
    <!--验证账号-->
    <div v-show="step === 1" class="find-item">
      <van-cell-group :border="false">
        <van-field
          v-model="validAccountForm.account"
          clearable
          label="账户名"
          placeholder="请输入账户名"
        >
          <span slot="label">账&emsp;户&emsp;名</span>
        </van-field>
        <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="slider-valid" class="nc-container"></div>
        </div>
        <van-field
          v-else-if="validateInFO.validator_type === 'IMAGE'"
          v-model="validAccountForm.img_code"
          clearable
          label="图片验证码"
          placeholder="请输入图片验证码"
          maxlength="4"
        >
          <img v-if="valid_img_url" :src="valid_img_url" slot="button" @click="getValidImgUrl" class="captcha-img">
        </van-field>
        <div v-else></div>
      </van-cell-group>
      <div class="big-btn">
        <van-button size="large" :disabled="val_disabled_account" @click="handleValidAccount">验证账户</van-button>
      </div>
    </div>
    <!--验证手机号-->
    <div v-show="step === 2" class="find-item">
      <van-cell-group :border="false">
        <van-field
          v-model="validMobileForm.mobile"
          clearable
          readonly
        >
          <span slot="label">手&emsp;机&emsp;号</span>
        </van-field>
        <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="step-slider-valid" class="nc-container"></div>
        </div>
        <van-field
          v-else-if="validateInFO.validator_type === 'IMAGE'"
          v-model="validMobileForm.img_code"
          clearable
          label="图片验证码"
          placeholder="请输入图片验证码"
          maxlength="4"
        >
          <img v-show="valid_img_url" :src="valid_img_url" slot="button" @click="getValidImgUrl" class="captcha-img">
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
        <van-button size="large" :disabled="val_disabled_mobile" @click="handleNextStep">下一步</van-button>
      </div>
    </div>
    <!--更新密码-->
    <div v-show="step === 3">
      <van-notice-bar>
        请务必牢记您的新密码！为了您的账户安全，请定期更换密码！
      </van-notice-bar>
      <div class="find-item">
        <van-cell-group :border="false">
          <van-field
            v-model="changePasswordForm.password"
            type="password"
            autocompete="off"
            clearable
            placeholder="请输入密码"
            maxlength="20"
            :error-message="passwordError"
          >
            <span slot="label">新&emsp;密&emsp;码</span>
          </van-field>
          <van-field
            v-model="changePasswordForm.rep_password"
            type="password"
            autocompete="off"
            clearable
            label="确认新密码"
            placeholder="请确认密码"
            maxlength="20"
            :error-message="repPasswordError"
          />
        </van-cell-group>
        <div class="big-btn">
          <van-button size="large" :disabled="val_disabled_password" @click="submitChangeForm">确认修改</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Vue from 'vue'
  import * as API_Passport from '@/api/passport'
  import * as API_Common from '@/api/common'
  import { RegExp } from '@/ui-utils'
  import Storage from '@/utils/storage'
  import AliyunAfs from '@/components/AliyunAfs'

  export default {
    name: 'find-password',
    layout: 'full',
    head() {
      return {
        title: `找回密码-${this.site.title}`
      }
    },
    data() {
      return {
        // uuid
        uuid: Storage.getItem('uuid'),
        // 步骤
        step: 1,
        // 校验账户信息 表单
        validAccountForm: {
          scene: 'FIND_PASSWORD'
        },
        // 校验手机 表单
        validMobileForm: {
          scene: 'FIND_PASSWORD'
        },
        // 图片验证码URL
        valid_img_url: '',
        // 修改密码 表单
        changePasswordForm: {
          password: '',
          rep_password: ''
        },
        /** 验证信息 **/
        validateInFO: {},
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
      /** 验证账户按钮 按钮是否禁用 */
      val_disabled_account() {
        const { account, img_code } = this.validAccountForm
        if(this.afs) {
          return !(account && this.accountAfsSuccess)
        } else {
          return !(account && img_code)
        }
      },
      /** 验证手机号 按钮是否禁用 */
      val_disabled_mobile() {
        const { img_code, sms_code } = this.validMobileForm
        if(this.afs) {
          return !(this.accountAfsSuccess && sms_code)
        } else {
          return !(img_code && sms_code)
        }
      },
      /** 确认修改密码 按钮是否禁用 */
      val_disabled_password() {
        const { password, rep_password } = this.changePasswordForm
        return (!password || !rep_password) || (password !== rep_password)
      },
      // 密码 错误信息
      passwordError() {
        const { password } = this.changePasswordForm
        if (!password) return ''
        if (RegExp.password.test(password)) return ''
        return '密码应为6-20位英文或数字！'
      },
      // 确认密码 错误信息
      repPasswordError() {
        const { password, rep_password } = this.changePasswordForm
        if (!password || !rep_password) return ''
        if (password !== rep_password) return '两次输入不一致！'
        return ''
      },
    },
    mounted() {
      this.$nextTick(this.getValidImgUrl)
      /** 获取验证方式 **/
      API_Common.getValidator().then(res => {
        this.validateInFO = res
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
      /** 获取图片验证码URL */
      getValidImgUrl() {
        const uuid = this.step === 1 ? this.uuid : this.validMobileForm.uuid
        this.valid_img_url = API_Common.getValidateCodeUrl(uuid, 'FIND_PASSWORD')
      },
      /** 验证账户 */
      handleValidAccount() {
        const params = JSON.parse(JSON.stringify(this.validAccountForm))
        params.uuid = this.uuid
        if(this.afs) {
          params.c_sessionid = this.afsParam.c_sessionid
          params.sig = this.afsParam.sig
          params.nc_token = this.afsParam.nc_token
          delete params.scene
          API_Passport.validAccount(params).then((response) => {
            this.validMobileForm.mobile = response.mobile
            this.validMobileForm.uname = response.uname
            this.validMobileForm.uuid = response.uuid
            delete params.scene
            this.step = 2
            this.accountAfsSuccess = false
            API_Common.getValidator().then(res => {
              this.validateInFO = res
              const { validator_type, aliyun_afs } = res
              if (validator_type === 'ALIYUN') {
                this.afs = new AliyunAfs('#step-slider-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
                this.afs.init()
              } else {
                this.accountAfsSuccess = true
              }
            })
          })
        } else {
          params.captcha = params.img_code
          API_Passport.validAccount(params).then((response) => {
            this.validMobileForm.mobile = response.mobile
            this.validMobileForm.uname = response.uname
            this.validMobileForm.uuid = response.uuid
            this.step = 2
            this.getValidImgUrl()
          })
        }
      },
      /** 发送手机验证码异步方法 */
      sendValidMobileSms() {
        return new Promise((resolve, reject) => {
          // const { uuid, img_code, scene } = this.validMobileForm
          const params =JSON.parse(JSON.stringify(this.validMobileForm))
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            API_Passport.sendFindPasswordSms(params).then(() => {
              this.$message.success('发送成功，请注意查收！')
              resolve()
            }).catch(() => {
              this.afs.reload()
              this.accountAfsSuccess = false
              reject()
            })
          } else {
            params.captcha = params.img_code
            if (!params.img_code) {
              this.$message.error('请输入图片验证码！')
              reject()
            } else {
              API_Passport.sendFindPasswordSms(params).then(() => {
                this.$message.success('发送成功，请注意查收！')
                resolve()
              }).catch(reject)
            }
          }
        })
      },
      /** 下一步 */
      handleNextStep() {
        const { uuid, sms_code } = this.validMobileForm
        API_Passport.validFindPasswordSms(uuid, sms_code).then(() => {
          this.step = 3
          this.getValidImgUrl()
        }).catch(() => {
          if(this.afs) {
            this.afs.reload()
            this.accountAfsSuccess = false
          } else {
            this.getValidImgUrl()
          }
        })
      },
      /** 找回密码 */
      submitChangeForm() {
        const { uuid } = this.validMobileForm
        const { password } = this.changePasswordForm
        API_Passport.changePassword(uuid, password).then(() => {
          this.$message.success('密码找回成功，请牢记您的新密码！')
          setTimeout(() => {
            this.$router.push(`/login${this.MixinForward}`)
          }, 200)
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  /deep/ .van-field__control {
    font-size: 14px !important;
  }
  .big-btn {
    padding: 10px 15px;
    .van-button {
      color: #fff;
      background-color: $color-main;
      &.van-button--disabled {
        color: #999;
        background-color: #e8e8e8;
        border: 1px solid #e5e5e5;
      }
    }
  }
  .captcha-img {
    width: 70px;
    height: 24px;
  }
</style>
