<template>
  <div id="goods-sku" style="background-color: #fff;">
    <div class="content-goods-sku">
    	<el-form
        :model="skuInfoForm"
        :rules="rules">
  	  	<!--商品信息-->
  	    <div class="base-info-item goods-info">
  	      <h4>商品信息</h4>
  	      <div class="goods-info">
  	        <el-form-item label="商品名称：" class="goods-name-width">
              <span style="cursor: pointer;" v-html="skuInfoForm.goods_name">{{skuInfoForm.goods_name}}</span>
  	        </el-form-item>
  	        <el-form-item label="商品图片：">
  	        	<div class="goods-image-list" v-for="(goods,index) in skuInfoForm.goods_gallery_list">
  	        		<img :src="goods.thumbnail" alt="">
  	        	</div>
  	        </el-form-item>
  	      </div>
  	    </div>
  	    <!--商品规格-->
  	    <div class="base-info-item" v-if="skuInfoForm">
  	      <h4>商品规格</h4>
  	      <div>
  	        <el-form-item label="商品规格：" label-width="120px" style="width: 90%;text-align: left;">
              <en-sku-selector
              :categoryId="skuInfoForm.category_id"
              :goodsId="goodsId"
              :goodsSn="skuInfoForm.sn"
              :productSn="productSn"
              :goodsSkuInfo="skuList"
              @finalSku="finalSku"/>
  	        </el-form-item>
  	        <el-form-item label="总库存：" label-width="120px" style="width: 20%;text-align: left;">
  						<el-input v-model.number="skuInfoForm.quantity" disabled></el-input>
  	        </el-form-item>
  	      </div>
  	    </div>
      </el-form>
    </div>
    <div class="footer">
      <el-button type="primary" style="padding: 12px 40px;font-size: 15px;" @click="reserveStockGoods">保存</el-button>
    </div>
  </div>
</template>

