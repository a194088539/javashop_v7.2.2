
<template>
  <el-table :data="skuList" style="width: 100%">
    <el-table-column label="商品列表" align="center" width="100">
      <template slot-scope="scope">
        <a :href="'/goods/' + scope.row.goods_id" target="_blank">
          <img :src="scope.row[image]"  class="goods-image">
        </a>
      </template>
    </el-table-column>
    <el-table-column label="商品名称" align="center">
      <template slot-scope="scope">
        <div style="display: inline-block">
          <a :href="'/goods/' + scope.row.goods_id" target="_blank" class="goods-name">
            <span v-if="order_type === 'PINTUAN'" class="assemble-color">多人拼团</span>
            <span v-html="scope.row[name]">{{ scope.row[name] }}</span>
          </a>
          <p v-if="scope.row.spec_list" class="sku-spec">{{ scope.row | formatterSkuSpec }}</p>
          <p v-if="scope.row.promotion_tags && scope.row.promotion_tags.length">
            <span class="sku-act-tag" v-for="(tag, index) in scope.row.promotion_tags" :key="index">{{ tag }}</span>
          </p>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="商品单价" align="center" width="120">
      <template slot-scope="scope">{{ scope.row[price] | unitPrice('￥') }}</template>
    </el-table-column>
    <el-table-column label="数量" align="center" width="90">
      <template slot-scope="scope">{{ scope.row[num] }}</template>
    </el-table-column>
    <el-table-column label="商品小计" align="center" width="120">
      <template slot-scope="scope">{{ (scope.row[total] || (scope.row[num] * scope.row[price])) | unitPrice('￥') }}</template>
    </el-table-column>
  </el-table>
</template>

<script>
  export default {
    name: 'member-sku-list',
    props: {
      order_type: {
        type: String
      },
      skuList: {
        type: Array
      },
      image: {
        default: 'goods_image'
      },
      name: {
        default: 'goods_name'
      },
      price: {
        default: 'price'
      },
      num: {
        default: 'num'
      },
      total: {
        default: 'total'
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  /deep/ .el-table__header th:first-child .cell {
    font-size: 16px;
    font-weight: bold;
  }
  .el-table {
    border: 1px solid #ebeef5;
    border-bottom: none;
    font-size: 12px;
  }
  .goods-name {
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
    overflow: hidden;
    .assemble-color {
      color: $color-main;
      border: 1px solid $color-main;
      border-radius: 3px;
    }
  }
  .goods-image {
    width: 50px;
    height: 50px;
  }
  .sku-act-tag {
    display: inline-block;
    padding: 0 5px;
    line-height: 15px;
    margin-right: 5px;
    border: 1px solid $color-main;
    color: $color-main
  }
</style>
