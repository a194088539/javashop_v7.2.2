import Vue from 'vue'

import 'babel-polyfill'

import 'normalize.css/normalize.css' // A modern alternative to CSS resets
import Element from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

import UIComponents from 'ui-components'
import EnComponents from '@/components'
import VueAwesomeSwiper from 'vue-awesome-swiper'
import 'swiper/dist/css/swiper.css'

import '@/styles/index.scss' // global css
import App from './App'
import router from './router'
import store from './store'

import i18n from './lang' // Internationalization
import './icons' // icon
import './permission' // permission control
// 全局注册echarts、jsonp
import echarts from 'echarts'
import axios from 'axios'
// register global utility filters.
import * as filters from './filters' // global filter
// register global utility mixins.
import mixin from './utils/mixin'

Vue.use(Element, {
  size: 'small',
  i18n: (key, value) => i18n.t(key, value)
})

Vue.use(UIComponents)
Vue.use(EnComponents)
Vue.use(VueAwesomeSwiper)

Vue.prototype.$echarts = echarts
Vue.prototype.$http = axios

Vue.prototype.$message.success = function(text) {
  Vue.prototype.$message({
    showClose: true,
    message: text,
    type: 'success',
    duration: 5000
  })
}
Vue.prototype.$message.error = function(text) {
  Vue.prototype.$message({
    showClose: true,
    message: text,
    type: 'error',
    duration: 5000
  })
}

Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

Vue.mixin(mixin)

Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  template: '<App/>',
  components: { App }
})
