/**
 * 注册全局组件
 */

import Vue from 'vue'
import * as components from '@/components'
import Router from 'vue-router'

Vue.use(Router)
// 避免路由重复报错
const emptyFn = () => {}
const originalPush = Router.prototype.push
Router.prototype.push = function push (location, onComplete = emptyFn, onAbort) {
  return originalPush.call(this, location, onComplete, onAbort)
}

Object.keys(components).forEach(key => {
  Vue.component(components[key].name, components[key])
})
