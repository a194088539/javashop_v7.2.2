<template>
  <div>
    <en-table-layout
      :tableData="tableData.data"
      :row-key="setRowKey"
      @selection-change="handleSelectionChange"
      :loading="loading"
      ref="tableContainer"
    >
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <el-button
            size="mini"
            type="primary"
          @click="handleGoodsBatchAudit">批量审核</el-button>
        </div>
        <div class="toolbar-search">
          <en-table-search
            @search="searchEvent"
            @advancedSearch="advancedSearchEvent"
            advanced
          >
            <template slot="advanced-content">
              <el-form ref="advancedForm" :model="advancedForm" label-width="80px">
                <el-form-item label="商品名称">
                  <el-input size="medium" v-model="advancedForm.goods_name" clearable></el-input>
                </el-form-item>
                <el-form-item label="商品编号">
                  <el-input size="medium" v-model="advancedForm.goods_sn" clearable></el-input>
                </el-form-item>
                <el-form-item label="店铺名称">
                  <en-shop-picker @changed="(shop) => { advancedForm.seller_id = shop.shop_id }"/>
                </el-form-item>
                <el-form-item label="商品类别">
                  <en-category-picker @changed="categoryChanged"/>
                </el-form-item>
                <el-form-item label="商品品牌">
                  <en-brand-picker @changed="(brand) => { advancedForm.brand_id = brand.brand_id }"/>
                </el-form-item>
                <el-form-item label="价格区间">
                  <el-input size="medium" v-model="advancedForm.start_price" :maxlength="10" style="width: 100px;" clearable></el-input>
                  -
                  <el-input size="medium" v-model="advancedForm.end_price" :maxlength="10" style="width: 100px;" clearable></el-input>
                </el-form-item>
              </el-form>
            </template>
          </en-table-search>
        </div>
      </div>
      <template slot="table-columns">
        <el-table-column type="selection" width="55" :selectable="isSelectRow" :reserve-selection="false"></el-table-column>
        <el-table-column label="商品名称">
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
        <!--店铺名称-->
        <el-table-column prop="seller_name" label="店铺名称" width="150"/>
        <!--商品分类-->
        <!--<el-table-column prop="category_name" label="商品分类"/>-->
        <!--销售价格-->
        <el-table-column label="销售价格" width="150">
          <template slot-scope="scope">{{ scope.row.price | unitPrice('￥') }}</template>
        </el-table-column>
        <!--品牌名称-->
        <!--<el-table-column prop="brand_name" label="品牌名称"/>-->
        <!--操作-->
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleGoodsAudit(scope.$index, scope.row)">审核</el-button>
          </template>
        </el-table-column>
      </template>
      <el-pagination
        v-if="tableData"
        slot="pagination"
        @size-change="handlePageSizeChange"
        @current-change="handlePageCurrentChange"
        :current-page="tableData.page_no"
        :page-sizes="[ 10, 20, 50, 100]"
        :page-size="tableData.page_size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="tableData.data_total">
      </el-pagination>
    </en-table-layout>
    <!--审核商品 dialog-->
    <el-dialog
      :title="dialogGoodsAuditTitle"
      :visible.sync="dialogGoodsAuditVisible"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="goodsAuditForm" :rules="goodsAuditRules" ref="goodsAuditForm" label-width="100px">
        <!--是否通过=-->
        <el-form-item label="是否通过" prop="pass">
          <el-radio-group v-model="goodsAuditForm.pass">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="0">不通过</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注信息" prop="message">
          <el-input
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入审核备注(120字以内)"
            :maxlength="120"
            v-model="goodsAuditForm.message">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogGoodsAuditVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitGoodsAuditForm">确 定</el-button>
      </span>
    </el-dialog>
    <!--批量审核商品 dialog-->
    <el-dialog
      title="批量审核商品"
      :visible.sync="dialogGoodsBatchAuditVisible"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="goodsBatchAuditForm" :rules="goodsBatchAuditRules" ref="goodsBatchAuditForm" label-width="100px">
        <!--是否通过=-->
        <el-form-item label="是否通过" prop="pass">
          <el-radio-group v-model="goodsBatchAuditForm.pass">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="0">不通过</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注信息" prop="message">
          <el-input
            type="textarea"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入审核备注(120字以内)"
            :maxlength="120"
            v-model="goodsBatchAuditForm.message">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogGoodsBatchAuditVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitGoodsBatchAuditForm">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
  import * as API_goods from '@/api/goods'
  export default {
    name: 'goodsAudit',
    data() {
      return {
        // 列表loading状态
        loading: false,
        // 列表参数
        params: {
          page_no: 1,
          page_size: 10
        },
        // 商品数据
        tableData: '',
        // 审核商品 表单
        goodsAuditForm: {
          message: '',
          pass: 1
        },
        // 批量审核商品 表单
        goodsBatchAuditForm: {
          goods_ids: [],
          message: '',
          pass: 1
        },
        // 审核商品 表单校验
        goodsAuditRules: {
          message: [
            { required: false, message: '请输入审核备注！', trigger: 'blur' }
          ]
        },
        // 批量审核商品 表单校验
        goodsBatchAuditRules: {
          message: [
            { required: false, message: '请输入审核备注！', trigger: 'blur' }
          ]
        },
        // 审核商品 dialog
        dialogGoodsAuditVisible: false,
        // 批量审核商品 dialog
        dialogGoodsBatchAuditVisible: false,
        // 审核商品 dialogTitle
        dialogGoodsAuditTitle: '审核商品',
        // 高级搜索数据
        advancedForm: {
          goods_name: '',
          goods_sn: '',
          shop_name: '',
          category_path: ''
        },
        // 当前已选择的行
        multipleSelection: []
      }
    },
    mounted() {
      this.GET_GoodsAuditList()
    },
    activated() {
      this.GET_GoodsAuditList()
    },
    watch: {
      'goodsAuditForm.pass': function(newVal) {
        this.goodsAuditRules.message[0].required = newVal === 0
      },
      'goodsBatchAuditForm.pass': function(newVal) {
        this.goodsBatchAuditRules.message[0].required = newVal === 0
      }
    },
    methods: {
      // 单选或者全选
      handleSelectionChange(val) {
        this.multipleSelection = val
      },
      // 判断 当前行是都可以选中
      isSelectRow(row, index) {
        return row.is_auth === 0 ? 1 : 0
      },
      setRowKey(row) {
        return row.create_time
      },
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_GoodsAuditList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_GoodsAuditList()
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          keyword: data
        }
        Object.keys(this.advancedForm).forEach(key => delete this.params[key])
        this.GET_GoodsAuditList()
      },

      /** 高级搜索事件触发 */
      advancedSearchEvent() {
        this.params = {
          ...this.params,
          page_no: 1,
          ...this.advancedForm
        }
        delete this.params.keyword
        this.GET_GoodsAuditList()
      },

      /** 高级搜索中 分类选择组件值发生改变 */
      categoryChanged(data) {
        this.advancedForm.category_path = data.category_path
      },

      /** 审核商品 */
      handleGoodsAudit(index, row) {
        this.dialogGoodsAuditTitle = '审核商品 - ' + row.sn
        this.goodsAuditForm = {
          goods_ids: [row.goods_id],
          message: '',
          pass: 1
        }
        this.dialogGoodsAuditVisible = true
      },
      /** 批量审核商品 */
      handleGoodsBatchAudit() {
        if (!this.multipleSelection.length) {
          return this.$message.error('请选择要审核的商品！')
        }
        this.dialogGoodsBatchAuditVisible = true
      },
      /** 审核商品 表单提交 */
      submitGoodsAuditForm() {
        this.$refs['goodsAuditForm'].validate((valid) => {
          if (valid) {
            API_goods.batchAuditGoods(this.goodsAuditForm).then(response => {
              this.dialogGoodsAuditVisible = false
              this.$message.success('审核完成！')
              this.GET_GoodsAuditList()
            })
          } else {
            this.$message.error('表单填写有误，请核对！')
            return false
          }
        })
      },
      /** 批量审核商品 表单提交 */
      async submitGoodsBatchAuditForm() {
        this.$refs['goodsBatchAuditForm'].validate(async valid => {
          if (valid) {
            let params = JSON.parse(JSON.stringify(this.goodsBatchAuditForm))
            try {
              params.goods_ids = this.multipleSelection.map(item => item.goods_id)
              await API_goods.batchAuditGoods(params)
              await this.GET_GoodsAuditList()
              this.params.page_no = 1
              this.$refs.tableContainer && this.$refs.tableContainer.$refs && this.$refs.tableContainer.$refs.table && this.$refs.tableContainer.$refs.table.clearSelection()
              this.dialogGoodsBatchAuditVisible = false
              this.$message.success('批量审核成功！')
            } catch (e) {
              this.$message.error('批量审核失败，请重试！')
            }
          } else {
            this.$message.error('表单填写有误，请核对！')
            return false
          }
        })
      },
      /** 获取待审核商品 */
      GET_GoodsAuditList() {
        this.loading = true
        API_goods.getAuditGoods(this.params).then(response => {
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
    padding: 0 20px;
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
