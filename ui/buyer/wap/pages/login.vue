<template>
  <div id="login">
    <van-nav-bar
      title="登录"
      left-arrow
      @click-left="MixinRouterIndex"
      @click-right="$router.push('clear')">
      <i class="clear-pl" slot="right"/>
    </van-nav-bar>
    <!--登录方式切换-->
    <div class="login-tab">
      <div
        class="tab-item account"
        :class="[login_type === 'account' && 'active']"
        @click="login_type = 'account'"
      >
        <span>账号密码登录</span>
      </div>
      <div
        class="tab-item quick"
        :class="[login_type === 'quick' && 'active']"
        @click="login_type = 'quick'"
      >
        <span>短信验证码登录</span>
      </div>
    </div>
    <!--账号密码登录、短信验证码登录-->
    <div class="login-content">
      <div v-show="login_type === 'account'" class="content-item account">
        <no-ssr>
          <van-cell-group :border="false">
            <van-field
              v-model="accountForm.username"
              placeholder="邮箱/用户名/已验证手机"
            >
              <span slot="label">用&emsp;户&emsp;名</span>
            </van-field>
            <van-field
              v-model="accountForm.password"
              type="password"
              autocompete="off"
              placeholder="请输入密码"
            >
              <span slot="label">密&emsp;&emsp;&emsp;码</span>
            </van-field>
            <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
              <div id="slider-valid" class="nc-container" v-if="login_type === 'account'"></div>
            </div>
            <van-field
              v-else-if="validateInFO.validator_type === 'IMAGE'"
              v-model="accountForm.captcha"
              center
              label="图片验证码"
              placeholder="请输入图片验证码"
              maxlength="4"
            >
              <img v-if="captcha_url" slot="button" :src="captcha_url" @click="handleChangeCaptchalUrl" class="captcha-img"/>
            </van-field>
            <div v-else></div>
          </van-cell-group>
        </no-ssr>
        <div class="login-btn">
          <van-button size="large" :disabled="login_disabled_account" @click="handleLogin">登&nbsp;&nbsp;&nbsp;录</van-button>
        </div>
      </div>
      <div v-show="login_type === 'quick'" class="content-item quick">
        <no-ssr>
          <van-cell-group :border="false">
            <van-field
              v-model="quickForm.mobile"
              type="tel"
              placeholder="请输入手机号"
              maxlength="11"
            >
              <span slot="label">手&emsp;机&emsp;号</span>
            </van-field>
            <div style="padding: 10px 15px" v-if="validateInFO.validator_type === 'ALIYUN'">
              <div id="slider-valid" class="nc-container" v-if="login_type === 'quick'"></div>
            </div>
            <van-field
              v-else-if="validateInFO.validator_type === 'IMAGE'"
              v-model="quickForm.captcha"
              center
              label="图片验证码"
              placeholder="请输入图片验证码"
              maxlength="4"
            >
              <img v-if="captcha_url" slot="button" :src="captcha_url" @click="handleChangeCaptchalUrl" class="captcha-img"/>
            </van-field>
            <div v-else></div>
            <van-field
              v-if="accountAfsSuccess"
              v-model="quickForm.sms_code"
              center
              label="短信验证码"
              placeholder="请输入短信验证码"
            >
              <en-count-down-btn slot="button" :start="sendValidMobileSms">发送验证码</en-count-down-btn>
            </van-field>
          </van-cell-group>
        </no-ssr>
        <div class="login-btn">
          <van-button size="large" :disabled="login_disabled_quick" @click="handleLogin">登&nbsp;&nbsp;&nbsp;录</van-button>
        </div>
      </div>
    </div>
    <!--忘记密码、手机注册-->
    <div class="login-nav">
      <nuxt-link :to="'/find-password' + MixinForward">
        <i class="iconfont ea-icon-password"></i>忘记密码
      </nuxt-link>
      <nuxt-link :to="'/register' + MixinForward">
        <i class="iconfont ea-icon-mobile"></i>手机注册
      </nuxt-link>
    </div>
    <div class="agreement-tips">
      <p>登录即代表您已同意<a href="javascript:;" @click="showPolicy = true">{{ site.site_name }}隐私政策</a></p>
    </div>
    <van-popup
      v-model="showPolicy"
      position="bottom"
      style="height: 75%"
    >
      <van-nav-bar
        title="隐私政策"
        class="policy-title"
      />
      <div class="policy-content" v-html="policy.content"></div>
    </van-popup>
  </div>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex'
  import { RegExp } from '@/ui-utils'
  import jwt_decode from 'jwt-decode'
  import * as API_Common from '@/api/common'
  import * as API_Passport from '@/api/passport'
  import * as API_Connect from '@/api/connect'
  import * as API_Article from '@/api/article'
  import EnCountDownBtn from "@/components/CountDownBtn"
  import Storage from '@/utils/storage'
  import AliyunAfs from '@/components/AliyunAfs'
  export default {
    name: 'login',
    components: {EnCountDownBtn},
    layout: 'full',
    async asyncData() {
      const policy = await API_Article.getArticleDetail('115')
      return { policy }
    },
    head() {
      return {
        title: `会员登录-${this.site.title}`
      }
    },
    data() {
      return {
        // uuid
        uuid: Storage.getItem('uuid'),
        // 登录类型
        login_type: 'account', // 'account',
        /** 图片验证码 */
        captcha_url: '',
        /** 快捷登录 表单 */
        quickForm: {
          scene: 'LOGIN',
          mobile: '',
          captcha: '',
          sms_code: ''
        },
        /** 普通登录 表单 */
        accountForm: {
          scene: 'LOGIN',
          username: '',
          password: '',
          captcha: ''
        },
        isConnect: false,
        // 是否为微信内置浏览器
        isWXBrowser: process.client ? /micromessenger/i.test(navigator.userAgent) : false,
        // 是否为支付宝内置浏览器
        isAliPayBrowser: process.client ? (navigator.userAgent.match(/Alipay/i) === 'alipay') : false,
        validateInFO: {},
        // 阿里云验证条实例
        afs: null,
        // 当滑动验证成功之后返回的参数
        afsParam: {
          c_sessionid: '',
          sig: '',
          nc_token: ''
        },
        // 阿里云滑动验证是否通过 && 如果不是 阿里云验证则直接设置为 true
        accountAfsSuccess: false,
        // 是否显示隐私政策
        showPolicy: false
      }
    },
    watch: {
      login_type: {
        handler(newValue) {
          this.accountAfsSuccess = false
          this.afs = null
          this.afsParam.c_sessionid = this.afsParam.sig = this.afsParam.nc_token = ''
          const { validator_type, aliyun_afs } = this.validateInFO
          if (validator_type === 'ALIYUN') {
            this.afs = new AliyunAfs('#slider-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
            this.afs.init()
          } else {
            this.accountAfsSuccess = true
          }
        }
      }
    },
    computed: {
      // 普通登录按钮 是否禁用
      login_disabled_account() {
        const { username, password, captcha } = this.accountForm
        if(this.afs) {
          return !(username && password && this.accountAfsSuccess)
        } else {
          return !(username && password && captcha)
        }
      },
      // 短信登录按钮 是否禁用
      login_disabled_quick() {
        const { captcha, mobile, sms_code } = this.quickForm
        if(this.afs) {
          return !(this.accountAfsSuccess && mobile && sms_code)
        } else {
          return !(captcha && mobile && sms_code)
        }
      },
      ...mapGetters(['site'])
    },
    mounted() {
      // 如果已登录  直接跳转
      if (Storage.getItem('refresh_token')) {
        const _forwardMatch = this.MixinForward.match(/\?forward=(.+)/) || []
        const forward = _forwardMatch[1]
        window.location.href = forward || '/'
        return
      } else {
        // 监听手机浏览器返回键时禁止返回之前的路由
        // if (window.history && window.history.pushState) {
        //     window.addEventListener('popstate', this.forbidBack, false);
        //     this.forbidBack() 
        // }

        this.handleChangeCaptchalUrl()
        const uuid_connect = Storage.getItem('uuid_connect')
        const isConnect = (this.$route.query.form === 'connect' && !!uuid_connect) || this.MixinIsWeChatBrowser()
        this.isConnect = isConnect
        if (isConnect) {
          this.login_type = 'account'
        }
      }
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
    destoryed (){
      // 离开页面时销毁监听
      window.removeEventListener('popstate', this.forbidBack, false);
    },
    methods: {
      forbidBack() {
          window.history.pushState(null, null,document.URL);
          // window.history.forward(1);
      },
      // 阿里云滑动验证 通过后的回调函数
      initCaptcha(data) {
        this.afsParam.c_sessionid = data.csessionid;
        this.afsParam.sig = data.sig;
        this.afsParam.nc_token = data.nc_token;
        this.accountAfsSuccess = true
      },
      /** 发送短信验证码异步回调 */
      sendValidMobileSms() {
        const { mobile, captcha, scene } = this.quickForm
        const params = JSON.parse(JSON.stringify(this.quickForm))
        return new Promise((resolve, reject) => {
          if (!mobile) {
            this.$message.error('请输入手机号码！')
            reject()
          } else if (!RegExp.mobile.test(mobile)) {
            this.$message.error('手机号码格式有误！')
            reject()
          } else {
            if (this.isConnect) {
              API_Connect.sendMobileLoginSms(mobile, captcha, this.uuid, scene).then(() => {
                this.$message.success('短信发送成功，请注意查收！')
                resolve()
              })
            } else {
              if (this.afs) {
                params.c_sessionid = this.afsParam.c_sessionid
                params.sig = this.afsParam.sig
                params.nc_token = this.afsParam.nc_token
                delete params.scene
                API_Passport.sendLoginSms(mobile, params).then(() => {
                  this.$message.success('短信发送成功，请注意查收！')
                  resolve()
                })
              } else {
                if (!captcha) {
                  this.$message.error('请输入图片验证码！')
                  reject()
                } else {
                  params.uuid = Storage.getItem('uuid')
                  API_Passport.sendLoginSms(mobile, params).then(() => {
                    this.$message.success('短信发送成功，请注意查收！')
                    resolve()
                  })
                }
              }
            }
          }
        }).catch((e) => {})
      },
      /** 改变图片验证码URL */
      handleChangeCaptchalUrl() {
        this.captcha_url = API_Common.getValidateCodeUrl(this.uuid, 'LOGIN')
      },
      /** 登录事件 */
      handleLogin() {
        const _forwardMatch = this.MixinForward.match(/\?forward=(.+)/) || []
        const forward = _forwardMatch[1]
        const login_type = this.login_type
        const form = login_type === 'quick' ? this.quickForm : this.accountForm
        if (login_type === 'quick') {
          if (!form.mobile || !RegExp.mobile.test(form.mobile) || !form.sms_code) {
            this.$message.error('表单填写有误，请检查！')
            return false
          }
        } else {
          // 如果是 阿里云滑动验证
          if (this.afs) {
            if (!form.username || !form.password) {
              this.$message.error('表单填写有误，请检查！')
              return false
            }
          } else {
            if (!form.username || !form.password || !form.captcha) {
              this.$message.error('表单填写有误，请检查！')
              return false
            }
          }
        }
        if (this.isConnect) {
          let uuid = Storage.getItem('uuid_connect')
          if (!uuid) {
            this.$message.error('参数异常，请刷新页面！')
            return false
          }
          const params = JSON.parse(JSON.stringify(form))
          params.uuid = this.uuid
          if(this.afs) {// 判断如果在微信浏览器里是滑动验证
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            delete params.captcha
            if (login_type === 'quick') {
              API_Connect.loginByMobileConnect(uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
            } else {
              API_Connect.loginByConnect(uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
            }
          }else {
            if (login_type === 'quick') {
              API_Connect.loginByMobileConnect(uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
            } else {
              API_Connect.loginByConnect(uuid, params).then(loginCallback).catch(this.handleChangeCaptchalUrl)
            }
          }
          
          const _this = this
          // 登录回调
          async function loginCallback(response) {
            if (response.result === 'bind_success') {
              const { uid, access_token, refresh_token } = response
              _this.$store.dispatch('user/setAccessTokenAction', access_token)
              _this.$store.dispatch('user/setRefreshTokenAction', refresh_token)
              const expires = new Date(jwt_decode(refresh_token).exp * 1000)
              Storage.setItem('uid', uid, { expires })
              await _this.getUserData()
              if (_this.MixinIsWeChatBrowser()) {
                window.location.href = forward || '/'
                return
              }
              if (forward && /^http/.test(forward)) {
                window.location.href = forward
              } else {
                window.location.href = forward || '/'
              }
            } else {
              _this.$confirm('当前用户已绑定其它账号，请先解绑！', () => {
                _this.removeAccessToken()
                _this.removeRefreshToken()
                this.$route.push('/')
              })
            }
          }
        } else {
          const params = JSON.parse(JSON.stringify(form))
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            delete params.captcha
            this.login({ login_type, form: params }).then(() => {
              if (forward && /^http/.test(forward)) {
                // window.location.href = forward
                this.$route.push(forward)
              } else {
                this.$router.replace({ path: forward || '/' })
              }
            }).catch(() => {
              this.afs.reload()
              this.accountAfsSuccess = false
            })
          } else {
            params.uuid = Storage.getItem('uuid')
            this.login({ login_type, form: params }).then(() => {
              if (forward && /^http/.test(forward)) {
                // window.location.href = forward
                this.$router.push(forward)
              } else {
                this.$router.replace({ path: forward || '/' })
              }
            }).catch(this.handleChangeCaptchalUrl)
          }
        }
      },
      /** 获取第三方登录链接 */
      getConnectUrl: API_Connect.getConnectUrl,
      ...mapActions({
        login: 'user/loginAction',
        removeAccessToken: 'user/removeAccessTokenAction',
        removeRefreshToken: 'user/removeRefreshTokenAction',
        getUserData: 'user/getUserDataAction'
      })
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  .van-popup {
    overflow: hidden;
  }
  .policy-title {
    position: relative;
  }
  .policy-content {
    padding: 10px;
    height: calc(100% - 46px - 44px);
    overflow: scroll;
    box-sizing: border-box;
  }
  .login-tab {
    height: 44px;
    line-height: 45px;
    border-bottom: 1px solid #ebeef5;
    color: #333;
    font-size: 14px;
    .tab-item {
      width: 50%;
      float: left;
      text-align: center;
      &.active span {
        border-bottom: 2px solid #f23030;
        padding: 13px 10px;
      }
      &.disabled {
        color: #999;
        pointer-events: none;
      }
    }
  }
  .login-content {
    padding-top: 10px;
    padding-bottom: 20px;
    .login-btn {
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
      width: 60px;
      height: 24px;
    }
  }
  .login-nav {
    padding: 0 15px;
    a + a { float: right }
  }
  .agreement-tips {
    margin-top: 240px;
    width: 100%;
    text-align: center;
    font-size: 14px;
    line-height: 30px;
    color: #333
  }
  /deep/ .clear-pl {
    display: inline-block;
    width: 15px;
    height: 15px;
  }
  /deep/ .van-field__button {
	  padding-left: 0;
  }

</style>
