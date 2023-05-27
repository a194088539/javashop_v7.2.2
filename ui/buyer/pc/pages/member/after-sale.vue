<template>
  <div id="after-sale">
    <div class="member-nav">
      <ul class="member-nav-list">
        <li class="active">
          <nuxt-link to="/member/after-sale">申请售后服务</nuxt-link>
        </li>
        <li>
          <nuxt-link to="/member/service-record">售后服务记录</nuxt-link>
        </li>
        <li>
          <nuxt-link to="/member/refund-record">退款明细</nuxt-link>
        </li>
      </ul>
    </div>
    <div class="after-search">
      <template>
        <el-date-picker
          v-model="after_time_range"
          type="daterange"
          align="center"
          :editable="false"
          unlink-panels
          :default-time="['00:00:00', '23:59:59']"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :picker-options="{ disabledDate (time) { return time.getTime() - 1 >= new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime() + 86400000 - 1 }, shortcuts: MixinPickerShortcuts }">
        </el-date-picker>
      </template>
      <input type="text" v-model="params.key_words" placeholder="请输入订单编号" @keyup.enter="Search_GET_OrderList">
      <button type="button" @click="Search_GET_OrderList">搜索</button>
      <span v-if="orderList">搜到：<em>{{ orderList.data_total }}</em> 笔订单</span>
      <span v-else>搜索中...</span>
    </div>
    <template v-if="orderList && orderList.data.length">
      <div class="mod-main mod-comm">
        <div class="mc">
          <table class="tb-void tb-top">
            <colgroup>
              <col width="130">
              <col width="">
              <col width="260">
            </colgroup>
            <thead>
              <tr>
                <th>订单编号</th>
                <th>订单商品</th>
                <th>下单时间</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="order in orderList.data" :key="order.sn">
                <td>
                  <nuxt-link :to="'/member/my-order/detail?order_sn=' + order.sn" target="_blank" class="order-sn">{{ order.sn }}</nuxt-link>
                </td>
                <td>
                  <div class="list-h">
                    <ul>
                      <li style="width:auto;" v-for="sku in order.sku_list" :key="sku.sku_id">
                        <div class="p-img">
                          <a :href="'/goods/' + sku.goods_id" target="_blank">
                            <img width="50" height="50" :src="sku.goods_image" :title="sku.name">
                          </a>
                        </div>
                        <div class="btns" v-if="sku.goods_operate_allowable_vo.allow_apply_service">
                          <nuxt-link :to="'/member/apply-service?order_sn=' + order.sn + '&sku_id=' + sku.sku_id" class="btn btn-5">申请</nuxt-link>
                        </div>
                      </li>
                    </ul>
                  </div>
                </td>
                <td>
                  <div class="ftx03">{{ order.create_time | unixToDate }}</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="member-pagination" v-if="orderList">
        <el-pagination
          @current-change="handleCurrentPageChange"
          :current-page.sync="params.page_no"
          :page-size="params.page_size"
          layout="total, prev, pager, next"
          :total="orderList.data_total">
        </el-pagination>
      </div>
    </template>
    <empty-member v-else>暂无可申请售后的订单</empty-member>
  </div>
</template>

<script>
  import * as API_Order from '@/api/order'
  import Vue from 'vue'
  import { DatePicker } from 'element-ui'
  Vue.use(DatePicker)
  export default {
    name: 'after-sale',
    head() {
      return {
        title: `申请售后服务-${this.site.title}`
      }
    },
    data() {
      return {
        orderList: '',
        params: {
          page_no: 1,
          page_size: 10,
          key_words: ''
        },
        after_time_range: [],
        // 日期选择器快捷选项
        MixinPickerShortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      }
    },
    mounted() {
      this.GET_Orders()
    },
    methods: {
      /** 当前页数发生改变 */
      handleCurrentPageChange(page) {
        this.params.page_no = page;
        this.GET_Orders()
      },
      /** 搜索获取数据 */
      Search_GET_OrderList(){
        this.params.page_no = 1
        if (this.after_time_range && this.after_time_range.length) {
          this.params.start_time = Math.floor(this.after_time_range[0].getTime() / 1000)
          this.params.end_time = Math.floor(this.after_time_range[1].getTime() / 1000)
        } else {
          this.params.start_time = this.params.end_time = ''
        }
        this.GET_Orders()
      },
      /** 获取我的订单数据 */
      GET_Orders() {
        // 去除参数中为空的选项
        let params = {}
        for (let key in this.params) {
          if (this.params[key]) {
            params[key] = this.params[key]
          }
        }
        API_Order.getOrderList(params).then(response => {
          this.orderList = response;
          this.MixinScrollToTop()
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../../assets/styles/color";
  /deep/ .el-input__inner {
    height: 27px;
    line-height: 27px;
    width:250px;
    border: 1px solid #ccc;
  }
  /deep/ .el-date-editor {
    .el-range-separator{
      line-height: 19px;
    }
    .el-range__icon{
      line-height: 23px;
    }
  }
  /deep/ .el-range__close-icon{
    line-height:23px !important;
  }
  .p-detail, .p-img, .p-market, .p-name, .p-price {
    overflow: hidden;
  }
  .ftx-03, .ftx03 {
    color: #999;
  }
  table {
    border-collapse: collapse;
  }
  .after-search {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    width: 100%;
    height: 44px;
    border-bottom: 1px solid #e7e7e7;
    em {
      color: $color-main;
    }
    input {
      border: 1px solid #ccc;
      padding: 5px 15px;
      height: 15px;
      width: 150px;
      color: #333;
      border-radius: 3px;
      transition: border .2s ease-out;
      margin-left: 10px;
      &:focus {
        border-color: darken($color-main, 75%);
      }
    }
    button {
      width: 80px;
      background-color: #e7e7e7;
      color: #333;
      cursor: pointer;
      line-height: 27px;
      margin-left: 10px;
      margin-right: 10px;
      transition: background-color .2s ease-out;
      border-radius: 3px;
      &:hover {
        background-color: #d3d3d3;
      }
    }
  }
  .mod-main {
    padding: 10px;
    background-color: #fff;
    margin-bottom: 20px;
  }
  .mod-comm {
    padding: 10px 20px 20px;
  }
  .mc {
    zoom: 1;
    overflow: visible;
    line-height: 20px;
  }
  .mod-main .order-sn {
    &:hover {
      color: #e4393c;
    }
  }
  .tb-void {
    line-height: 18px;
    text-align: center;
    border: 1px solid #f2f2f2;
    border-top: 0;
    color: #333;
    width: 100%;
  }
  .tb-void th {
    background: #f5f5f5;
    height: 32px;
    line-height: 32px;
    padding: 0 5px;
    text-align: center;
    font-weight: 400;
  }
  .mod-main .tb-void td {
    border: 1px solid #f2f2f2;
    padding: 10px;
  }
  .mod-main .tb-void a {
    color: #005ea7;
  }
  .mod-main .list-h {
    overflow: hidden;
    zoom: 1;
  }
  .mod-main .list-h li {
    float: left;
    padding: 0 10px;
    width: 60px;
  }
  .mod-main .btns a {
    display: block;
    width: 50px;
    height: 25px;
    line-height: 25px;
    border: 1px solid #bfd6af;
    text-align: center;
    background: #f5fbef;
    color: #666;
    cursor: pointer;
    margin-bottom: 5px;
    &:first-child {
      margin-top: 5px;
    };
  }

</style>
