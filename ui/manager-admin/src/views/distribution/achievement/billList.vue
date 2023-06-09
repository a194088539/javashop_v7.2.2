<template>
  <div>
    <en-table-layout
      toolbar
      :tableData="tableData"
      pagination
      :loading="loading">
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns"></div>
        <div class="toolbar-search">
          <en-table-search @search="searchEvent" placeholder="请输入分销商用户名"/>
        </div>
      </div>
      <template slot="table-columns">
        <!--账单号-->
        <el-table-column prop="sn" label="账单号"/>
        <!--开始日期-->
        <el-table-column prop="start_time" :formatter="MixinUnixToDate" label="开始日期"/>
        <!--结束日期-->
        <el-table-column prop="end_time" :formatter="MixinUnixToDate" label="结束日期"/>
        <!--提成金额-->
        <el-table-column label="提成金额">
          <template slot-scope="scope">{{ scope.row.push_money | unitPrice('￥') }}</template>
        </el-table-column>
        <!--订单金额-->
        <el-table-column label="订单金额">
          <template slot-scope="scope">{{ scope.row.order_money | unitPrice('￥') }}</template>
        </el-table-column>
        <!--订单数-->
        <el-table-column prop="order_count" label="订单数" width="60"/>
        <!--退还提成金额-->
        <el-table-column label="退还提成金额">
          <template slot-scope="scope">{{ scope.row.return_push_money | unitPrice('￥') }}</template>
        </el-table-column>
        <!--退还订单金额-->
        <el-table-column label="退还订单金额">
          <template slot-scope="scope">{{ scope.row.return_order_money | unitPrice('￥') }}</template>
        </el-table-column>
        <!--退还订单数-->
        <el-table-column prop="return_order_count" label="退还订单数" width="100"/>
        <!--本期应结-->
        <el-table-column label="本期应结">
          <template slot-scope="scope">{{ scope.row.final_money | unitPrice('￥') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleSeeBillDetails(scope.row)">查看</el-button>
          </template>
        </el-table-column>
      </template>
      <el-pagination
        v-if="tableData"
        slot="pagination"
        @size-change="handlePageSizeChange"
        @current-change="handlePageCurrentChange"
        :current-page="pageData.page_no"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageData.page_size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pageData.data_total">
      </el-pagination>
    </en-table-layout>
  </div>
</template>

<script>
  import * as API_distribution from '@/api/distribution'
  export default {
    name: 'billList',
    data() {
      return {
        // 列表loading状态
        loading: false,

        // 列表参数
        params: {
          page_no: 1,
          page_size: 10,
          total_id: this.$route.query.total_id
        },
        // 列表数据
        tableData: [],

        pageData: {}
      }
    },
    mounted() {
      this.GET_BillList()
    },
    activated() {
      delete this.params.uname
      this.params = {
        ...this.params,
        ...this.$route.query
      }
      this.GET_BillList()
    },
    beforeRouteUpdate(to, from, next) {
      delete this.params.uname
      this.params = {
        ...this.params,
        ...this.$route.query
      }
      this.GET_BillList()
      next()
    },
    methods: {
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_BillList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_BillList()
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          uname: data
        }
        this.GET_BillList()
      },

      /** 获取账单列表 */
      GET_BillList() {
        this.loading = true
        this.params.total_id = this.$route.query.total_id
        API_distribution.getBillList(this.params).then(response => {
          this.loading = false
          this.tableData = response.data
          this.pageData = {
            page_no: response.page_no,
            page_size: response.page_size,
            data_total: response.data_total
          }
        })
      },

      /** 查看结算单详情 */
      handleSeeBillDetails(row) {
        this.$router.push({ name: 'billDetails', params: { bill_id: row.id, member_id: row.member_id }})
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
    padding: 0 20px;
  }
</style>

