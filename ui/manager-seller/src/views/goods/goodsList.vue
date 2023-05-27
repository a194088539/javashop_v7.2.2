<template>
  <div>
    <en-table-layout
      toolbar
      pagination
      :tableData="tableData"
      :height="700"
      :loading="loading"
      @cell-mouse-enter="handleMouseEnter">
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <!--商品状态 上架 下架-->
          <div class="conditions">
            <span>商品状态：</span>
            <el-select
              class="choose-machine"
              v-model="params.market_enable"
              placeholder="请选择商品状态"
              @change="changeGoodsStatus"
              clearable>
              <el-option
                v-for="item in goodsStatusList"
                :key="item.value"
                :label="item.label"
                :value="item.value"></el-option>
            </el-select>
          </div>
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
          <el-button @click="publishGoods" type="primary">发布商品</el-button>
          <el-button @click="gotoRecycle"  type="primary">回收站</el-button>
        </div>
        <div class="toolbar-search">
          <en-table-search @search="searchEvent" placeholder="请输入商品名称或编号"/>
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
          <template slot-scope="scope" slot="header">
            <span>实际库存</span>
            <el-tooltip effect="dark" content="订单发货时扣减" placement="top">
              <i class="iconfont el-icon-info" style="margin-left: 5px;"></i>
            </el-tooltip>
          </template>
          <template slot-scope="scope">{{ scope.row.quantity }} 
            <i v-if="scope.row.is_edit" class="iconfont el-icon-edit" style="margin-left: 5px; font-size: 15px; cursor:pointer;" @click="handleStockGoods(scope.row)"></i>
          </template>
        </el-table-column>
        <el-table-column label="可用库存" width="150">
          <template slot-scope="scope" slot="header">
            <span>可用库存</span>
            <el-tooltip effect="dark" content="创建订单时扣减" placement="top">
              <i class="iconfont el-icon-info" style="margin-left: 5px;"></i>
            </el-tooltip>
          </template>
          <template slot-scope="scope">{{ scope.row.enable_quantity }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="150">
          <template slot-scope="scope">{{ scope.row.create_time | unixToDate('yyyy-MM-dd hh:mm') }}</template>
        </el-table-column>
        <el-table-column  label="状态" width="150">
          <template slot-scope="scope">
            <span>{{ scope.row | marketStatus }}</span>
            <div class="under-reason" v-if="scope.row.market_enable === 0" @click="showUnderReason(scope.row)">(下架原因)</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300" style="text-align: left;">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleEditGoods(scope.row)">编辑
            </el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleSkuInfo(scope.row)">SKU管理
            </el-button>
            <el-button
              v-if="distributionSet"
              type="text"
              size="mini"
              @click="handleRebate(scope.row)">返利
            </el-button>
            <el-button
              size="mini"
              type="text"
              v-if="scope.row.market_enable === 0"
              @click="handleDeleteGoods(scope.row)">删除
            </el-button>
            <el-button
              size="mini"
              type="text"
              v-if="scope.row.market_enable === 1"
              @click="handleUnderGoods(scope.row)">下架
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
    <!--库存编辑-->
    <el-dialog title="编辑实际库存" :visible.sync="goodsStockshow" width="50%" class="pop-sku">
      <div class="goods-stock-info">
        <span class="goods_title">商品标题</span>
        <span class="goods_name"><a :href="`${MixinBuyerDomain}/goods/${goodsId}`" target="_blank" v-html="goodsName">{{goodsName}}</a></span>
      </div>
      <div>
        <el-form :model="goodsStockData" ref="goodsStockData" v-if="goodsStocknums === 1" style="width: 50%;" label-width="100px" :rules="rules" @submit.native.prevent>
          <el-form-item label="实际库存" prop="quantity" >
            <el-input  v-model="goodsStockData.quantity" />
          </el-form-item>
        </el-form>
        <en-table-layout
          :tableData="goodsStockData"
          :loading="loading"
          v-if="goodsStocknums > 1"
          :stripe="false">
          <template slot="table-columns">
            <el-table-column
              v-for="(item, index) in goodsStockTitle"
              v-if="item.prop !== 'sku_id'"
              :label="item.label"
              :key="index">
              <template slot-scope="scope">
                <el-input v-if="item.prop === 'quantity'" @blur="checkQuantity(scope.row.quantity)" style="width: 50%" v-model="scope.row.quantity"/>
                <span v-if="item.prop !== 'quantity'" >{{ scope.row[item.prop] }}</span>
              </template>
            </el-table-column>
          </template>
        </en-table-layout>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="goodsStockshow = false">取 消</el-button>
        <el-button type="primary" @click="reserveStockGoods">确 定</el-button>
      </div>
    </el-dialog>
    <!--下架原因-->
    <el-dialog title="下架原因" :visible.sync="isShowUnderReason" width="17%" >
      <div align="center">{{ under_reason }}</div>
    </el-dialog>
    <!--分销返利-->
    <el-dialog title="分销返利" :visible.sync="isShowDisRebate" width="24%">
      <el-form :model="disRebateData" label-width="100px" :rules="disRules" ref="disRebateData" status-icon>
        <el-form-item label="1级返利" prop="grade1Rebate">
          <el-input v-model="disRebateData.grade1Rebate">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="2级返利" prop="grade2Rebate">
          <el-input v-model="disRebateData.grade2Rebate">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="reserveDisSet('disRebateData')">保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
  import * as API_goods from '@/api/goods'
  import { CategoryPicker } from '@/components'
  import { RegExp } from '~/ui-utils'
  export default {
    name: 'goodsList',
    components: {
      [CategoryPicker.name]: CategoryPicker
    },
    data() {
      const checkQuantity = (rule, value, callback) => {
        if (!value && value !== 0) {
          return callback(new Error('实际库存不能为空'))
        }
        setTimeout(() => {
          if (!/^[0-9]\d*$/.test(value)) {
            callback(new Error('请输入整数'))
          } else if (!(parseInt(value) >= 0 && parseInt(value) <= 99999999)) {
            callback(new Error('请输入0 - 99999999之间的正整数'))
          } else {
            callback()
          }
        }, 500)
      }
      const checkMoney = (rule, value, callback) => {
        if (!value) {
          return callback(new Error('返利金额不能为空'))
        }
        setTimeout(() => {
          if (!RegExp.money.test(value)) {
            callback(new Error('请输入正确的金额'))
          } else if (parseFloat(value) < 0 || parseFloat(value) > 99999999) {
            callback(new Error('请输入0~99999999之间的金额'))
          } else {
            callback()
          }
        }, 500)
      }

      return {
        /** 列表loading状态 */
        loading: false,

        /** 列表参数 */
        params: {
          page_no: 1,
          page_size: 10,
          // 商品类型 默认为'' NORMAL 正常商品 POINT 积分商品
          goods_type: '',
          ...this.$route.query
        },

        /** 列表数据 */
        tableData: [],

        /** 列表分页数据 */
        pageData: [],

        /** 商品状态列表 */
        goodsStatusList: [
          { value: 0, label: '未出售（已下架）' },
          { value: 1, label: '出售中（已上架）' }
        ],

        /** 当前商品分组*/
        categoryId: '',

        /** 当前商品id*/
        goodsId: '',

        /** 当前商品名称*/
        goodsName: '',

        /** 商品库存显示*/
        goodsStockshow: false,

        /** 是否显示下架原因 */
        isShowUnderReason: false,

        /** 下架原因 */
        under_reason: '',

        /** 库存商品数量*/
        goodsStocknums: 0,

        /** 商品库存列表数据*/
        goodsStockData: null,

        /** 商品库存列表表头数据 */
        goodsStockTitle: [],

        /** 要合并的列的位置数组 */
        concactArray: [],

        /** 校验规则 */
        rules: {
          quantity: [
            { validator: checkQuantity, trigger: 'blur' }
          ]
        },

        /** 店铺信息 */
        shopInfo: this.$store.getters.shopInfo,

        /** 分销设置是否开启 1开启 0关闭 */
        distributionSet: 0,

        /** 是否显示分销返利弹框 */
        isShowDisRebate: false,

        /** 分销返利数据 */
        disRebateData: {
          /** 商品id */
          goods_id: 0,

          /** 1级返利 */
          grade1Rebate: 0,

          /** 2级返利 */
          grade2Rebate: 0

        },

        /** 分销返利校验规则 */
        disRules: {
          grade1Rebate: [
            { required: true, message: '1级返利金额不能为空', trigger: 'blur' },
            { validator: checkMoney, trigger: 'blur' }
          ],
          grade2Rebate: [
            { required: true, message: '2级返利金额不能为空', trigger: 'blur' },
            { validator: checkMoney, trigger: 'blur' }
          ]
        }
      }
    },
    filters: {
      /** 销售状态格式化 */
      marketStatus(row) {
        switch (row.is_auth) {
          case 0 : return row.market_enable === 1 ? '待审核' : '已下架'
          case 1 : return row.market_enable === 1 ? '售卖中' : '已下架'
          case 2 : return row.market_enable === 1 ? '审核拒绝' : '已下架'
        }
      }
    },
    mounted() {
      this.params = { ...this.params, ...this.$route.query }
      this.params.market_enable && (this.params.market_enable = parseInt(this.params.market_enable))
      this.GET_GoodsList()
      this.getDistributionSet()
    },
    activated() {
      this.params = { ...this.params, ...this.$route.query }
      this.params.market_enable && (this.params.market_enable = parseInt(this.params.market_enable))
      this.GET_GoodsList()
      this.getDistributionSet()
    },
    methods: {
      /** 库存边界限制 */
      checkQuantity(value) {
        if (!value && value !== 0) {
          this.$message.error('库存不能为空')
        } else if (!RegExp.integer.test(value) && parseInt(value) !== 0) {
          this.$message.error('请输入整数')
        } else if (!(parseInt(value) >= 0 && parseInt(value) <= 99999999)) {
          this.$message.error('请输入0 - 99999999之间的正整数')
        }
      },

      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_GoodsList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_GoodsList()
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          page_no: 1,
          keyword: data
        }
        this.GET_GoodsList()
      },

      /** 切换上下架状态*/
      changeGoodsStatus(val) {
        delete this.params.market_enable
        if (val !== '' && val !== -1) {
          this.params = {
            ...this.params,
            market_enable: val
          }
        }
        this.GET_GoodsList()
      },

      /** 下架*/
      handleUnderGoods(row) {
        this.$confirm('确认下架此商品, 是否继续?', '提示', { type: 'warning' }).then(() => {
          API_goods.underGoods(row.goods_id, {}).then(() => {
            this.$message.success('下架成功')
            this.GET_GoodsList()
          })
        })
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
        this.GET_GoodsList()
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
        this.GET_GoodsList()
      },

      /** 显示下架原因 */
      showUnderReason(row) {
        this.isShowUnderReason = false
        this.isShowUnderReason = true
        this.under_reason = row.under_message
      },

      GET_GoodsList() {
        this.loading = true
        API_goods.getGoodsList(this.params).then(response => {
          this.loading = false
          this.pageData = {
            page_no: response.page_no,
            page_size: response.page_size,
            data_total: response.data_total
          }
          this.tableData = response.data
        })
      },

      /** 发布商品*/
      publishGoods() {
        this.$router.push({ name: 'goodPublish', params: { callback: this.GET_GoodsList }})
      },

      /** 跳转回收站*/
      gotoRecycle() {
        this.$router.push({ path: '/goods/recycle-station' })
      },

      /** SKU管理 */
      handleSkuInfo(row) {
        this.$router.push({ name: 'goodsSku', params: { goodsid: row.goods_id, callback: this.GET_GoodsList }})
      },

      /** 编辑商品 isdraft 商品列表1*/
      handleEditGoods(row) {
        this.$router.push({ name: 'goodPublish', params: { goodsid: row.goods_id, isdraft: 1, callback: this.GET_GoodsList }})
      },

      /** 删除商品 */
      handleDeleteGoods(row) {
        this.$confirm('确认删除此商品, 是否继续?', '提示', { type: 'warning' }).then(() => {
          const _ids = [row.goods_id].toString()
          API_goods.deleteGoods(_ids).then(() => {
            this.GET_GoodsList()
            this.$message.success('删除商品成功！')
          })
        })
      },

      /** 库存编辑按钮显示 */
      handleMouseEnter(row) {
        this.$set(this, 'tableData', this.tableData.map(item => {
          item.is_edit = item.goods_id === row.goods_id
          return item
        }))
      },
      /** 库存 */
      handleStockGoods(row) {
        this.goodsId = row.goods_id
        this.goodsName = row.goods_name
        this.goodsStockshow = true
        API_goods.getGoodsStockList(row.goods_id, {}).then((response) => {
          this.goodsStockTitle = this.goodsStockData = []
          this.goodsStocknums = response.length
          // 构造待发货字段
          if (response.length > 1) {
            this.$nextTick(() => {
              response.forEach((key) => {
                this.goodsStockTitle = [
                  { label: 'SKU', prop: 'sku' },
                  { label: '实际库存', prop: 'quantity' }
                ]
                // 构造表结构
                let _skuData = []
                key.spec_list.forEach(elem => {
                  _skuData.push(elem.spec_value)
                })
                _skuData = _skuData.join('/')
                const _key = {
                  sku: _skuData,
                  sku_id: key.sku_id,
                  quantity: key.quantity
                }
                this.goodsStockData.push(Object.assign(_key))
              })
            })
          } else {
            this.goodsStockData = response[0]
          }
        })
      },

      /** 保存库存商品 */
      reserveStockGoods() {
        let _params = []
        if (Array.isArray(this.goodsStockData)) {
          _params = this.goodsStockData.map((elem) => {
            return {
              quantity_count: elem.quantity,
              sku_id: elem.sku_id
            }
          })
        } else {
          _params.push({
            quantity_count: this.goodsStockData.quantity,
            sku_id: this.goodsStockData.sku_id
          })
        }
        const _res = _params.some(key => {
          return !(parseInt(key.quantity_count) >= 0 && parseInt(key.quantity_count) <= 99999999) || !/^[0-9]\d*$/.test(key.quantity_count)
        })
        if (_res) {
          this.$message.error('实际库存须为0 - 99999999之间的整数')
          return
        }
        API_goods.reserveStockGoods(this.goodsId, _params).then(() => {
          this.goodsStockshow = false
          this.$message.success('实际库存商品保存成功')
          this.GET_GoodsList()
        })
      },

      /** 获取分销设置 */
      getDistributionSet() {
        API_goods.getDistributionSet().then(response => {
          this.distributionSet = response.message
        })
      },

      /** 返利 获取返利信息*/
      handleRebate(row) {
        setTimeout(() => { this.$refs['disRebateData'].resetFields() })
        API_goods.getDistributionInfo(row.goods_id).then(response => {
          this.isShowDisRebate = true
          this.disRebateData = {
            /** 商品id */
            goods_id: response.goods_id || row.goods_id,

            /** 1级返利 */
            grade1Rebate: response.grade1_rebate,

            /** 2级返利 */
            grade2Rebate: response.grade2_rebate
          }
        })
      },

      /** 保存分销返利信息 */
      reserveDisSet(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            API_goods.setDistributionInfo(this.disRebateData).then(() => {
              this.isShowDisRebate = false
              this.$message.success('当前商品分销返利金额修改成功')
            })
          }
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  /deep/ .el-table--scrollable-x .el-table__body-wrapper {
    overflow-x: inherit;
  }
  /deep/ div.toolbar {
    height: 70px;
    padding: 20px 0;
  }
  /deep/ .el-table {
    width: 100%;
    .el-button--text {
      font-size: 15px;
      margin: 0;
      font-weight: 600;
      span {
        padding: 0 10px;
        border-right: 1.5px solid #409EFF;
      }
      &:last-child span {
        border-right: none;
      }
    }
  }

  .inner-toolbar {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    width: 100%;
    justify-content: space-between;
    align-items: center;
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

  /deep/ .pop-sku {
    .toolbar {
      display: none;
    }
    .el-form-item__label {
      text-align: left;
    }
    .el-table__header-wrapper {
      th, tr {
        background-color: #e1e1e1;
      }
    }
    .el-table__body-wrapper {
      max-height: 440px;
      overflow-y: scroll;
    }
    .goods-stock-info {
      padding-bottom: 20px;
      .goods_title {
        color: #919EAF;
        margin-right: 40px;
      }
    }
  }


  .toolbar-search {
    margin-right: 10px;
    width: 20%;
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

  /deep/ div.cell {
    overflow:hidden;

    text-overflow:ellipsis;

    display:-webkit-box;

    -webkit-box-orient:vertical;

    -webkit-line-clamp:2;
  }
  /*下架原因*/
  .under-reason {
    color: red;
    cursor: pointer;
  }

</style>
