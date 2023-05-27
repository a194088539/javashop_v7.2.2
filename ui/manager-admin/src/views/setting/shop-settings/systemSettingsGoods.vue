<template>
  <el-form :model="goodsForm" :rules="goodsFormRule" ref="goodsForm" label-width="130px" style="width: 350px">
    <el-form-item label="上架商品是否审核">
      <el-radio-group v-model="goodsForm.marcket_auth">
        <el-radio :label="1">是</el-radio>
        <el-radio :label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="修改商品是否审核">
      <el-radio-group v-model="goodsForm.update_auth">
        <el-radio :label="1">是</el-radio>
        <el-radio :label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="商品评论是否审核">
      <el-radio-group v-model="goodsForm.comment_auth">
        <el-radio :label="1">是</el-radio>
        <el-radio :label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="商品咨询是否审核">
      <el-radio-group v-model="goodsForm.ask_auth">
        <el-radio :label="1">是</el-radio>
        <el-radio :label="0">否</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="缩略图尺寸">
      <el-form-item prop="thumbnail_width">
        <el-input placeholder="100" size="small" v-model="goodsForm.thumbnail_width">
          <template slot="prepend">宽</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
      <el-form-item prop="thumbnail_height">
        <el-input placeholder="100" size="small" v-model="goodsForm.thumbnail_height">
          <template slot="prepend">高</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
    </el-form-item>
    <el-form-item label="小图尺寸" prop="cancel_order_day">
      <el-form-item prop="small_width">
        <el-input placeholder="400" size="small" v-model="goodsForm.small_width">
          <template slot="prepend">宽</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
      <el-form-item prop="small_height">
        <el-input placeholder="400" size="small" v-model="goodsForm.small_height">
          <template slot="prepend">高</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
    </el-form-item>
    <el-form-item label="大图尺寸" prop="cancel_order_day">
      <el-form-item prop="big_width">
        <el-input placeholder="800" size="small" v-model="goodsForm.big_width">
          <template slot="prepend">宽</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
      <el-form-item prop="big_height">
        <el-input placeholder="800" size="small" v-model="goodsForm.big_height">
          <template slot="prepend">高</template>
          <template slot="append">px</template>
        </el-input>
      </el-form-item>
    </el-form-item>
    <el-form-item label="">
      <el-button type="primary" @click="submitEditGoodsSetting">保存设置</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  import * as API_Goods from '@/api/goods'
  import { RegExp } from '~/ui-utils'

  export default {
    name: 'systemSettingsGoods',
    data() {
      const checkThumbnailWidth = (rule, value, callback) => {
        console.log(value)
        if (!value && typeof value !== 'number') {
          callback(new Error('缩略图宽度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('缩略图宽度设置超过上限值'))
        } else {
          callback()
        }
      }

      const checkThumbnailHeight = (rule, value, callback) => {
        if (!value && typeof value !== 'number') {
          callback(new Error('缩略图高度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('缩略图高度设置超过上限值'))
        } else {
          callback()
        }
      }

      const checkSmallWidth = (rule, value, callback) => {
        if (!value && typeof value !== 'number') {
          callback(new Error('小图高度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('小图高度设置超过上限值'))
        } else {
          callback()
        }
      }

      const checkSmallHeight = (rule, value, callback) => {
        if (!value && typeof value !== 'number') {
          callback(new Error('小图高度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('小图高度设置超过上限值'))
        } else {
          callback()
        }
      }

      const checkBigWidth = (rule, value, callback) => {
        if (!value && typeof value !== 'number') {
          callback(new Error('大图高度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('大图高度设置超过上限值'))
        } else {
          callback()
        }
      }

      const checkBigHeight = (rule, value, callback) => {
        if (!value && typeof value !== 'number') {
          callback(new Error('大图高度不能为空'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入正整数'))
        } else if (parseFloat(value) > 99999999) {
          callback(new Error('大图高度设置超过上限值'))
        } else {
          callback()
        }
      }
      return {
        // 商品设置
        goodsForm: {
          marcket_auth: 1,
          update_auth: 1,
          comment_auth: 1,
          ask_auth: 1,
          thumbnail_width: 100,
          thumbnail_height: 100,
          small_width: 400,
          small_height: 400,
          big_width: 800,
          big_height: 800
        },
        goodsFormRule: {
          thumbnail_width: [
            { required: true, message: '请输入缩略图宽度', trigger: 'blur' },
            { validator: checkThumbnailWidth, trigger: 'blur' }
          ],
          thumbnail_height: [
            { required: true, message: '请输入缩略图高度', trigger: 'blur' },
            { validator: checkThumbnailHeight, trigger: 'blur' }
          ],
          small_width: [
            { required: true, message: '请输入小图宽度', trigger: 'blur' },
            { validator: checkSmallWidth, trigger: 'blur' }
          ],
          small_height: [
            { required: true, message: '请输入小图高度', trigger: 'blur' },
            { validator: checkSmallHeight, trigger: 'blur' }
          ],
          big_width: [
            { required: true, message: '请输入大图宽度', trigger: 'blur' },
            { validator: checkBigWidth, trigger: 'blur' }
          ],
          big_height: [
            { required: true, message: '请输入大图高度', trigger: 'blur' },
            { validator: checkBigHeight, trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
      API_Goods.getGoodsSettings().then(response => {
        Object.keys(this.goodsForm).forEach(key => {
          this.goodsForm[key] = response[key]
        })
      })
    },
    methods: {
      submitEditGoodsSetting() {
        this.$refs['goodsForm'].validate((valid) => {
          if (valid) {
            API_Goods.editGoodsSettings(this.goodsForm).then(() => {
              this.$message.success('修改成功！')
            })
          } else {
            this.$message.error('表单填写有误，请核对！')
          }
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .image-pane .el-input-group {
    width: 200px;
  }
  .point-pane .el-input-group {
    width: 200px;
  }
</style>
