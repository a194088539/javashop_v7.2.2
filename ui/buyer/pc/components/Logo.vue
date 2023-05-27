<template>
  <nuxt-link to="/">
    <img :src="$store.getters.site.logo" id="logo">
  </nuxt-link>
</template>

<script>
  export default {
    name: 'EnLogo',
    mounted() {
      if (!this.$store.getters.site) {
        this.$store.dispatch('getCommonDataAction')
      }
    }
  }
</script>
<style type="text/scss" lang="scss" scoped>
  #logo {
    width: 260px;
    height: 100%;
  }
</style>
