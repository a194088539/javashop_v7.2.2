<template>
  <div>
    <en-table-layout
      :tableData="tableData.data"
      :loading="loading"
      :tips="true">
      <div slot="tips">
        <h4>操作提示</h4>
        <ul>
          <li>导出时，会按右侧搜索中的筛选条件导出全部数据。如果不需要，请先清空筛选条件！</li>
        </ul>
      </div>
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <el-button size="mini" type="primary" icon="el-icon-download" @click="handleExportOrders" style="margin-left: 5px">导出Excel</el-button>
        </div>
        <div class="toolbar-search">
          <en-table-search
            @search="searchEvent"
            @advancedSearch="advancedSearchEvent"
            advanced
            advancedWidth="465"
            placeholder="请输入订单编号"
          >
            <template slot="advanced-content">
              <el-form ref="advancedForm" :model="advancedForm" label-width="80px">
                <el-form-item label="收货人">
                  <el-input size="medium" v-model="advancedForm.ship_name" clearable></el-input>
                </el-form-item>
                <el-form-item label="商品名称">
                  <el-input size="medium" v-model="advancedForm.goods_name" clearable></el-input>
                </el-form-item>
                <el-form-item label="店铺名称">
                  <en-shop-picker @changed="(shop) => { advancedForm.seller_id = shop.shop_id }"/>
                </el-form-item>
                <el-form-item label="下单日期">
                  <el-date-picker
                    v-model="advancedForm.order_time_range"
                    type="daterange"
                    align="center"
                    :editable="false"
                    unlink-panels
                    :default-time="['00:00:00', '23:59:59']"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="timestamp"
                    :picker-options="{ disabledDate (time) { return time.getTime() - 1 >= new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate()).getTime() + 86400000 - 1 }, shortcuts: MixinPickerShortcuts }">
                  </el-date-picker>
                <!-- (time) { return time.getTime() > Date.now() }-->
                </el-form-item>
                <el-form-item label="订单状态">
                  <el-select v-model="advancedForm.order_status" placeholder="请选择" clearable>
                    <el-option label="新订单" value="NEW"/>
                    <el-option label="已确认" value="CONFIRM"/>
                    <el-option label="已付款" value="PAID_OFF"/>
                    <el-option label="已发货" value="SHIPPED"/>
                    <el-option label="已收货" value="ROG"/>
                    <el-option label="已完成" value="COMPLETE"/>
                    <el-option label="已取消" value="CANCELLED"/>
                  </el-select>
                </el-form-item>
                <el-form-item label="支付方式">
                  <el-select v-model="advancedForm.payment_type" placeholder="请选择" clearable>
                    <el-option label="在线支付" value="ONLINE"/>
                    <el-option label="货到付款" value="COD"/>
                  </el-select>
                </el-form-item>
                <el-form-item label="订单来源">
                  <el-select v-model="advancedForm.client_type" placeholder="请选择" clearable>
                    <el-option label="PC" value="PC"/>
                    <el-option label="WAP" value="WAP"/>
                    <el-option label="APP" value="APP"/>
                    <el-option label="小程序" value="MINI"/>
                  </el-select>
                </el-form-item>
              </el-form>
            </template>
          </en-table-search>
        </div>
      </div>
      <template slot="table-columns">
        <!--订单编号-->
        <el-table-column prop="sn" label="订单编号"/>
        <!--下单时间-->
        <el-table-column prop="create_time" :formatter="MixinUnixToDate" label="下单时间"/>
        <!--订单总额-->
        <el-table-column label="订单总额">
          <template slot-scope="scope">{{ scope.row.order_amount | unitPrice('￥') }}</template>
        </el-table-column>
        <!--收货人-->
        <el-table-column prop="ship_name" label="收货人"/>
        <!--订单状态-->
        <el-table-column prop="order_status_text" label="订单状态"/>
        <!--付款状态-->
        <el-table-column prop="pay_status_text" label="付款状态"/>
        <!--发货状态-->
        <el-table-column prop="ship_status_text" label="发货状态"/>
        <!--支付方式-->
        <el-table-column label="支付方式">
          <template slot-scope="scope">{{ scope.row.payment_type | paymentTypeFilter }}</template>
        </el-table-column>
        <!--订单来源-->
        <el-table-column  label="订单来源">
          <template slot-scope="scope">{{ scope.row.client_type === 'MINI' ? '小程序' : scope.row.client_type  }}</template>
        </el-table-column>
        <!--操作-->
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.order_status !== 'COMPLETE'"
              size="mini"
              type="primary"
              @click="handleOperateOrder(scope.$index, scope.row)">操作</el-button>
            <el-button
              v-else
              size="mini"
              @click="handleOperateOrder(scope.$index, scope.row)">查看</el-button>
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
  import * as API_order from '@/api/order'
  import { Foundation } from '~/ui-utils'

  export default {
    name: 'orderList',
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
        tableData: '',
        // 高级搜索数据
        advancedForm: {}
      }
    },
    filters: {
      paymentTypeFilter(val) {
        return val === 'ONLINE' ? '在线支付' : '货到付款'
      }
    },
    mounted() {
      this.GET_OrderList()
    },
    activated() {
      this.GET_OrderList()
    },
    methods: {
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_OrderList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_OrderList()
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          order_sn: data
        }
        Object.keys(this.advancedForm).forEach(key => delete this.params[key])
        this.params.page_no = 1
        this.GET_OrderList()
      },

      /** 高级搜索事件触发 */
      advancedSearchEvent() {
        this.params = {
          ...this.params,
          ...this.advancedForm
        }
        delete this.params.start_time
        delete this.params.end_time
        if (this.advancedForm.order_time_range) {
          this.params.start_time = this.advancedForm.order_time_range[0]
          this.params.end_time = this.advancedForm.order_time_range[1]
        }
        delete this.params.order_sn
        delete this.params.order_time_range
        this.params.page_no = 1
        this.GET_OrderList()
      },

      /** 查看、操作订单 */
      handleOperateOrder(index, row) {
        this.$router.push({
          name: 'orderDetail',
          params: {
            sn: row.sn,
            callback: this.GET_OrderList
          }
        })
      },

      /** 导出订单 */
      handleExportOrders() {
        API_order.exportOrders(this.params).then(response => {
          const json = {
            sheet_name: '订单列表',
            sheet_values: response.map(item => ({
              '订单编号': item.sn,
              '商品信息': item.sku_list.map(sku_item => {
                return `「商品名称：${sku_item.name}, 商品价格：${sku_item.purchase_price}, 商品数量：${sku_item.num}」`
              }).join(' \r\n'),
              '交易单号': item.trade_sn,
              '下单时间': Foundation.unixToDate(item.create_time),
              '所属店铺': item.seller_name,
              '下单会员': item.member_name,
              '订单总额': Foundation.formatPrice(item.order_amount),
              '订单运费': Foundation.formatPrice(item.shipping_amount),
              '优惠金额': Foundation.formatPrice(item.discount_price),
              '订单状态': item.order_status_text,
              '付款状态': item.pay_status_text,
              '发货状态': item.ship_status_text,
              '付款类型': item.payment_type === 'ONLINE' ? '在线支付' : '货到付款',
              '付款方式': item.payment_method_name,
              '付款时间': Foundation.unixToDate(item.payment_time),
              '付款金额': Foundation.formatPrice(item.pay_money),
              '收货人': item.ship_name,
              '收货人联系方式': item.ship_mobile,
              '收货地址': item.ship_province + '' + item.ship_city + '' + item.ship_county + '' + item.ship_town + '-' + item.ship_addr,
              '物流公司': item.logi_name,
              '快递单号': item.ship_no,
              '订单发货时间': Foundation.unixToDate(item.ship_time),
              '订单收货时间': Foundation.unixToDate(item.signing_time),
              '订单完成时间': Foundation.unixToDate(item.complete_time),
              '订单来源': item.client_type,
              '是否需要发票': item.need_receipt === 1 ? '需要' : '不需要',
              '订单取消原因': item.cancel_reason,
              '买家备注': item.remark
            }))
          }
          this.MixinExportJosnToExcel(json, '订单列表')
        })
      },

      /** 获取订单列表 */
      GET_OrderList() {
        this.loading = true
        const params = this.MixinClone(this.params)
        if (params.start_time && params.end_time) {
          params.start_time = parseInt(params.start_time / 1000)
          params.end_time = parseInt(params.end_time / 1000)
        }
        if (params.seller_id === 0) delete params.seller_id
        API_order.getOrderList(params).then(response => {
          this.loading = false
          this.tableData = response
        }).catch(() => { this.loading = false })
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

  .goods-image {
    width: 50px;
    height: 50px;
  }
</style>

