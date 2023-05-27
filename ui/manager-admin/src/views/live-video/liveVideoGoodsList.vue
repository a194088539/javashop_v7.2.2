<template>
  <en-table-layout
    :tableData="tableData.data"
    :loading="loading"
  >
    <div slot="toolbar" class="inner-toolbar">
      <div class="toolbar-btns"></div>
      <div class="toolbar-search">
        <en-table-search @search="searchEvent" placeholder="请输入关键字"/>
      </div>
    </div>
    <template slot="table-columns">
      <el-table-column label="商品名称">
        <template slot-scope="scope">
          <div class="goods-info">
            <div class="goods-image" style="margin: 0 20px;">
              <a :href="`${MixinBuyerDomain}/goods/${scope.row.goods_id}`" target="_blank">
                <img :src="scope.row.img_show_url"/>
              </a>
            </div>
            <div class="goods-name-box">
              <div class="goods-name">
                <a :href="`${MixinBuyerDomain}/goods/${scope.row.goods_id}`" target="_blank" style="color: #00a2d4;"><span v-html="scope.row.name">{{ scope.row.name }}</span></a>
              </div>
              <span>商品编号:{{scope.row.sn}}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="规格SKU">
        <template slot-scope="scope">
          <span>{{ getSkuList(scope.row.specs) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="seller_name" label="店铺名称"/>
      <el-table-column label="商品价格">
        <template slot-scope="scope">{{ scope.row.price | unitPrice('￥') }}</template>
      </el-table-column>
      <el-table-column label="商品状态">
        <template slot-scope="scope">{{ scope.row.audit_status | filterStatus }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            @click="handleDeleteLiveVideoGoods(scope.$index, scope.row)">{{ scope.row.audit_status === 1 ? '撤回' : '删除'}}</el-button>
        </template>
      </el-table-column>
    </template>
    <el-pagination
      v-if="tableData"
      slot="pagination"
      @size-change="handlePageSizeChange"
      @current-change="handlePageCurrentChange"
      :current-page="tableData.page_no"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="tableData.page_size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="tableData.data_total">
    </el-pagination>
  </en-table-layout>
</template>

<script>
  import * as API_goods from '@/api/goods'
  import * as API_Live from '@/api/live'
  import { RegExp } from '~/ui-utils'

  export default {
    name: 'liveVideoGoodsList',
    data() {
      return {
        /** 列表loading状态 */
        loading: false,

        /** 列表参数 */
        params: {
          page_no: 1,
          page_size: 10
        },

        /** 商品列表数据 */
        tableData: ''
      }
    },
    filters: {
      /** 商品状态 */
      filterStatus(val) {
        switch (val) {
          case 0: return '未审核'
          case 1: return '审核中'
          case 2: return '审核通过'
          case 3: return '审核失败'
          case 4: return '已撤回'
        }
      }
    },
    mounted() {
      this.GET_LiveVideoGoodsList()
    },
    methods: {
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_LiveVideoGoodsList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_LiveVideoGoodsList()
      },

      /** 搜索事件触发 */
      searchEvent(name) {
        this.params = {
          ...this.params,
          page_no: 1,
          name: name
        }
        if (!name) delete this.params.name
        this.GET_LiveVideoGoodsList()
      },

      /** 撤回、删除直播商品 */
      handleDeleteLiveVideoGoods(index, row) {
        if (row.audit_status === 1) {
          this.$confirm('确定撤回此商品，是否继续？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            API_Live.resetuditLiveVideoGoods(row.id).then(() => {
              this.GET_LiveVideoGoodsList()
              this.$message.success('撤回成功！')
            })
          })
        } else {
          this.$confirm('确定删除此商品，是否继续？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            API_Live.deleteLiveVideoGoods(row.id).then(() => {
              this.GET_LiveVideoGoodsList()
              this.$message.success('删除成功！')
            })
          })
        }
      },

      /** 获取规格列表 */
      getSkuList(val) {
        const _val = typeof val === 'string' ? JSON.parse(val) : val
        if (_val) {
          let _specs = []
          _val.forEach(elem => {
            _specs.push(elem.spec_value)
          })
          _specs = _specs.join('/')
          return _specs
        }
        return '/'
      },

      /** 获取直播商品列表 */
      GET_LiveVideoGoodsList() {
        this.loading = true
        API_Live.getLiveVideoGoodsList(this.params).then(response => {
          this.loading = false
          this.tableData = response
        }).catch(() => (this.loading = false))
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  /deep/ .el-table td:not(.is-left) {
    text-align: center;
  }

  .inner-toolbar {
    display: flex;
    width: 100%;
    justify-content: space-between;
  }

  .toolbar-search {
    margin-right: 10px;
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
        a:hover {
          color: #f42424 !important;
        }
      }
    }
  }
</style>
