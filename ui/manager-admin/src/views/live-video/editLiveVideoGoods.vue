<template>
  <div>
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
        <el-table-column prop="seller_name" label="店铺名称" width="200"/>
        <el-table-column label="商品价格" width="200">
          <template slot-scope="scope">{{ scope.row.price | unitPrice('￥') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleDeleteLiveVideoGoods(scope.$index, scope.row)">删除</el-button>
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
  </div>
</template>

<script>
  import * as API_Live from '@/api/live'

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
        tableData: '',

        roomId: this.$route.params.room_id
      }
    },
    watch: {
      $route() {
        if (this.$route.params.room_id) {
          this.roomId = this.$route.params.room_id
          this.GET_LiveVideoGoodsList()
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

      /** 删除直播商品 */
      handleDeleteLiveVideoGoods(index, row) {
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
      },

      /** 获取直播商品列表 */
      GET_LiveVideoGoodsList() {
        this.loading = true
        API_Live.getLiveVideoRoomGoods(this.roomId, this.params).then(response => {
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
