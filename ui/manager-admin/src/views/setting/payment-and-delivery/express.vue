<template>
  <div>
    <en-table-layout
      :tableData="tableData.data"
      :loading="loading"
    >
      <div slot="toolbar" class="inner-toolbar">
        <div class="toolbar-btns">
          <el-button size="mini" type="primary" icon="el-icon-circle-plus-outline" @click="handleAddExpress">添加</el-button>
        </div>
        <div class="toolbar-search">
          <en-table-search @search="searchEvent" placeholder="请输入公司名称" />
        </div>
      </div>
      <template slot="table-columns">
        <el-table-column prop="name" label="公司名称"/>
        <el-table-column prop="code" label="公司代码"/>
        <el-table-column prop="kdcode" label="快递鸟代码"/>
        <el-table-column label="状态" :formatter="disabledStatus"/>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEditExpress(scope.$index, scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.disabled === 'OPEN'"
              size="mini"
              type="danger"
              @click="handleDisabledExpress(scope.$index, scope.row)">禁用</el-button>
            <el-button
              v-if="scope.row.disabled === 'CLOSE'"
              size="mini"
              type="success"
              @click="handleDisabledExpress(scope.$index, scope.row)">开启</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDeleteExpress(scope.$index, scope.row)">删除</el-button>
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
    <el-dialog
      title="物流公司"
      :visible.sync="dialogExpressVisible"
      :modal-append-to-body="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form :model="expressForm" :rules="expressRules" ref="expressForm" label-width="120px">
        <el-form-item label="物流公司名称" prop="name">
          <el-input v-model="expressForm.name"></el-input>
        </el-form-item>
        <el-form-item label="物流公司代码" prop="code">
          <el-input v-model="expressForm.code"></el-input>
        </el-form-item>
        <el-form-item label="快递鸟物流代码" prop="kdcode">
          <el-input v-model="expressForm.kdcode"></el-input>
        </el-form-item>
        <el-form-item label="电子面单" prop="is_waybill">
          <el-radio-group v-model="expressForm.is_waybill">
            <el-radio :label="1">支持</el-radio>
            <el-radio :label="0">不支持</el-radio>
          </el-radio-group>
        </el-form-item>

        <template v-if="expressForm.is_waybill">
          <el-row v-for="(item, index) in expressForm.formItems" :key="index">
            <el-col :span="11">
              <el-form-item label="字段类型"  :prop="`formItems[${index}].code`" label-width="80px">
                <el-select v-model="item.code" placeholder="字段类型" style="width:150px;">
                  <el-option label="customer_name" value="customer_name"></el-option>
                  <el-option label="customer_pwd" value="customer_pwd"></el-option>
                  <el-option label="month_code" value="month_code"></el-option>
                  <el-option label="send_site" value="send_site"></el-option>
                  <el-option label="send_staff" value="send_staff"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="10">
              <el-form-item label="字段名称" :prop="`formItems[${index}].name`" label-width="80px">
                <el-input v-model="item.name" placeholder="字段名称" style="width:130px;"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="text-align: center;">
              <el-button type="primary" @click="removeFromList(index)">删除</el-button>
            </el-col>
          </el-row>
          <el-row v-if="expressForm.formItems.length < 5">
            <el-col :span="24" style="text-align: center;">
                <el-button type="primary" @click="addFormList">增加</el-button>
            </el-col>
          </el-row>
        </template>

      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogExpressVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitExpressForm('expressForm')">确 定</el-button>
        </span>
    </el-dialog>
  </div>
</template>

<script>
  import * as API_Express from '@/api/express'

  export default {
    name: 'expressSettings',
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
        /** 快递公司 表单 */
        expressForm: { formItems: [] },
        /** 快递公司 dialog */
        dialogExpressVisible: false
      }
    },
    computed: {
      /** 快递公司 表单规则 */
      expressRules() {
        let validate = {
          name: [this.MixinRequired('请输入物流公司名称！')],
          code: [this.MixinRequired('请输入物流公司代码！')],
          kdcode: [this.expressForm.is_waybill ? this.MixinRequired('请输入快递鸟物流代码！') : {}],
          customer_name: [this.MixinRequired('请输入物流公司客户号！')],
          customer_pwd: [this.MixinRequired('请输入物流公司电子面单密码！')]
        }
        this.expressForm.formItems.forEach((item, index) => {
          validate[`formItems[${index}].name`] = [{
            validator: (rule, value, callback) => {
              let { formItems } = this.expressForm
              if (value === '') {
                callback(new Error('请输入字段名称！'))
              } else if (formItems.filter(i => i.name === value).length > 1) {
                callback(new Error('字段名称已重复！'))
              } else {
                callback()
              }
            }
          }]
          validate[`formItems[${index}].code`] = [{
            validator: (rule, value, callback) => {
              let { formItems } = this.expressForm
              if (value === '') {
                callback(new Error('请输入字段类型！'))
              } else if (formItems.filter(i => i.code === value).length > 1) {
                callback(new Error('字段类型已重复！'))
              } else {
                callback()
              }
            }
          }]
        })
        return validate
      }
    },
    mounted() {
      this.GET_ExpressList()
    },
    methods: {
      addFormList() {
        /** 电子面单表单验证 */
        let { formItems } = this.expressForm
        let isContainue = true
        if (!formItems.length) {
          this.expressForm.formItems.push({
            name: '',
            code: ''
          })
          return
        }
        let last = formItems[formItems.length - 1]
        if (!last.code || !last.name) {
          this.$message.error('请填写完整在添加！')
          return
        }
        formItems.forEach(item => {
          let current = formItems.filter(j => (j.code === item.code || j.name === item.name))
          current.length > 1 ? isContainue = false : null
        })
        if (!isContainue) {
          this.$message.error('已经存在重复的字段类型或字段名称！')
          return
        }

        this.expressForm.formItems.push({
          name: '',
          code: ''
        })
      },
      removeFromList(payload) {
        this.expressForm.formItems.splice(payload, 1)
      },
      /** 分页大小发生改变 */
      handlePageSizeChange(size) {
        this.params.page_size = size
        this.GET_ExpressList()
      },

      /** 分页页数发生改变 */
      handlePageCurrentChange(page) {
        this.params.page_no = page
        this.GET_ExpressList()
      },

      /** 状态格式化 */
      disabledStatus(row, column, cellValue) {
        return row.disabled === 'OPEN' ? '已开启' : '已禁用'
      },

      /** 添加快递公司 */
      handleAddExpress() {
        this.expressForm = { is_waybill: 0, formItems: [] }
        this.dialogExpressVisible = true
        this.$nextTick(() => this.$refs.expressForm.clearValidate())
      },

      /** 编辑物流公司 */
      handleEditExpress(index, row) {
        const params = JSON.parse(JSON.stringify(row))
        if (typeof params.is_waybill !== 'number') {
          params.is_waybill = 0
        }

        this.expressForm = {
          name: params.name,
          code: params.code,
          is_waybill: params.is_waybill,
          kdcode: params.kdcode,
          id: params.id,
          formItems: params.form || []
        }

        this.dialogExpressVisible = true
        this.$nextTick(() => this.$refs.expressForm.clearValidate())
      },

      /** 快递公司 提交表单 */
      submitExpressForm(formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
            const { id } = this.expressForm
            let expressForm = JSON.parse(JSON.stringify(this.expressForm))
            if (!id) {
              API_Express.addExpress(expressForm).then(response => {
                this.dialogExpressVisible = false
                this.$message.success('添加成功！')
                this.GET_ExpressList()
              })
            } else {
              API_Express.editExpress(id, expressForm).then(response => {
                this.dialogExpressVisible = false
                this.$message.success('保存成功！')
                this.MixinSetTableData(this.tableData, 'id', id, response)
              })
            }
          } else {
            this.$message.error('表单填写有误，请检查！')
            return false
          }
        })
      },

      /** 搜索事件触发 */
      searchEvent(data) {
        this.params = {
          ...this.params,
          name: data
        }
        this.params.page_no = 1
        this.GET_ExpressList()
      },

      /** 删除物流公司 */
      handleDeleteExpress(index, row) {
        this.$confirm('确定要删除此物流公司吗？', '提示', { type: 'warning' }).then(() => {
          API_Express.deleteExpress(row.id).then(response => {
            this.$message.success('删除成功！')
            this.GET_ExpressList()
          })
        }).catch(() => {})
      },

      /** 开启或禁用物流公司 */
      handleDisabledExpress(index, row) {
        this.$confirm('确定要执行此操作吗？', '提示', { type: 'warning' }).then(() => {
          API_Express.openDisableExpress(row.id, row.disabled === 'OPEN' ? 'CLOSE' : 'OPEN').then(response => {
            this.$message.success('操作成功！')
            this.GET_ExpressList()
          })
        }).catch(() => {})
      },

      /** 获取物流公司列表 */
      GET_ExpressList() {
        this.loading = true
        API_Express.getExpressList(this.params).then(response => {
          this.loading = false
          this.tableData = response
        }).catch(() => {
          this.loading = false
        })
      }
    }
  }
</script>
