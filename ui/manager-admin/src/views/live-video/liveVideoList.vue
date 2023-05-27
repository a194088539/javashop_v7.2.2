<template>
  <div>
    <en-table-layout
      :tableData="tableData.data"
      :loading="loading"
    >
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
        </div>
        <div class="toolbar-search">
          <en-table-search @search="searchEvent" placeholder="请输入主播微信号"/>
        </div>
      </div>
      <template slot="table-columns">
        <!--直播ID-->
        <el-table-column label="直播ID">
          <template slot-scope="scope">
            <span>{{ scope.row.we_chat_room_id || '-' }}</span>
          </template>
        </el-table-column>
        <!--直播时间-->
        <el-table-column label="直播时间" width="350">
          <template slot-scope="scope">
            <span>{{ scope.row.start_time | unixToDate }}</span>至
            <span>{{ scope.row.end_time | unixToDate }}</span>
          </template>
        </el-table-column>
        <!--直播间名称-->
        <el-table-column prop="room_name" label="直播间名称"/>
        <!--店铺名称-->
        <el-table-column prop="seller_name" label="店铺名称"/>
        <!--主播微信号-->
        <el-table-column prop="anchor_we_chat" label="主播微信号"/>
        <!--直播状态-->
        <el-table-column label="直播状态">
          <template slot-scope="scope">{{ scope.row.status | filterStatus }}</template>
        </el-table-column>
        <!--操作-->
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              :disabled="scope.row.status === '103' || !scope.row.we_chat_room_id"
              @click="handleEditGoods(scope.$index, scope.row)">编辑商品</el-button>
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
    name: 'liveVideoList',
    data() {
      return {
        // 列表loading状态
        loading: false,
        // 列表参数
        params: {
          page_no: 1,
          page_size: 10
        },
        // 列表数据
        tableData: ''
      }
    },
    filters: {
      /** 直播状态 */
      filterStatus(val) {
        switch (val) {
          case '101': return '直播中'
          case '102': return '未开始'
          case '103': return '已结束'
          case '104': return '禁播'
          case '105': return '暂停中'
          case '106': return '异常'
          case '107': return '已过期'
        }
      }
    },
    mounted() {
      this.GET_LiveVideoList()
    },
    methods: {
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_LiveVideoList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_LiveVideoList()
      },

      /** 搜索事件触发 */
      searchEvent(anchor_we_chat) {
        this.params.page_no = 1
        this.params = {
          ...this.params,
          page_no: 1,
          anchor_we_chat: anchor_we_chat
        }
        if (!anchor_we_chat) delete this.params.anchor_we_chat
        this.GET_LiveVideoList()
      },

      /** 编辑商品 */
      handleEditGoods(index, row) {
        this.$router.push({ name: 'editLiveVideoGoods', params: { room_id: row.we_chat_room_id, callback: this.GET_LiveVideoList }})
      },

      /** 获取直播列表 */
      GET_LiveVideoList() {
        this.loading = true
        const { params } = this
        Object.keys(params).forEach(key => {
          if (!params[key] && params[key] !== 0) {
            delete params[key]
          }
        })
        API_Live.getLiveVideoRoomList(params).then(response => {
          this.loading = false
          this.tableData = response
        }).catch(() => { this.loading = false })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  
</style>

