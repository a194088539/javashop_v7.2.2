<template>
  <div id="distribution-manage">
    <nav-bar title="分销管理"/>
    <div class="distribution-container">
      <van-cell-group>
        <van-cell title="我推荐的人" is-link to="/member/distribution/my-recommend"/>
        <van-cell title="我的业绩" is-link to="/member/distribution/my-performance"/>
        <van-cell title="提现设置" is-link to="/member/distribution/withdrawals-setting"/>
        <van-cell title="提现申请" is-link to="/member/distribution/withdrawals-apply"/>
        <van-cell title="提现记录" is-link to="/member/distribution/withdrawals-history"/>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
  export default {
    name: "distribution-manage",
    head() {
      return {
        title: `分销-${this.site.title}`
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../../assets/styles/color";
  .distribution-container {
    padding-top: 46px;
  }

</style>
