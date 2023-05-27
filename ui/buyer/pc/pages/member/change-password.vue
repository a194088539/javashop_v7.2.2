<template>
  <div id="change-password">
    <div v-show="step === 1" class="valid-mobile-container">
      <el-alert type="info" title="" :closable="false">
        <h2>为什么要进行身份验证？</h2>
        <p>1. 为保障您的账户信息安全，在变更账户中的重要信息时需要身份验证，感谢您的理解与支持。 </p>
        <p>2. 验证身份遇到问题？请提供用户名，手机号，历史发票，点击联系我司 在线客服 或者拨打400*****400咨询。</p>
      </el-alert>
      <el-form v-if="user" :model="validMobileForm" :rules="validMobileRules" ref="validMobileForm" label-width="110px">
        <el-form-item label="已验证手机：">
          <h2>{{ user.mobile | secrecyMobile }}</h2>
        </el-form-item>
        <div style="margin: 0 0 22px 110px;width:300px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="slider-valid" class="nc-container"></div>
        </div>
        <el-form-item label="图片验证码：" prop="captcha" class="img-code" v-else-if="validateInFO.validator_type === 'IMAGE'">
          <el-input v-model="validMobileForm.captcha" placeholder="请输入图片验证码" clearable :maxlength="4">
            <img slot="append" :src="valid_img_url" @click="getValidImgUrl">
          </el-input>
        </el-form-item>
        <div v-else></div>
        <el-form-item label="短信验证码：" prop="sms_code" class="sms-code" v-if="accountAfsSuccess">
          <el-input v-model="validMobileForm.sms_code" placeholder="请输入短信验证码" clearable :maxlength="6">
            <en-count-down-btn :time="60" :start="sendValidMobileSms" @end="getValidImgUrl" slot="append"/>
          </el-input>
        </el-form-item>
        <el-form-item label="">
          <el-button @click.stop="submitValMobileForm" v-if="accountAfsSuccess">提交验证</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-show="step === 2" class="change-password-container">
      <el-alert type="warning" title="" :closable="false">
        <h2>提示</h2>
        <p>1. 密码只能为6-20个英文字母或数字。 </p>
        <p>2. 请务必牢记您的新密码。</p>
      </el-alert>
      <el-form :model="changePasswordForm" :rules="changePasswordRules" ref="changePasswordForm" label-width="140px">
        <el-form-item label="请输入密码：" prop="password">
          <el-input v-model="changePasswordForm.password" placeholder="请输入密码" type="password" autocompete="off"></el-input>
        </el-form-item>
        <el-form-item label="请确认密码：" prop="rep_password">
          <el-input v-model="changePasswordForm.rep_password" placeholder="请确认密码" type="password" auto-complete="off"></el-input>
        </el-form-item>
        <div style="margin: 0 0 22px 33px;width:300px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="step-slider-valid" class="nc-container"></div>
        </div>
        <el-form-item label="图片验证码：" prop="captcha" class="img-code" v-else-if="validateInFO.validator_type === 'IMAGE'">
          <el-input v-model="changePasswordForm.captcha" placeholder="请输入图片验证码" clearable :maxlength="4">
            <img slot="append" :src="valid_img_url" @click="getValidImgUrl">
          </el-input>
        </el-form-item>
        <div v-else></div>
        <el-form-item label="">
          <el-button @click.stop="submitChangeForm" v-if="accountAfsSuccess">确认修改</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'
  import * as API_Common from '@/api/common'
  import * as API_Safe from '@/api/safe'
  import { RegExp } from '@/ui-utils'
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
        /** 验证手机 表单规则 */
        validMobileRules: {
          captcha: [this.MixinRequired('请输入图片验证码！')],
          sms_code: [this.MixinRequired('请输入短信验证码！')]
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
        /** 修改密码 表单规则 */
        changePasswordRules: {
          password: [
            this.MixinRequired('请输入新的登录密码！'),
            {
              validator: (rule, value, callback) => {
                if (!RegExp.password.test(value)) {
                  callback(new Error('密码应为6-20位英文或数字！'))
                } else {
                  callback()
                }
              },
              trigger: 'blur'
            }
          ],
          rep_password: [
            this.MixinRequired('请再次输入密码！'),
            {
              validator: (rule, value, callback) => {
                if (value !== this.changePasswordForm.password) {
                  callback(new Error('两次输入的密码不相同'))
                } else {
                  callback()
                }
              },
              trigger: 'blur'
            }
          ],
          captcha: [this.MixinRequired('请输入图片验证码！')]
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
    computed: {
      ...mapGetters(['user'])
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
            if (!params.captcha) {
              reject()
              this.$message.error('请输入图片验证码！')
            } else {
              API_Safe.sendMobileSms(params).then(() => {
                this.$message.success('验证码发送成功，请注意查收！')
                resolve()
              }).catch(() => {
                this.getValidImgUrl()
                reject()
              })
            }
          }
        })
      },
      /** 手机验证 */
      submitValMobileForm() {
        this.$refs['validMobileForm'].validate((valid) => {
          if (valid) {
            const { sms_code } = this.validMobileForm
            if (this.afs) {
              API_Safe.validChangePasswordSms(sms_code).then(() => {
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
              API_Safe.validChangePasswordSms(sms_code).then(() => {
                this.step = 2
                this.getValidImgUrl()
              }).catch(this.getValidImgUrl)
            }
          } else {
            this.$message.error('表单填写有误，请检查！')
            return false
          }
        })
      },
      /** 修改密码 */
      submitChangeForm() {
        this.$refs['changePasswordForm'].validate((valid) => {
          const params = JSON.parse(JSON.stringify(this.changePasswordForm))
          params.password = md5(params.password)
          delete params.rep_password
          if (valid) {
            if(this.afs) {
              params.c_sessionid = this.afsParam.c_sessionid
              params.sig = this.afsParam.sig
              params.nc_token = this.afsParam.nc_token
              delete params.scene
              delete params.captcha
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
              params.uuid = this.uuid
              if (!params.captcha) {
                this.$message.error('请输入图片验证码！')
                reject()
              } else {
                API_Safe.changePassword(params).then(() => {
                  this.$message.success('密码修改成功，请重新登录！')
                  setTimeout(async () => {
                    await this.$store.dispatch('user/logoutAction', 'change-pwd')
                    this.$router.push('/login')
                  }, 200)
                }).catch(this.getValidImgUrl)
              }
            }
          } else {
            this.$message.error('表单填写有误，请检查！')
            return false
          }
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .valid-mobile-container, .change-password-container {
    width: 100%;
    /deep/ .el-alert {
      h2 { margin: 20px 0 }
      p { margin-bottom: 10px }
    }
    /deep/ .el-form {
      margin-top: 10px;
      padding-left: 24px
    }
    /deep/ .el-input__inner { width: 190px }
    /deep/ .img-code {
      .el-input { width: auto }
      .el-input-group__append {
        padding: 0;
        cursor: pointer;
        img { height: 38px }
      }
    }
    /deep/ .sms-code {
      .el-input { width: auto }
    }
  }
</style>
