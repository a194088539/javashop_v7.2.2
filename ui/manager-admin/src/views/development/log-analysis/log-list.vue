<template>
  <div id="log-list">
    <en-table-layout
      :toolbar="false"
      :tableData="servicesData"
    >
      <template slot="table-columns">
        <el-table-column prop="name" label="服务名"/>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              style="background-color: #f5f5f5"
              @click="handleLogDetails(scope.row.name)">查看日志详情</el-button>
          </template>
        </el-table-column>
      </template>
    </en-table-layout>
  </div>
</template>

<script>
  import * as API_Logs from '@/api/logs'
  export default {
    name: 'logList',
    data() {
      return {
        servicesData: []
      }
    },
    mounted() {
      this.GET_LogServices()
    },
    methods: {
      handleLogDetails(name) {
        this.$router.push({ path: `/development/log-analysis/log-detail?service_name=${name}` })
      },
      GET_LogServices() {
        API_Logs.getLogServices().then(response => {
          this.servicesData = response
        })
      }
    }
  }
</script>
