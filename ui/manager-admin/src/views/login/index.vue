<template>
  <div class="login-container">
    <div class="login-bg" id="login-bg"></div>
    <div class="login-form" @keyup.enter="submitLogin">
      <a :href="MixinBuyerDomain" class="login-logo" target="_blank">
        <img class="login-logo-img" src="../../assets/logo_images/logo-javashop-rectangle-light.png" alt="javashop">
      </a>
      <el-form :model="loginForm" :rules="loginRules" ref="loginForm" class="login-input">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" :placeholder="translateKey('username')" autofocus clearable>
            <svg-icon slot="prefix" class="el-input__icon" icon-class="user"/>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" autocompete="off" :placeholder="translateKey('password')" :minlength="6" :maxlength="20" clearable>
            <svg-icon slot="prefix" class="el-input__icon" icon-class="password"/>
          </el-input>
        </el-form-item>
        <div style="margin-bottom: 20px" v-if="validateInFO.validator_type === 'ALIYUN'">
          <div id="pc-valid" class="nc-container"></div>
        </div>
        <el-form-item prop="validcode" v-else-if="validateInFO.validator_type === 'IMAGE'">
          <el-input v-model="loginForm.validcode" :placeholder="translateKey('validcode')" :maxlength="4" clearable>
            <template slot="append">
              <img class="login-validcode-img" :src="validcodeImg" @click="changeValidcode">
            </template>
          </el-input>
        </el-form-item>
        <div v-else></div>
        <el-form-item>
          <el-button type="primary" :loading="loading" :disabled="!accountAfsSuccess" @click="submitLogin">{{translateKey('logIn')}}</el-button>
        </el-form-item>
        <lang-select class="set-language"/>
      </el-form>
    </div>
  </div>
</template>

<script>
  import * as API_common from '@/api/common'
  import 'particles.js'
  import particlesjsConfig from '@/assets/particlesjs-config.json'
  import LangSelect from '@/components/LangSelect'
  import Storage from '@/utils/storage'
  import uuidv1 from 'uuid/v1'
  import AliyunAfs from '@/components/AliyunAfs'

  export default {
    components: { LangSelect },
    name: 'login',
    data() {
      return {
        loading: false,
        loginForm: {
          username: '',
          password: '',
          scene: 'LOGIN',
          validcode: ''
        },
        loginRules: {
          username: [
            { required: true, message: this.translateKey('val_username'), trigger: 'blur' }
          ],
          password: [
            { required: true, message: this.translateKey('val_password'), trigger: 'blur' }
          ],
          validcode: [
            { required: true, message: this.translateKey('val_validcode'), trigger: 'blur' }
          ]
        },
        validcodeImg: '',
        uuid: Storage.getItem('admin_uuid'),
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
        accountAfsSuccess: false
      }
    },
    mounted() {
      const uuid = Storage.getItem('admin_uuid')
      if (uuid) {
        this.uuid = uuid
      } else {
        const _uuid = uuidv1()
        this.uuid = _uuid
        Storage.setItem('admin_uuid', _uuid, { expires: 30 })
      }
      this.changeValidcode()
      this.loadParticles()
      /** 获取登录验证方式 **/
      API_common.getValidator().then(res => {
        this.validateInFO = res
        const { validator_type, aliyun_afs } = res
        if (validator_type === 'ALIYUN') {
          this.afs = new AliyunAfs('#pc-valid', aliyun_afs.scene, aliyun_afs.app_key, this.initCaptcha)
          this.afs.init()
        } else {
          this.accountAfsSuccess = true
        }
      })
    },
    methods: {
      /** 阿里云滑动验证 通过后的回调函数 **/
      initCaptcha(data) {
        this.afsParam.c_sessionid = data.csessionid
        this.afsParam.sig = data.sig
        this.afsParam.nc_token = data.nc_token
        this.accountAfsSuccess = true
      },
      /** 翻译 */
      translateKey(key) {
        return this.$t('login.' + key)
      },
      /** 加载背景插件 */
      loadParticles() {
        window.particlesJS('login-bg', particlesjsConfig)
      },
      /** 更换图片验证码 */
      changeValidcode() {
        this.validcodeImg = API_common.getValidateCodeUrl('LOGIN', this.uuid)
      },
      /** 表单提交 */
      submitLogin() {
        if (!this.accountAfsSuccess) return
        if (this.afs) {
          const params = this.MixinClone(this.loginForm)
          params.c_sessionid = this.afsParam.c_sessionid
          params.sig = this.afsParam.sig
          params.nc_token = this.afsParam.nc_token
          delete params.validcode
          delete params.scene
          this.loading = true
          this.$store.dispatch('loginAction', params).then(() => {
            this.loading = false
            const forward = this.$route.query.forward
            let query = JSON.parse(JSON.stringify(this.$route.query))
            delete query.forward
            this.$router.push({ path: forward || '/', query })
          }).catch(() => {
            this.loading = false
            this.afs.reload()
            this.accountAfsSuccess = false
          })
        } else {
          this.$refs.loginForm.validate((valid) => {
            if (valid) {
              const params = this.MixinClone(this.loginForm)
              params.uuid = this.uuid
              params.captcha = params.validcode
              delete params.validcode
              this.loading = true
              this.$store.dispatch('loginAction', params).then(() => {
                this.loading = false
                const forward = this.$route.query.forward
                let query = JSON.parse(JSON.stringify(this.$route.query))
                delete query.forward
                this.$router.push({ path: forward || '/', query })
              }).catch(() => {
                this.loading = false
                this.changeValidcode()
              })
            } else {
              this.$message.error(this.translateKey('val_form'))
              return false
            }
          })
        }
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../styles/mixin";
  /deep/ .nc-container .nc_scale span{
    height: 34px;
  }
  .login-container {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
  }

  .bg {
    position: absolute;
    z-index: -1;
    width: 100%;
    height: 100%;
  }

  $form_width: 325px;
  .login-form {
    position: absolute;
    z-index: 1;
    left: 50%;
    top: 100px;
    padding: 20px 15px;
    margin-left: -(($form_width+30px)/2);
    width: $form_width;
    background-color: #fff;
    @include box-shadow(0 0 15px 2px #d8dce5)
  }
  .login-logo {
    width: 100%;
    height: 110px;
  }
  .login-logo-img {
    width: 300px;
    height: 100px;
    margin: 0 auto;
    display: block;
  }

  .login-input {
    margin-top: 20px;
    .el-button {
      width: 100%;
    }
    /deep/ .el-input-group__append {
      padding: 0;
      margin: 0;
      border: 0;
    }
    .login-validcode-img {
      width: 90px;
      height: 32px;
      display: block;
      cursor: pointer;
    }
  }
</style>
