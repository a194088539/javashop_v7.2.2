<template>
  <div class="floor-layout tpl-30">
    <div class="layout-main">
      <layout-item :block="data.blockList[0]" @handle-edit="handleEditBlock(0)"/>
    </div>
  </div>
</template>

<script>
  import mixin from '../mixin'

  export default {
    name: 'tpl_30',
    mixins: [mixin],
    title: '标题或间隔性模块',
    dataTpl: {
      tpl_id: 30,
      blockList: [
        { block_type: 'IMAGE', block_value: '', block_opt: '' }
      ]
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
 .layout-item{
   border-top:1px solid #ebebeb;
   border-bottom:1px solid #ebebeb;
 }
</style>
