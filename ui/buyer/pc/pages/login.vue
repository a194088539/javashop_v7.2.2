<template>
  <div id="login">
    <en-header-other title="欢迎登录"/>
    <!-- 大图 -->
    <div class="login-content">
      <div class="prompt">
        <div>
          <i></i>
          <p>依据《网络安全法》，{{ site.site_name }}为保障您的账户安全和正常使用，请尽快完成手机号验证！ 新版<a href="javascript:;" @click="handleArticle">《{{ site.site_name }}隐私政策》</a>已上线，将更有利于保护您的个人隐私。</p>
        </div>
      </div>
      <div class="login-banner">
        <div class="bg-banner">
          <div class="banner-img"></div>
          <!-- 登录页 -->
          <div class="login-box">
            <div class="login-form">
              <div class="tips-wapper">
                <i></i>
                <p>{{ site.site_name }}不会以任何理由要求您转账汇款，谨防诈骗。</p>
              </div>
              <div class="login-tab">
                <ul>
                  <li @click="!isConnect && (login_type = 'quick')">
                    <a href="javascript:;" class="tab-a" :class="[login_type === 'quick' && 'active', isConnect && 'disabled']">快捷登录</a>
                  </li>
                  <li @click="login_type = 'account'">
                    <a href="javascript:;" :class="[login_type === 'account' && 'active']">账号登录</a>
                  </li>
                </ul>
              </div>
              <div class="login-interface">
                <div v-show="login_type === 'quick'" class="login-show quick-login">
                  <form class="quick-form" @keyup.enter="handleLogin">
                    <div class="item item-form-o">
                      <label for="mobile">
                        <i class="iconfont ea-icon-mobile"></i>
                      </label>
                      <input id="mobile" v-model="quickForm.mobile" placeholder="请输入手机号" maxlength="11" autofocus>
                    </div>
                    <div class="item" style="border: none;" v-if="validateInFO.validator_type === 'ALIYUN'">
                      <div id="pc-valid" class="nc-container" v-if="login_type === 'quick'"></div>
                    </div>
                    <div class="item" v-else-if="validateInFO.validator_type === 'IMAGE'">
                      <label for="validcode-mobile">
                        <i class="iconfont ea-icon-safe"></i>
                      </label>
                      <input id="validcode-mobile" v-model="quickForm.captcha" placeholder="图片验证码" maxlength="4">
                      <img v-if="val_code_url" class="validcode-img" :src="val_code_url" @click="handleChangeValUrl">
                    </div>
                    <div class="item" v-else style="border:0;"></div>
                    <div class="item item-form-t">
                      <en-count-down-btn :start="sendValidMobileSms" :disabled="!accountAfsSuccess" @end="handleChangeValUrl" :class="['send-sms-btn', !accountAfsSuccess && 'is-disabled']"/>
                    </div>
                    <div class="item item-form-p">
                      <label for="sms-code">
                        <i class="iconfont ea-icon-sms"></i>
                      </label>
                      <input id="sms-code" v-model="quickForm.sms_code" placeholder="短信验证码" maxlength="6">
                    </div>
                    <div class="forget">
                      <span><nuxt-link :to="'/find-password' + MixinForward">忘记密码</nuxt-link></span>
                    </div>
                    <button class="form-sub" type="button" @click="handleLogin">登&nbsp;&nbsp;&nbsp;录</button>
                  </form>
                </div>
                <div v-show=" login_type === 'account'" class="login-show account-login">
                  <form class="account-form" @keyup.enter="handleLogin">
                    <div class="item">
                      <label for="username">
                        <i class="iconfont ea-icon-persion"></i>
                      </label>
                      <input id="username" v-model="accountForm.username" placeholder="邮箱/用户名/已验证手机">
                    </div>
                    <div class="item">
                      <label for="password">
                        <i class="iconfont ea-icon-password"></i>
                      </label>
                      <input id="password" v-model="accountForm.password" type="password" placeholder="请输入密码" autocompete="off">
                    </div>
                    <div class="item" style="border: none;" v-if="validateInFO.validator_type === 'ALIYUN'">
                      <div id="pc-valid" class="nc-container" v-if="login_type === 'account'"></div>
                    </div>
                    <div class="item" v-else-if="validateInFO.validator_type === 'IMAGE'">
                      <label for="validcode">
                        <i class="iconfont ea-icon-safe"></i>
                      </label>
                      <input id="validcode" v-model="accountForm.captcha" placeholder="图片验证码" maxlength="4">
                      <img v-if="val_code_url" class="validcode-img" :src="val_code_url" @click="handleChangeValUrl">
                    </div>
                    <div class="item" v-else style="border:0;"></div>
                    <div class="forget">
                      <span><nuxt-link :to="'/find-password' + MixinForward">忘记密码</nuxt-link></span>
                    </div>
                    <button :class="['form-sub', !accountAfsSuccess && 'is-disabled']" type="button" :disabled="!accountAfsSuccess" @click="handleLogin">登&nbsp;&nbsp;&nbsp;录</button>
                  </form>
                </div>
              </div>
              <!-- 第三方登录、立即注册 -->
              <div class="other-login">
                <ul>
                  <li class="other-one"><a :href="getConnectUrl('pc', 'QQ')" target="_blank"><span>QQ</span></a></li>
                  <li class="other-one"><a :href="getConnectUrl('pc', 'WECHAT')" target="_blank"><span>微信</span></a></li>
                  <li class="other-one"><a :href="getConnectUrl('pc', 'WEIBO')" target="_blank"><span>微博</span></a></li>
                  <li><a :href="getConnectUrl('pc', 'ALIPAY')" target="_blank"><span>支付宝</span></a></li>
                  <li class="other-right">
                    <nuxt-link :to="'/register' + MixinForward" class="registered">
                      <b></b>立即注册
                    </nuxt-link>
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex'
  import { RegExp } from '@/ui-utils'
  import Storage from '@/utils/storage'
  import jwt_decode from 'jwt-decode'
  import * as API_Common from '@/api/common'
  import * as API_Passport from '@/api/passport'
  import * as API_Connect from '@/api/connect'
  import * as API_Article from '@/api/article'
  import AliyunAfs from '@/components/AliyunAfs'
  export default {
    name: 'login',
    layout: 'full',
    async asyncData() {
      try {
        const protocol = await API_Article.getArticleDetail('115');
        return { protocol }
      } catch (e) {
        return { protocol: '隐私政策获取失败...' }
      }
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
        login_type: 'quick',
        // 图片验证码
        val_code_url: '',
        // 快捷登录 表单
        quickForm: {
          scene: 'LOGIN'
        },
        // 普通登录 表单
        accountForm: {
          scene: 'LOGIN'
        },
        // 是否为信任登录
        isConnect: false,
        // 验证信息
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
        // 是否请求完成
        isRequestFinish: true
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
            this.afs = new AliyunAfs('#pc-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
            this.afs.init()
          } else {
            this.accountAfsSuccess = true
          }
        }
      }
    },
    mounted() {
      setTimeout(this.handleChangeValUrl)
      const uuid_connect = Storage.getItem('uuid_connect')
      const isConnect = this.$route.query.form === 'connect' && !!uuid_connect
      this.isConnect = isConnect
      if (isConnect) {
        this.login_type = 'account'
      }
      API_Common.getValidator().then(res => {
        this.validateInFO = res
        const { validator_type, aliyun_afs } = res
        if (validator_type === 'ALIYUN') {
          this.afs = new AliyunAfs('#pc-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
          this.afs.init()
        } else {
          this.accountAfsSuccess = true
          this.handleChangeValUrl()
        }
      })
    },
    computed: {
      ...mapGetters(['site'])
    },
    methods: {
      //隐私政策
      handleArticle() {
        this.$layer.open({
          type: 1,
          title: '隐私政策',
          area: ['800px', '600px'],
          scrollbar: false,
          cancel: () => {
            location.href = '/login'
          },
          content: `<div style="padding: 15px">${this.protocol.content}</div>`
        })
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
        const params = JSON.parse(JSON.stringify(this.quickForm))
        return new Promise((resolve, reject) => {
          if (!params.mobile) {
            this.$message.error('请输入手机号码！')
            reject()
          } else if (!RegExp.mobile.test(params.mobile)) {
            this.$message.error('手机号码格式有误！')
            reject()
          } else {
            if (this.afs) {
              params.c_sessionid = this.afsParam.c_sessionid
              params.sig = this.afsParam.sig
              params.nc_token = this.afsParam.nc_token
              delete params.scene
              API_Passport.sendLoginSms(params.mobile, params).then(() => {
                this.$message.success('短信发送成功，请注意查收！')
                resolve()
              }).catch(() => {
                this.afs.reload()
                this.accountAfsSuccess = false
                reject()
              })
            } else {
              if (!params.captcha) {
                this.$message.error('请输入图片验证码！')
              } else {
                params.uuid = Storage.getItem('uuid')
                API_Passport.sendLoginSms(params.mobile, params).then(() => {
                  this.$message.success('短信发送成功，请注意查收！')
                  resolve()
                }).catch(() => {
                  this.handleChangeValUrl()
                  reject()
                })
              }
            }
          }
        })
      },
      /** 改变图片验证码URL */
      handleChangeValUrl() {
        this.isRequestFinish = true
        this.val_code_url = API_Common.getValidateCodeUrl(this.uuid, 'LOGIN')
      },
      /** 登录事件 */
      handleLogin() {
        if (!this.isRequestFinish) return
        if (!this.accountAfsSuccess) return
        const _forwardMatch = this.MixinForward.match(/\?forward=(.+)/) || []
        let forward = _forwardMatch[1]
        if (!forward || forward.indexOf('/login') > -1) {
          forward = '/'
        }
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
        this.isRequestFinish = false
        if (this.isConnect) {
          const uuid = Storage.getItem('uuid_connect')
          if (!uuid) {
            this.$message.error('参数异常，请刷新页面！')
            this.isRequestFinish = true
            return false
          }
          const params = JSON.parse(JSON.stringify(form))
          params.uuid = this.uuid
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
          }
          API_Connect.loginByConnect(uuid, params).then(async response => {
            if (response.result === 'bind_success') {
              this.setAccessToken(response.access_token)
              this.setRefreshToken(response.refresh_token)
              const expires = new Date(jwt_decode(response.refresh_token).exp * 1000)
              Storage.setItem('uid', response.uid, { expires })
              await this.getUserData()
              Storage.removeItem('uuid_connect')
              window.location.href = forward
            } else {
              this.$alert('当前用户已绑定其它账号！', () => {
                this.removeAccessToken()
                this.removeRefreshToken()
                Storage.removeItem('uuid_connect')
              })
            }
          }).catch(this.handleChangeValUrl)
        } else {
          const params = JSON.parse(JSON.stringify(form))
          if(this.afs) {
            params.c_sessionid = this.afsParam.c_sessionid
            params.sig = this.afsParam.sig
            params.nc_token = this.afsParam.nc_token
            delete params.scene
            delete params.captcha
            this.login({ login_type, form: params }).then(async () => {
              await this.getCartData()
              window.location.href = forward
            }).catch(() => {
              this.afs.reload()
              this.isRequestFinish = true
              this.accountAfsSuccess = false
            })
          } else {
            params.uuid = Storage.getItem('uuid')
            this.login({ login_type, form: params }).then(async () => {
              await this.getCartData()
              window.location.href = forward
            }).catch(this.handleChangeValUrl)
          }
        }
      },
      getConnectUrl: API_Connect.getConnectUrl,
      ...mapActions({
        login: 'user/loginAction',
        getCartData: 'cart/getCartDataAction',
        setAccessToken: 'user/setAccessTokenAction',
        removeAccessToken: 'user/removeAccessTokenAction',
        setRefreshToken: 'user/setRefreshTokenAction',
        removeRefreshToken: 'user/removeRefreshTokenAction',
        getUserData: 'user/getUserDataAction'
      })
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  .layui-layer-title {
    text-align: center;
    font-size: 16px;
  }
  .layui-layer-content {
    line-height: 30px;
  }
  .login-content .prompt {
    width: 100%;
    text-align: center;
    background: #fff8f0;
    margin: 0 auto;
    padding: 10px 0;
    a { color: black }
  }
  .login-content .login-banner {
    background: #FF5E18;
    width: 100%;
    .bg-banner {
      width: 1200px;
      margin: 0 auto;
      position: relative;
      .banner-img {
        background: url(../assets/images/background-banner.jpg) no-repeat center center;
        background-size: cover;
        height: 560px;
      }
    }
  }
  .login-box {
    float: right;
    position: absolute;
    right: 20px;
    top: 20px;
    width: 346px;
    background: #ffffff;
  }
  .login-box .login-form .tips-wapper {
    background: #fff8f0;
    width: 100%;
    padding-top: 10px;
    padding-bottom: 10px;
    text-align: center;
    p {
      font-size: 12px;
      color: #999;
    }
  }
  .login-box .login-form .login-tab {
    height: 60px;
    border-bottom: 1px solid #f4f4f4;
    ul li {
      float: left;
      width: 50%;
      padding: 20px 0;
      text-align: center;
      font-size: 20px;
      a {
        display: block;
        height: 18px;
        width: 99%;
        color: #666;
        &:hover, &.active { color: $color-main }
        &.disabled {
          cursor: not-allowed;
          color: #ccc
        }
      }
      .tab-a { border-right: 1px solid #f4f4f4 }
    }
  }
  .login-form .login-interface .account-login { display: block }
  .login-form .login-interface .login-show form {
    width: 280px;
    margin: 25px auto;
  }
  .active { color: $color-main }
  .login-interface form .item.focus {
    border-color: #666;
    label {
      border-color: #666;
      .iconfont { color: #666 }
    }
  }
  .login-interface form .item-form-t {
    height: 30px;
    border: 0 !important;
    button {
      width: 100%;
      height: 100%;
      cursor: pointer;
      border-radius: 3px;
    }
  }
  .login-interface .quick-form .item {
    position: relative;
    width: 100%;
    height: 35px;
    margin-bottom: 10px;
    border: 1px solid #bdbdbd;
  }
  .login-interface .account-form .item {
    position: relative;
    width: 100%;
    height: 35px;
    margin-bottom: 25px;
    border: 1px solid #bdbdbd;
  }
  .validcode-img {
    position: absolute;
    top: 0;
    right: 0;
    width: 85px;
    height: 35px;
    cursor: pointer;
  }
  .login-interface form .item label {
    width: 35px;
    height: 35px;
    float: left;
    display: inline-block;
    border-right: 1px solid #bdbdbd;
  }
  .login-interface form .item .iconfont {
    display: block;
    width: 100%;
    height: 100%;
    text-align: center;
    line-height: 35px;
    color: #666;
  }
  .login-interface form .item input {
    display: inline-block;
    height: 35px;
    line-height: 35px;
    width: 225px;
    padding-left: 10px;
  }
  .login-interface form .forget {
    height: 18px;
    margin: 20px 0;
  }
  .login-interface form .forget span {
    float: right;
  }
  .login-interface form .form-sub {
    width: 100%;
    height: 35px;
    background: $color-main;
    text-align: center;
    margin: 10px 0;
    cursor: pointer;
    color: #fff;
    font-size: 14px;
    transition: background ease-out .2s;
    &.is-disabled {
      cursor: not-allowed;
      background-color: rgba($color: $color-main, $alpha: .6);

      &:hover {
        background-color: rgba($color: $color-main, $alpha: .6);
      }
    }
  }
  .login-interface form .form-sub:hover {
    background: darken($color-main, 10%);
  }
  .login-box .login-form .other-login {
    height: 50px;
    border-top: 1px solid #f4f4f4;
    padding: 0 20px;
    background-color: #fcfcfc;
  }
  .login-box .login-form .other-login ul li {
    float: left;
    line-height: 50px;
    a>span {
      padding: 0 10px;
      color: #666666;
      &:hover { color: $color-main }
    }
  }
  .login-box .login-form .other-login ul .other-one a>span {
    border-right: 1px solid #bdbdbd;
  }
  .login-box .login-form .other-login ul .other-right {
    float: right;
  }
  .other-login ul .other-right a {
    color: $color-main;
    font-size: 15px;
  }
  .send-sms-btn {
    width: 187px;
    height: 35px;
    border: none;
    color: #ffffff;
    background-color: #607d8b;
    transition: background ease-out .2s;
  }
  .send-sms-btn:hover {
    background-color: #516a77;
  }
  .send-sms-btn:disabled {
    background-color: #ccc !important;
    color: #fff !important;
    cursor: not-allowed !important;
  }
</style>
