<template>
  <div id="change-password">
    <nav-bar title="修改密码"/>
    <div class="change-password-container"></div>
    <div v-show="step === 1" class="valid-mobile">
      <van-cell-group :border="false">
        <van-field
          v-model="bindMobile"
          clearable
          placeholder="请输入手机号"
          maxlength="11"
        >
          <span slot="label">已验证手机</span>
        </van-field>
        <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="slider-valid" class="nc-container" style="padding-top: 0;"></div>
        </div>
        <van-field
          v-else-if="validateInFO.validator_type === 'IMAGE'"
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
          v-model="changePasswordForm.password"
          type="password"
          autocompete="off"
          clearable
          placeholder="6-20位英文或数字"
          maxlength="20"
        ><span slot="label">新&emsp;密&emsp;码</span></van-field>
        <van-field
          v-model="changePasswordForm.rep_password"
          type="password"
          autocompete="off"
          clearable
          placeholder="请确认密码"
          maxlength="20"
        ><span slot="label">重&nbsp;复&nbsp;密&nbsp;码</span></van-field>
        <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="step-slider-valid" class="nc-container" style="padding-top: 0;"></div>
        </div>
        <van-field
          v-else-if="validateInFO.validator_type === 'IMAGE'"
          v-model="changePasswordForm.captcha"
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
        <van-button size="large" :disabled="val_disabled_password" @click="submitChangeForm" >修改密码</van-button>
      </div>
    </div>
  </div>
</template>

<script>
  import * as API_Common from '@/api/common'
  import * as API_Safe from '@/api/safe'
  import { Foundation, RegExp } from '@/ui-utils'
  import Storage from '@/utils/storage'
  import AliyunAfs from '@/components/AliyunAfs'
  import md5 from 'js-md5'
  export default {
    name: 'change-password',
    head() {
      return {
        title: `修改密码-${this.site.title}`
      }
    },
    data() {
      return {
        uuid: Storage.getItem('uuid'),
        /** 步骤 */
        step: 1,
        /** 验证手机 表单 */
        validMobileForm: {
          scene: 'VALIDATE_MOBILE'
        },
        /** 图片验证码URL */
        valid_img_url: '',
        /** 修改密码 表单 */
        changePasswordForm: {
          scene: 'MODIFY_PASSWORD',
          password: '',
          rep_password: '',
          captcha: ''
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
    mounted() {
      this.$nextTick(this.getValidImgUrl)
      /** 获取验证方式**/
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
    computed: {
      bindMobile() {
        return Foundation.secrecyMobile(this.$store.getters.user.mobile)
      },
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
        const { password, rep_password, captcha } = this.changePasswordForm
        if(this.afs) {
          return !(password && rep_password, this.accountAfsSuccess)
        } else {
          return !(password && rep_password, captcha)
        }
      }
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
        this.valid_img_url = API_Common.getValidateCodeUrl(this.uuid, this.step === 1 ? 'VALIDATE_MOBILE' : 'MODIFY_PASSWORD')
      },
      /** 发送验证手机验证码 */
      sendValidMobileSms() {
        return new Promise((resolve, reject) => {
          const params = JSON.parse(JSON.stringify(this.validMobileForm))
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            delete params.sms_code
            API_Safe.sendMobileSms(params).then(() => {
              this.$message.success('验证码发送成功，请注意查收！')
              resolve()
            }).catch(() => {
              this.afs.reload()
              this.accountAfsSuccess = false
              reject()
            })
          } else {
            params.uuid = this.uuid
            delete params.sms_code
            if (!params.captcha) {
              this.$message.error('请填写图片验证码！')
              return false
            }
            API_Safe.sendMobileSms(params).then(() => {
              this.$message.success('验证码发送成功，请注意查收！')
              resolve()
            }).catch(reject)
          }
        }).catch((e) => {})
      },
      /** 手机验证 */
      submitValMobileForm() {
        const { sms_code } = this.validMobileForm
        if (!sms_code) {
          this.$message.error('请填写短信验证码！')
          return false
        }
        if (this.afs) {
          API_Safe.validChangePasswordSms(sms_code).then(() => {
            this.step = 2
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
          }).catch(() => {
            this.afs.reload()
            this.accountAfsSuccess = false
          })
        } else {
          API_Safe.validChangePasswordSms(sms_code).then(() => {
            this.step = 2
            this.getValidImgUrl()
          }).catch(() => {
            this.getValidImgUrl()
          })
        }
      },
      /** 修改密码 */
      submitChangeForm() {
        const { uuid } = this
        const params = JSON.parse(JSON.stringify(this.changePasswordForm))
        if (!RegExp.password.test(params.password)) {
          this.$message.error('密码格式不正确！')
          return false
        }
        if (params.password !== params.rep_password) {
          this.$message.error('两次密码输入不一致！')
          return false
        }
        if(this.afs) {
          params.c_sessionid = this.afsParam.c_sessionid
          params.sig = this.afsParam.sig
          params.nc_token = this.afsParam.nc_token
          delete params.scene
          delete params.captcha
          params.password = md5(params.password)
          API_Safe.changePassword(params).then(() => {
            this.$message.success('密码修改成功，请重新登录！')
            setTimeout(async () => {
              await this.$store.dispatch('user/logoutAction', 'change-pwd')
              this.$router.push('/login')
            }, 200)
          }).catch(() => {
            this.afs.reload()
            this.accountAfsSuccess = false
          })
        } else {
          if (!params.captcha) {
            this.$message.error('请填写图片验证码！')
            return false
          }
          params.uuid = this.uuid
          params.password = md5(params.password)
          API_Safe.changePassword(params).then(() => {
            this.$message.success('密码修改成功，请重新登录！')
            setTimeout(() => {
              this.$store.dispatch('user/logoutAction')
              this.$router.replace({ name: 'login' })
            }, 500)
          })
        }
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  /deep/ .van-field__control {
    font-size: 14px;
  }
  .change-password-container {
    padding-top: 46px;
  }
</style>
