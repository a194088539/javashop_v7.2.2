/**
 * Created by Andste on 2018/5/13.
 * 层级选择器，提供基础功能
 */

import Vue from 'vue'
import GradeEditor from './src/main'

GradeEditor.install = function() {
  Vue.component(GradeEditor.name, GradeEditor)
}

export default GradeEditor
