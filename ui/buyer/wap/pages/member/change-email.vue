<template>
  <div id="change-email">
    <nav-bar title="修改电子邮箱"/>
    <div v-if="step !== 3" class="change-mobile-container">
      <div v-show="step === 1" class="valid-mobile">
        <van-cell-group :border="false">
          <van-field v-model="bindMobile" readonly>
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
          <van-button size="large" :disabled="val_disabled_mobile" @click="submitValMobileForm">提交校验</van-button>
        </div>
      </div>
      <div v-show="step === 2" class="change-email">
        <van-cell-group :border="false">
          <van-field
            v-model="changeEmailForm.email"
            clearable
            label="电子邮箱"
            placeholder="请输入新的电子邮箱"
            maxlength="100"
          />
          <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
            <div id="step-slider-valid" class="nc-container" style="padding-top: 0;"></div>
          </div>
          <van-field
            v-else-if="validateInFO.validator_type === 'IMAGE'"
            v-model="changeEmailForm.captcha"
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
            v-model="changeEmailForm.email_code"
            center
            clearable
            label="邮箱验证码"
            placeholder="请输入邮箱验证码"
            maxlength="6"
          >
            <en-count-down-btn slot="button" :start="sendChangeEmailCode">发送验证码</en-count-down-btn>
          </van-field>
        </van-cell-group>
        <div class="big-btn">
          <van-button size="large" :disabled="val_disabled_change" @click="submitChangeForm">确认修改</van-button>
        </div>
      </div>
    </div>
    <div v-else class="change-success">
      <div class="inner-success">
        <img src="../../assets/images/icon-success.png" class="icon-success">
        <div class="success-title">
          您的电子邮箱已成功更换为：<p class="success-mobile">{{ changeEmailForm.email }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex'
  import * as API_Common from '@/api/common'
  import * as API_Safe from '@/api/safe'
  import { Foundation, RegExp } from '@/ui-utils'
  import Storage from '@/utils/storage'
  import AliyunAfs from '@/components/AliyunAfs'
  export default {
    name: 'change-email',
    head() {
      return {
        title: `修改电子邮箱-${this.site.title}`
      }
    },
    data() {
      return {
        uuid: Storage.getItem('uuid'),
        /** 步骤 */
        step: 1,
        /** 校验手机号 表单 */
        validMobileForm: {
          scene: 'VALIDATE_MOBILE',
          captcha: '',
          sms_code: ''
        },
        /** 图片验证码URL */
        valid_img_url: '',
        /** 更换电子邮箱 表单 */
        changeEmailForm: {
          scene: 'BIND_EMAIL',
          email: '',
          captcha: '',
          email_code: ''
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
      ...mapGetters(['user']),
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
      /** 更换电子邮箱 按钮是否禁用 */
      val_disabled_change() {
        const { email, captcha, email_code } = this.changeEmailForm
        if(this.afs) {
          return !(email && this.accountAfsSuccess && email_code)
        } else {
          return !(email && captcha && email_code)
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
        this.valid_img_url = API_Common.getValidateCodeUrl(this.uuid, this.step === 1 ? 'VALIDATE_MOBILE' : 'BIND_EMAIL')
      },
      /** 发送手机验证码 */
      sendValidMobileSms() {
        return new Promise((resolve, reject) => {
          const params = JSON.parse(JSON.stringify(this.validMobileForm))
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            API_Safe.sendMobileSms(params).then(() => {
              this.$message.success('发送成功，请注意查收！')
              resolve()
            }).catch(() => {
              this.afs.reload()
              this.accountAfsSuccess = false
              reject()
            })
          } else {
            if (!params.captcha) {
              this.$message.error('请填写图片验证码！')
              return false
            }
            params.uuid = this.uuid
            API_Safe.sendMobileSms(params).then(() => {
              this.$message.success('发送成功，请注意查收！')
              resolve()
            }).catch(reject)
          }
      })
      },
      /** 校验更换手机号验证码 */
      submitValMobileForm() {
        const { sms_code } = this.validMobileForm
        if(this.afs) {
          API_Safe.validChangeMobileSms(sms_code).then(() => {
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
          }).catch(() => {
            this.afs.reload()
            this.accountAfsSuccess = false
          })
        } else {
          API_Safe.validChangeMobileSms(sms_code).then(() => {
            this.step = 2
            this.getValidImgUrl()
          }).catch(() => {
            this.getValidImgUrl()
          })
        }
      },
      /** 修改电子邮箱 发送验证码 */
      sendChangeEmailCode() {
        return new Promise((resolve, reject) => {
          const params = JSON.parse(JSON.stringify(this.changeEmailForm))
          if (!RegExp.email.test(params.email)) {
          this.$message.error('电子邮箱格式有误！')
          return false
        }
        if(this.afs) {
          params.c_sessionid = this.afsParam.c_sessionid
          params.sig = this.afsParam.sig
          params.nc_token = this.afsParam.nc_token
          delete params.scene
          API_Safe.sendBindEmailCode(params).then(() => {
            this.$message.success('验证码发送成功，请注意查收！')
            resolve()
          }).catch(() => {
            this.afs.reload()
            this.accountAfsSuccess = false
            reject()
          })
        } else {
          if (!params.captcha) {
            this.$message.error('请填写图片验证码！')
            return false
          }
          params.uuid = this.uuid
          API_Safe.sendBindEmailCode(params).then(() => {
            this.$message.success('验证码发送成功，请注意查收！')
            resolve()
          }).catch(reject)
        }
      })
      },
      /** 确认修改 */
      submitChangeForm() {
        const { email, email_code } = this.changeEmailForm
        if (!RegExp.email.test(email)) {
          this.$message.error('电子邮箱格式有误！')
          return false
        }
        API_Safe.bindEmail(email, email_code).then(() => {
          this.$message.success('更换成功！')
          this.$store.dispatch('user/getUserDataAction')
          this.step = 3
        }).catch(() => {
          if (this.afs) {
            this.afs.reload()
            this.accountAfsSuccess = false
          } else {
            this.getValidImgUrl()
          }
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  /deep/ .van-field__control {
    font-size: 14px;
  }
  .captcha-img {
    width: 70px;
    height: 24px;
  }
  .change-mobile-container {
    padding-top: 46px;
  }
  .change-success {
    padding-top: 46px;
    width: 100%;
    min-height: 300px;
    .inner-success {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-top: 20px;
    }
    .icon-success {
      width: 50px;
      height: 50px;
      margin-right: 15px;
    }
    .success-title {
      font-size: 16px;
      color: #333;
      .success-mobile {
        margin-top: 5px;
      }
    }
  }
</style>
