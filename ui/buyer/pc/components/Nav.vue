<template>
  <div id="nav" class="w">
    <en-category/>
    <ul class="nav-list">
      <li v-for="nav in $store.getters.navList" :key="nav.navigation_id">
        <a v-if="nav.url.indexOf('http') == 0" :href="nav.url" target="_blank">{{ nav.navigation_name }}</a>
        <nuxt-link v-else :to="nav.url">{{ nav.navigation_name }}</nuxt-link>
      </li>
    </ul>
  </div>
</template>

<script>
  import { parse, stringify } from 'querystring'
  export default {
    name: 'EnNav',
    methods: {
      getUrl(url) {
        if (/keyword=(.*)/.test(url)) {
          let querys = url.match(/\?(.*)/)
          const query = parse(querys[1])
          const qs = stringify(query)
          url = url.replace(/\?(.*)/, `?${qs}`)
        }
        return url
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  #nav {
    display: flex;
    height: 40px;
    .nav-list {
      overflow: hidden;
      max-width: calc(1210px - 240px);
      li {
        vertical-align: top;
        letter-spacing: normal;
        word-spacing: normal;
        display: inline-block;
        a {
          font-size: 16px;
          line-height: 40px;
          color: #333;
          height: 40px;
          padding: 0 24px;
          display: inline-block;
          zoom: 1;
          font-weight: 500;
          &:hover {
            color: $color-main
          }
        }
      }
    }
  }
</style>
