<template>
  <div id="account-security">
    <nav-bar title="账户安全"/>
    <div class="security-container">
      <van-cell-group>
        <van-cell title="账户密码" value="修改" is-link to="/member/change-password" />
        <van-cell title="手机号码" value="修改" is-link to="/member/change-mobile" />
        <van-cell title="电子邮箱" value="修改" is-link to="/member/change-email" />
        <van-cell title="支付密码" v-if="!isPassword" value="设置" is-link to="/member/change-paymentpassword" />
        <van-cell title="支付密码" v-else value="修改" is-link to="/member/change-paymentpassword" />
        <!-- 暂时先将WAP端的第三方登录绑定功能注释 -->
        <!--<van-cell title="第三方登录" value="查看" is-link to="./account-binding" />-->
      </van-cell-group>
    </div>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'
  import * as API_Deposit from '@/api/deposit'
  export default {
    name: 'account-safe',
    data() {
      return {
        isPassword:''
      }
    },
    head() {
      return {
        title: `账户安全-${this.site.title}`
      }
    },
    created() {
      if (!this.$store.getters.user.mobile) {
        this.$router.replace({ name: 'member-bind-mobile' })
      }
    },
    mounted() {
      API_Deposit.checkPassword().then(response => {
        this.isPassword = response
      })
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  .security-container {
    padding-top: 46px;
  }
</style>
