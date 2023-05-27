import Vue from 'vue'
import TreeList from './treeList'

TreeList.install = function() {
  Vue.component(TreeList.name, TreeList)
}

export default TreeList
