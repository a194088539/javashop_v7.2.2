<template>
  <div>
    <en-table-layout
      toolbar
      pagination
      :tableData="tableData"
      :loading="loading">
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <!--商品类型-->
          <div v-if="parseInt(shopInfo.self_operated) === 1" class="conditions">
            <span>商品类型：</span>
            <el-select
              class="choose-machine"
              v-model="params.goods_type"
              placeholder="请选择商品类型"
              @change="changeGoodsType"
              clearable>
              <el-option key="NORMAL" label="正常商品" value="NORMAL"/>
              <el-option key="POINT" label="积分商品" value="POINT"/>
            </el-select>
          </div>
          <!--商品分组 获取分组列表-->
          <div class="conditions">
            <span>店铺分组：</span>
            <en-category-picker class="choose-machine" size="mini" @changed="changeGoodsCateGory" :clearable='true'/>
          </div>
        </div>
        <div class="toolbar-search">
          <en-table-search @search="searchEvent" placeholder="请输入商品名称或编号" />
        </div>
      </div>
      <template slot="table-columns">
        <el-table-column label="商品名称" min-width="450">
          <template slot-scope="scope">
            <div class="goods-info">
              <div class="goods-image" style="margin: 0 20px;">
                <a :href="`${MixinBuyerDomain}/goods/${scope.row.goods_id}`" target="_blank">
                  <img :src="scope.row.thumbnail"/>
                </a>
              </div>
              <div class="goods-name-box">
                <div class="goods-name">
                  <a :href="`${MixinBuyerDomain}/goods/${scope.row.goods_id}`" target="_blank" style="color: #00a2d4;"><span v-html="scope.row.goods_name">{{ scope.row.goods_name }}</span></a>
                </div>
                <span>商品编号:{{scope.row.sn}}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="150">
          <template slot-scope="scope">{{ scope.row.price | unitPrice('￥') }}</template>
        </el-table-column>
        <el-table-column label="实际库存" width="150">
          <template slot-scope="scope">{{ scope.row.quantity }}</template>
        </el-table-column>
        <el-table-column label="可用库存" width="150">
          <template slot-scope="scope">{{ scope.row.enable_quantity }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
          <template slot-scope="scope">{{ scope.row.create_time | unixToDate }}</template>
        </el-table-column>
        <el-table-column prop="market_enable" label="状态" :formatter="marketStatus" width="150"/>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              type="primary"
              @click="handleWithdraw(scope.row)">查看
            </el-button>
          </template>
        </el-table-column>
      </template>

      <el-pagination
        slot="pagination"
        v-if="pageData"
        @size-change="handlePageSizeChange"
        @current-change="handlePageCurrentChange"
        :current-page="pageData.page_no"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageData.page_size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageData.data_total">
      </el-pagination>
    </en-table-layout>
    <el-dialog title="库存信息" center :visible.sync="goodsWarningStockshow" width="40%" class="popinfo">
      <en-table-layout :tableData="goodsWarningStockDate" :loading="loading">
        <template slot="table-columns">
          <el-table-column prop="sn" label="货号"/>
          <el-table-column prop="goods_name" label="商品名称"/>
          <el-table-column prop="quantity" label="实际库存"/>
          <el-table-column prop="enable_quantity" label="可用库存"/>
        </template>
      </en-table-layout>
    </el-dialog>
  </div>
</template>

<script>
  import * as API_goods from '@/api/goods'
  import { CategoryPicker } from '@/components'

  export default {
    name: 'understock',
    components: {
      [CategoryPicker.name]: CategoryPicker
    },
    data() {
      return {
        /** 列表loading状态 */
        loading: false,

        /** 列表参数 */
        params: {
          page_no: 1,
          page_size: 10
        },

        /** 列表数据 */
        tableData: [],

        /** 列表分页数据 */
        pageData: [],

        /** 预警商品库存信息显示*/
        goodsWarningStockshow: false,

        /** 预警商品库存信息*/
        goodsWarningStockDate: null,
        /** 店铺信息 */
        shopInfo: this.$store.getters.shopInfo
      }
    },
    mounted() {
      this.GET_WarningGoodsList()
    },
    methods: {

      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_WarningGoodsList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_WarningGoodsList()
      },

      /** 切换商品类型 */
      changeGoodsType(val) {
        delete this.params.goods_type
        if (val !== '') {
          this.params = {
            ...this.params,
            goods_type: val
          }
        }
        this.GET_WarningGoodsList()
      },

      /** 切换分组*/
      changeGoodsCateGory(data) {
        delete this.params.shop_cat_path
        if (data && Array.isArray(data) && data.length !== 0) {
          this.params = {
            ...this.params,
            shop_cat_path: '0|' + data.join('|') + '|'
          }
        }
        this.GET_WarningGoodsList()
      },
      /** 销售状态格式化 */
      marketStatus(row) {
        switch (row.is_auth) {
          case 0 : return row.market_enable === 1 ? '待审核' : '已下架'
          case 1 : return row.market_enable === 1 ? '售卖中' : '已下架'
          case 2 : return row.market_enable === 1 ? '审核拒绝' : '已下架'
        }
      },
      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          page_no: 1,
          keyword: data
        }
        this.GET_WarningGoodsList()
      },

      GET_WarningGoodsList() {
        this.loading = true
        API_goods.getWarningGoodsList(this.params).then(response => {
          this.loading = false
          this.pageData = {
            page_no: response.page_no,
            page_size: response.page_size,
            data_total: response.data_total
          }
          this.tableData = response.data
        })
      },

      /** 查看库存信息 */
      handleWithdraw(row) {
        this.goodsWarningStockshow = true
        API_goods.getWarningGoodsStockList(row.goods_id).then((response) => {
          this.goodsWarningStockDate = response
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  /deep/ div.toolbar {
    height: 70px;
    padding: 20px 0;
  }

  /deep/ .el-table td:not(.is-left) {
    text-align: center;
  }

  .inner-toolbar {
    display: flex;
    width: 100%;
  }

  .toolbar-search {
    margin-right: 10px;
  }

  div.toolbar-btns {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    div {
      span {
        display: inline-block;
        font-size: 14px;
        color: #606266;
      }
    }
    .conditions {
      display: flex;
      flex-direction: row;
      flex-wrap: nowrap;
      justify-content: flex-start;
      align-items: center;
      min-width: 24.5%;
      .choose-machine {
        width: 60%;
      }
    }
  }
  
  /deep/ .popinfo {
    .toolbar {
      display: none;
    }
    .el-dialog__body {
      border: 1px solid #e5e5e5;
      .el-table {
        border: 1px solid #e5e5e5;
      }
    }
  }

  .goods-info {
    display: flex;
    align-items: center;
    .goods-image {
      img {
        width: 80px;
        height: 80px;
      }
    }
    .goods-name-box {
      text-align: left;
      .goods-name {
        display: -webkit-box;
        -webkit-box-orient: vertical;
        -webkit-line-clamp: 2;
        overflow: hidden;
      }
    }
  }

</style>