<script>
  import * as API_goods from '@/api/goods'
  import { SkuSelector } from '@/components'
  import { RegExp } from '~/ui-utils'
  export default {
    name: 'goodsSku',
    components: {
      [SkuSelector.name]: SkuSelector
    },
    data() {
      // 总库存
      const checkQuantity = (rule, value, callback) => {
        if (!value) {
          callback(new Error('请填写总库存'))
        } else if (!RegExp.integer.test(value)) {
          callback(new Error('请输入大于0的正整数'))
        } else {
          if (parseInt(value) > 99999999) {
            callback(new Error('总库存不得大于99999999'))
          } else {
            callback()
          }
        }
      }
      return {
        goodsId: this.$route.params.goodsid,

        /** 用来向组件中传递的数据 */
        skuList: [],

        /** 规格信息提交表单 */
        skuInfoForm: {
          /** 商城分类id */
          category_id: 0,

          /** 商品名称 */
          goods_name: '',

          /** 商品编号 sn*/
          sn: '',

          /** 商品总库存 */
          quantity: 0,

          /** sku数据变化或者组合变化判断 0:没变化，1：变化 */
          has_changed: 0,

          /** sku列表 */
          sku_list: []
        },

        /** 是否自动生成货号 */
        productSn: false,

        /** 商品库存列表数据*/
        goodsStockData: null,

        /** 校验规则 */
        rules: {
          quantity: [
            { validator: checkQuantity, trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
      this.GET_GoodData()
    },
    methods: {
      async reserveStockGoods() {
        /** 规格校验 */
        if (!this.skuFormVali()) return
        /** 是否规格维度发生变化 */
        const final = this.skuInfoForm.sku_list.map(key => { if (key && key.sku_id) return key.sku_id })
        const _final = this.skuList.map(key => { if (key && key.sku_id) return key.sku_id })
        const _result = _final.every(key => final.includes(key))
        if (final.length && _final.length && !_result) {
          await this.$confirm('因规格的维度发生变化，会导致之前的SKU组合不存在，这会使相应的SKU参加的促销失效，确认要这么操作吗？', '提示', { type: 'warning' })
        }
        const _sku_list = this.skuInfoForm.sku_list
        API_goods.updateGoodsStockList(this.goodsId, _sku_list).then(() => {
          this.$message.success('保存成功')
          this.$router.push({ path: '/goods/goods-list' })
          this.$store.dispatch('delCurrentViews', {
            view: this.$route,
            $router: this.$router
          })
        })
      },
      /** 规格选择器规格数据改变时触发 */
      finalSku(val) {
        // 动态修改总库存 每次设置为0  此处每次进行循环计算 存在性能浪费
        // 如果是编辑模式 总库存不得重置为0
        this.skuInfoForm.quantity = 0
        val.forEach(key => {
          if (key.quantity) {
            this.skuInfoForm.quantity += parseInt(key.quantity)
          }
        })
        /** 删除 因为对象浅拷贝造成的字段冗余（不必要的字段）*/
        val.forEach(key => {
          key.spec_list.forEach(item => {
            delete item.cost
            delete item.price
            delete item.quantity
            delete item.sn
            delete item.weight
          })
        })
        this.skuInfoForm.sku_list = val
        /** 规格数据是否存在变化 */
        this.skuInfoForm.has_changed = 1
      },
      /** 商品规格选择器校验 */
      skuFormVali(isDraft) {
        /** 如果并未选择规格 */
        if (Array.isArray(this.skuInfoForm.sku_list) && this.skuInfoForm.sku_list.length === 0) {
          return true
        }
        this.productSn = false
        /** 是否自动生成货号校验 检测是否所有的货号都存在*/
        const _sn = this.skuInfoForm.sku_list.some(key => {
          return key.sn === ''
        })
        if (_sn) {
          this.$confirm('您没有为SKU填写货号，系统将根据商品货号自动生成, 是否继续?', '提示').then(() => {
            this.productSn = true
          })
          return false
        }
        /** 规格值空校验 */
        let _result = false
        this.skuInfoForm.sku_list.forEach(key => {
          Object.values(key).forEach(item => {
            if (!item && item !== 0) {
              _result = true
            }
          })
        })
        if (_result) {
          this.$message.error('存在未填写的规格值')
          return false
        }
        // 规格值校验
        let spec_fun = false
        let spec_tip
        this.skuInfoForm.sku_list.forEach(key => {
          if (!RegExp.money.test(key.cost)) {
            spec_fun = true
            spec_tip = '请输入正确的成本价金额'
          }
          if (!RegExp.money.test(key.price)) {
            spec_fun = true
            spec_tip = '请输入正确的价格'
          }
          if (!(parseInt(key.weight) >= 0 && parseInt(key.weight) <= 99999999)) {
            spec_fun = true
            spec_tip = '重量须为0 - 99999999之间的整数'
          }
          if (!(parseInt(key.quantity) >= 0 && parseInt(key.quantity) <= 99999999) || !/^[0-9]\d*$/.test(key.quantity)) {
            spec_fun = true
            spec_tip = '库存须为0 - 99999999之间的整数'
          }
        })
        if (spec_fun) {
          this.$message.error(spec_tip)
          return false
        }
        if (isDraft) {
          this.productSn = true
          return true
        }
        return true
      },
      GET_GoodData() {
        API_goods.getGoodData(this.goodsId, {}).then((response) => {
          this.skuInfoForm = { ...response }
          /** 商品规格校验属性  */
          if (!this.skuInfoForm.sku_list || !Array.isArray(this.skuInfoForm.sku_list)) {
            this.skuInfoForm.sku_list = []
          }
          API_goods.getGoodsStockList(this.goodsId, {}).then((response) => {
            /** 构造临时规格数据 */
            this.skuList = response.map(key => {
              if (key && key.spec_list && Array.isArray(key.spec_list)) {
                const spec_list = key.spec_list.map(item => {
                  let { spec_id, spec_image, spec_type, spec_value, spec_value_id, spec_name } = item
                  return { spec_id, spec_image, spec_type, spec_value, spec_value_id, spec_name }
                })
                let _spec_value_id = key.spec_list.map(item => item.spec_value_id).join('|')
                let { cost, quantity, sn, weight, sku_id } = key
                const price = key.price
                return { cost, price, quantity, sn, weight, sku_id, spec_list, _spec_value_id }
              } else {
                return []
              }
            })
          })
        })
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  .content-goods-sku {
    border-bottom: 1px solid #dcdfe6;
    div.base-info-item>div {
      margin-left: 5%
    }
    div.base-info-item {
      h4 {
        margin-bottom: 10px;
        padding:0 10px;
        border: 1px solid #ddd;
        background-color: #f8f8f8;
        font-weight: bold;
        color: #333;
        font-size: 14px;
        line-height: 40px;
        text-align: left;
      }
      .el-form-item {
      	width: 50%;
        display: flex;
        .goods-image-list {
        	width: 148px;
        	height: 148px;
        	overflow: hidden;
        	margin: 0 8px 8px 0;
        	border-radius: 6px;
        	border: 1px solid #c0ccda;
        	box-sizing: border-box;
        	display: inline-block;
          cursor: pointer;
        	img {
        		width: 148px;
        		height: 148px;
        	}
        }
      }
      .goods-info .el-form-item:before {
      	content: "*";
      	color:red;
      	line-height: 2.4;
      	margin:0 5px;
      }
      /deep/ .el-form-item__content {
        flex: 1;
        margin-left: 0 !important;
      }
    }
  }
  .footer {
    padding: 20px 0;
    text-align: center;
  }
</style>