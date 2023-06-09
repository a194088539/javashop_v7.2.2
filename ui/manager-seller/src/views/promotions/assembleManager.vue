<template>
  <div>
    <en-table-layout
      toolbar
      pagination
      :tableData="tableData"
      :loading="loading">

      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <el-button type="primary" @click="handleAssemble">新增</el-button>
        </div>
        <div class="toolbar-search">
          <en-table-search
            @search="searchEvent"
            @advancedSearch="advancedSearchEvent"
            advanced
            advancedWidth="465"
            placeholder="请输入活动名称"
          >
            <template slot="advanced-content">
              <el-form ref="advancedForm" :model="advancedForm" label-width="80px">
                <el-form-item label="活动名称">
                  <el-input size="medium" v-model="advancedForm.name" clearable></el-input>
                </el-form-item>
                <el-form-item label="活动状态">
                  <el-select v-model="advancedForm.status" placeholder="请选择">
                    <el-option label="全部" value=""/>
                    <el-option label="未开始" value="WAIT"/>
                    <el-option label="进行中" value="UNDERWAY"/>
                    <el-option label="已结束" value="END"/>
                  </el-select>
                </el-form-item>
                <el-form-item label="活动时间">
                  <el-date-picker
                    v-model="advancedForm.act_time_range"
                    type="daterange"
                    align="center"
                    :editable="false"
                    unlink-panels
                    :default-time="['00:00:00', '23:59:59']"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="timestamp"
                    :picker-options="{ disabledDate (time) { return time.getTime() - 2592e6 - 1 >= new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime() + 86400000 - 1 }, shortcuts: MixinPickerShortcuts }">
                  </el-date-picker>
                </el-form-item>
              </el-form>
            </template>
          </en-table-search>
        </div>
      </div>

      <template slot="table-columns">
        <!--活动名称-->
        <el-table-column prop="promotion_name" label="活动名称"/>
        <!--活动标题-->
        <el-table-column prop="promotion_title" label="活动标题"/>
        <!--起止时间-->
        <el-table-column label="起止时间" width="300">
          <template slot-scope="scope">
            <span>{{ scope.row.start_time | unixToDate }}</span> ～
            <span>{{ scope.row.end_time | unixToDate }}</span>
          </template>
        </el-table-column>
        <!--参团人数-->
        <el-table-column prop="required_num" width="120" label="参团人数"/>
        <!--活动状态-->
        <el-table-column prop="status" width="80" :formatter="assembleStatus" label="活动状态"/>
        <!--限购数量-->
        <el-table-column prop="limit_num" width="120" label="限购数量"/>
        <!--是否虚拟成团-->
        <el-table-column prop="enable_mocker" width="120" :formatter="assVirtual" label="虚拟成团"/>
        <!--操作-->
        <el-table-column label="操作" width="230">
          <template slot-scope="scope">
            <el-button
              :disabled="scope.row.status === 'END'"
              type="success"
              @click="handleAssemble(scope.row)">修改
            </el-button>
            <el-button
              :disabled="scope.row.status === 'END'"
              type="primary"
              @click="handleMangerAssemble(scope.row)">管理
            </el-button>
            <el-button
              type="danger"
              @click="handleDeleteAssemble(scope.row)">删除
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
  </div>
</template>

<script>
  import { mapGetters } from 'vuex'
  import * as API_activity from '@/api/activity'

  export default {
    name: 'assembleManager',
    computed: {
      ...mapGetters([
        'shopInfo'
      ])
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

        /** 列表数据*/
        tableData: [],

        /** 列表分页数据 */
        pageData: null,

        // 是否不可编辑 即是否是进行中 或者已失效状态 默认可以
        editEnabled: true,

        /** 高级搜索数据 */
        advancedForm: {}
      }
    },
    mounted() {
      this.GET_AssembleActivityList()
    },
    methods: {
      assVirtual(row) {
        return row.enable_mocker === 1 ? '开启' : '关闭'
      },

      /** 拼团状态显示 */
      assembleStatus({ status }) {
        switch (status) {
          case 'WAIT': return '未开始'
          case 'UNDERWAY': return '进行中'
          case 'END': return '已结束'
        }
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params.page_no = 1
        this.params.name = data
        if (!data) delete this.params.name
        this.GET_AssembleActivityList()
      },

      /** 高级搜索事件触发 */
      advancedSearchEvent() {
        this.params = {
          ...this.params,
          ...this.advancedForm
        }
        delete this.params.start_time
        delete this.params.end_time
        const { act_time_range } = this.advancedForm
        if (act_time_range) {
          this.params.start_time = parseInt(act_time_range[0] /= 1000)
          this.params.end_time = parseInt(act_time_range[1] /= 1000)
        }
        delete this.params.act_time_range
        this.params.page_no = 1
        this.GET_AssembleActivityList()
      },

      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_AssembleActivityList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_AssembleActivityList()
      },

      /** 获取活动列表 */
      GET_AssembleActivityList() {
        this.loading = true
        API_activity.getAssembleActivityList(this.params).then(response => {
          this.loading = false
          this.tableData = response.data
          this.pageData = {
            page_no: response.page_no,
            page_size: response.page_size,
            data_total: response.data_total
          }
        })
      },

      /** 新增/编辑活动 */
      handleAssemble(row) {
        this.$router.push({ name: 'assemble', params: { promotion_id: row.promotion_id, callback: this.GET_AssembleActivityList }})
      },

      /** 删除活动 */
      handleDeleteAssemble(row) {
        this.$confirm('确认删除当前活动？', '确认信息', { type: 'warning' }).then(() => {
          API_activity.delAssembleActivity(row.promotion_id).then(() => {
            this.$message.success('删除成功！')
            this.GET_AssembleActivityList()
          })
        }).catch(() => {})
      },

      /** 管理活动商品 */
      handleMangerAssemble(row) {
        this.$router.push({ name: 'assembleGoods', params: { promotion_id: row.promotion_id }, query: {
          promotion_name: row.promotion_name,
          start_time: row.start_time,
          end_time: row.end_time
        }})
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  /deep/ div.toolbar {
    height: 70px;
    padding: 15px;
  }
</style>



