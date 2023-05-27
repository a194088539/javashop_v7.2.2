<template>
  <div id="payment-complete" class="w" style="text-align: center">
    <h1 style="line-height: 150px"><i class="el-icon-success"></i>支付完成</h1>
    <div class="cp-btns">
      <nuxt-link v-if="type === 'RECHARGE'" to="/member/account-balance"><el-button type="danger">查看余额</el-button></nuxt-link>
      <nuxt-link v-if="type === 'TRADE' || type === 'ORDER'" to="/member/my-order"><el-button type="danger">查看订单</el-button></nuxt-link>
      <a href="/"><el-button type="danger">返回首页</el-button></a>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'payment-complete',
    data() {
      return {
        type: this.$route.query.type
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  h1 {
    color: #67C23A;
    .el-icon-success {
      margin-right: 10px;
    }
  }
  .cp-btns {
    margin: 20px auto;
    text-align: center;
    a + a {
      margin-left: 10px;
    }
  }
</style>
