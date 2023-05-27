/**
 * 分类选择组件
 * 随选随用
 */
import Vue from 'vue'
import CategoryPicker from './src/main'

CategoryPicker.install = function() {
  Vue.component(CategoryPicker.name, CategoryPicker)
}

export default CategoryPicker
