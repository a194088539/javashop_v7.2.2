<template>
  <en-table-layout
    :tableData="tableData.data"
    :loading="loading"
  >
    <div slot="toolbar" class="inner-toolbar">
      <div class="toolbar-btns"></div>
      <div class="toolbar-search">
        <en-table-search
          @search="searchEvent"
          @advancedSearch="advancedSearchEvent"
          advanced
          placeholder="请输入会员名"
          :advancedWidth="450"
        >
          <template slot="advanced-content">
            <el-form ref="advancedForm" :model="advancedForm" label-width="100px">
              <el-form-item label="会员名称">
                <el-input size="medium" v-model="advancedForm.member_name" clearable></el-input>
              </el-form-item>
              <el-form-item label="变更时间">
                <el-date-picker
                  v-model="advancedForm.change_time"
                  type="daterange"
                  :default-time="['00:00:00', '23:59:59']"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="timestamp"
                  style="width: 324px"
                  :picker-options="{ disabledDate (time) { return time.getTime() - 1 >= new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime() + 86400000 - 1 }, shortcuts: MixinPickerShortcuts }"
                >
                </el-date-picker>
              </el-form-item>
            </el-form>
          </template>
        </en-table-search>
      </div>
    </div>
    <template slot="table-columns">
      <el-table-column prop="id" label="日志ID"/>
      <el-table-column prop="member_name" label="会员名称"/>
      <el-table-column prop="money" label="可用金额变更">
        <template slot-scope="{ row }">
          <span v-if="row.money > 0" style="color: #FF605D;font-weight: 600;">￥+{{ row.money | unitPrice }}</span>
          <span v-else style="color: #4AB9B2;font-weight: 600;">￥{{ row.money | unitPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变更时间">
        <template slot-scope="scope">{{ scope.row.time | unixToDate }}</template>
      </el-table-column>
      <el-table-column prop="detail" label="日志描述"/>
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
  import * as API_Member from '@/api/member'

  export default {
    name: 'rechargeList',
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
        tableData: '',

        /** 高级搜索数据 */
        advancedForm: {}
      }
    },
    mounted() {
      this.GET_DepositLogList()
    },
    methods: {
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_DepositLogList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_DepositLogList()
      },

      /** 搜索事件触发 */
      searchEvent(keyword) {
        this.params.member_name = keyword
        Object.keys(this.advancedForm).forEach(key => delete this.params[key])
        this.params.page_no = 1
        this.GET_DepositLogList()
      },

      /** 高级搜索事件触发 */
      advancedSearchEvent() {
        const { advancedForm } = this
        Object.keys(advancedForm).forEach(key => {
          if (advancedForm[key] !== undefined) {
            this.params[key] = advancedForm[key]
          }
        })
        if (!this.params.member_name) delete this.params.member_name

        delete this.params.start_time
        delete this.params.end_time
        if (this.advancedForm.change_time) {
          this.params.start_time = this.advancedForm.change_time[0] / 1000
          this.params.end_time = this.advancedForm.change_time[1] / 1000
        }
        delete this.params.change_time
        this.params.page_no = 1
        this.GET_DepositLogList()
      },

      /** 获取预存款明细 */
      GET_DepositLogList() {
        this.loading = true
        API_Member.getMemberDepositLog(this.params).then(response => {
          this.loading = false
          this.tableData = response
        }).catch(() => { this.loading = false })
      }
    }
  }
</script>
