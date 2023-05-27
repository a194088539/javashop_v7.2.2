<template>
  <div class="ncsc-mobile-editor" @change="updateList(mobile_description_list)">
    <div class="title-bar"></div>
    <div class="container">
      <div class="pannel">
        <div class="control-panel" ref="controlContent">
          <div>
            <div 
              v-for="(item,index) in mobile_description_list"
              :class="[item.type == 'text' ? 'm-text' : 'm-image', item.checked ? 'current' : '' ]"
              class="module"
              @click="handleEditAreaContent(index)"
            >
              <div class="tools" v-if="!item.edit_checked">
                <a href="javascript:;" @click.stop="handleMoveUpward(item,index)">上移</a>
                <a href="javascript:;" @click.stop="handleMoveDown(item,index)">下移</a>
                <a href="javascript:;" v-if="item.type=='text'" @click.stop="handleEdit(index)">编辑</a>
                <el-upload
                  v-else
                  style="display: contents;"
                  :action="MixinUploadApi"
                  :before-upload="beforeAvatarUpload"
                  :on-success="handleReplaceImageSuccess"
                  :show-file-list="false"
                >
                  <a href="javascript:;">替换</a>
                </el-upload>
                <a href="javascript:;" @click.stop="handleRemove(index)">移除</a>
              </div>
              <div class="content">
                <div class="ncsc-mea-text" v-if="item.type == 'text' && item.edit_checked">
                  <p class="text-tip"></p>
                  <textarea class="textarea" cols="30" rows="10" maxlength="500" v-model="item.content"></textarea>
                  <div class="button">
                    <el-button type="primary" @click.stop="handleEditConfirm(item.content,index)">确定</el-button>
                    <el-button @click.stop="handleEditCancel(index)">取消</el-button>
                  </div>
                </div>
                <div class="text-div" v-if="item.type == 'text' && !item.edit_checked">{{item.content}}</div>
                <div class="image-div" v-else>
                  <img :src="item.content" alt="">
                </div>
              </div>
              <div class="cover" v-if="!item.edit_checked"></div>
            </div>
          </div>
          <div class="ncsc-mobile-edit-area" v-if="isEditArea">
            <div class="ncsc-mea-text">
              <p class="text-tip">
                <span>还可以输入<em>{{500 - editAreaText.length}}</em>字</span>
              </p>
              <textarea class="textarea" cols="30" rows="10" maxlength="500" v-model="editAreaText"></textarea>
              <div class="button">
                <el-button type="primary" @click="handleEditArea">确定</el-button>
                <el-button @click="handleHideEditArea">取消</el-button>
              </div>
            </div>
          </div>
          <div class="add-btn">
            <div class="btn">
              <el-upload
                :action="MixinUploadApi"
                :before-upload="beforeAvatarUpload"
                :on-success="handleImageSuccess"
                :on-error="handleError"
                :show-file-list="false"
              >
                <a href="javascript:void(0);">
                  <i class="iconfont el-icon-picture-outline"></i>
                  <p>图片</p>
                </a>
              </el-upload>
            </div>
            <div class="btn" @click="handleShowEditArea">
              <a href="javascript:void(0);">
                <i class="iconfont el-icon-edit"></i>
                <p>文字</p>
              </a>
            </div>
          </div>
        </div>
      </div>
      <div class="explain">
        <dl>
          <dt>1、基本要求</dt>
          <dd>建议：所有图片都是本宝贝相关的图片。</dd>
        </dl>
        <dl>
          <dt>2、图片大小要求：</dt>
          <dd>（1）建议使用宽度超过480像素、高度超过960像素的图片；</dd>
          <dd>（2）格式为：jpg\gif\png；</dd>
          <dd>举例：可以上传一张宽度为480，高度为960像素，格式为jpg的图片。</dd>
        </dl>
        <dl>
          <dt>3、文字要求：</dt>
          <dd>（1）每次插入文字不能超过500个字，标点、特殊字符按照一个字计算；</dd>
          <dd>（2）请手动输入文字，不要复制粘贴网页上的文字，防止出现乱码；</dd>
          <dd>（3）请注意特殊字符将会被转译。</dd>
          <dd>建议：不要添加太多的文字，这样看起来更清晰。</dd>
        </dl>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'EnEditArea',
    props: {
      mobileDescriptionList: {
        type: Array,
        default: () => []
      }
    },

    computed: {
      mobile_description_list: {
        get() {
          if (!this.mobileDescriptionList) return []
          return this.mobileDescriptionList
        },
        set(val) {
          this.updateList(val)
        }
      }
    },

    data() {
      return {
        /** 是否显示文本框 */
        isEditArea: false,

        /** 编辑的内容 */
        editAreaContent: [],

        /** 当前文本框内容 */
        editAreaText: '',

        /** 当前选中内容下标 */
        currentIndex: ''
      }
    },
    methods: {
      updateList(list) {
        this.$emit('change', list)
      },

      /** 文本框显示 */
      handleShowEditArea() {
        this.isEditArea = true
        this.$nextTick(() => {
          this.$refs.controlContent.scrollTop = this.$refs.controlContent.scrollHeight
        })
      },

      /** 内容编辑 */
      handleEditAreaContent(index) {
        this.mobile_description_list.forEach(item => { item.checked = false })
        this.mobile_description_list[index].checked = true
      },

      /** 上移 */
      handleMoveUpward(item, index) {
        this.mobile_description_list.forEach(item => { item.checked = false })
        if (index === 0) return
        this.mobile_description_list.splice(index - 1, 0, item)
        this.mobile_description_list.splice(index + 1, 1)
        this.$emit('change', this.mobile_description_list)
      },

      /** 下移 */
      handleMoveDown(item, index) {
        this.mobile_description_list.forEach(item => { item.checked = false })
        if (index === this.mobile_description_list.length - 1) return
        this.mobile_description_list.splice(index + 2, 0, item)
        this.mobile_description_list.splice(index, 1)
        this.$emit('change', this.mobile_description_list)
      },

      /** 编辑 */
      handleEdit(index) {
        this.mobile_description_list[index].edit_checked = true
      },

      /** 编辑 确定 */
      handleEditConfirm(item, index) {
        this.mobile_description_list[index].content = item
        this.mobile_description_list[index].edit_checked = false
        this.mobile_description_list.forEach(item => { item.checked = false })
        this.$emit('change', this.mobile_description_list)
      },

      /** 编辑 取消 */
      handleEditCancel(index) {
        this.mobile_description_list[index].edit_checked = false
        this.mobile_description_list.forEach(item => { item.checked = false })
      },

      /** 替换 */
      handleReplaceImageSuccess(response, file, fileList) {
        const index = this.mobile_description_list.findIndex((item) => (item.checked))
        this.mobile_description_list[index].content = response.url
        this.mobile_description_list.forEach(item => { item.checked = false })
        this.$emit('change', this.mobile_description_list)
      },

      /** 移除 */
      handleRemove(index) {
        this.mobile_description_list.splice(index, 1)
        this.$emit('change', this.mobile_description_list)
      },

      /** 确定 获取文本框内容 */
      handleEditArea() {
        this.mobile_description_list.push({
          type: 'text',
          content: this.editAreaText,
          checked: false,
          edit_checked: false
        })
        this.$emit('change', this.mobile_description_list)
        this.handleHideEditArea()
      },

      /** 取消 文本框隐藏 */
      handleHideEditArea() {
        this.isEditArea = false
        this.editAreaText = ''
      },

      /** 图片上传之前的校验  */
      beforeAvatarUpload(file) {
        const isType = file.type === 'image/jpeg' || file.type === 'image/jpg' || file.type === 'image/png'
        if (!isType) {
          this.$message.error('上传图片只能是 JPG/JPEG/PNG 格式!')
        }
        const isSize = new Promise((resolve, reject) => {
          let _URL = window.URL || window.webkitURL
          let img = new Image()
          img.onload = () => {
            let valid = img.width >= 480 && img.height >= 960
            valid ? resolve() : reject()
          }
          img.src = _URL.createObjectURL(file)
        }).then(() => {
          return file
        }, () => {
          this.$message.error('建议使用宽度超过480像素、高度超过960像素的图片')
          return Promise.reject()
        })
        return isType && isSize
      },

      /** 图片上传成功时的钩子 */
      handleImageSuccess(response, file, fileList) {
        console.log(this.mobile_description_list)
        this.mobile_description_list.push({
          type: 'image',
          content: response.url,
          checked: false,
          edit_checked: false
        })
        this.$emit('change', this.mobile_description_list)
      },

      /** 图片上传失败时的钩子 */
      handleError() {
        this.$message.error('上传失败!')
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
.ncsc-mobile-editor {
  display: block;
  overflow: hidden;
  .title-bar {
    display: block;
    height: 50px;
    background-color: #F5F5F5;
    border-style: solid;
    border-color: #E1E1E1;
    border-width: 1px 1px 0 1px;
  }
  .container {
    position: relative;
    z-index: 1;
    display: block;
    width: 100%;
    height: 494px;
    background-color: #777;
    .pannel {
      position: absolute;
      z-index: 1;
      top: 0;
      left: 50%;
      margin-left: -171px;
      width: 330px;
      height: 493px;
      border: solid 1px #DDD;
      background-color: #FFF;
      .control-panel {
        height: 430px;
        overflow-x: hidden;
        overflow-y: scroll;
        .module {
          background: #fff;
          width: 300px;
          margin: 10px 0 0;
          position: relative;
        }
        .current {
          min-height: 40px;
          .tools {
            display: block;
          }
          .cover {
            display: block;
          }
        }
        .tools {
          display: none;
          position: absolute;
          z-index: 20;
          top: 10px;
          right: 10px;
          a {
            line-height: 25px;
            color: #000;
            background: #fff;
            float: left;
            padding: 0 10px;
            margin-right: 1px;
          }
        }
        .content {
          .text-div {
            line-height: 150%;
            word-wrap: break-word;
            color: #666;
            font-size: 12px;
          }
          img {
            max-width: 300px;
          }
        }
        .cover {
          background-color: #000;
          display: none;
          width: 100%;
          height: 100%;
          left: 0;
          opacity: 0.5;
          position: absolute;
          top: 0;
        }
      }
      .add-btn {
        position: absolute;
        z-index: 2;
        bottom: 0;
        left: 0;
        right: 0;
        background: none repeat scroll 0 0 #ececec;
        height: 60px;
        overflow: hidden;
        .btn {
          text-align: center;
          width: 50%;
          height: 50px;
          margin: 5px 0;
          float: left;
          a {
            display: block;
            height: 50px;
            color: #999;
          }
          i {
            font-size: 24px;
            line-height: 30px;
            height: 30px;
          }
          p {
            font-size: 14px;
            line-height: 20px;
            height: 20px;
            margin: 0;
          }
        }
      }
    }
    .explain {
      display: block;
      position: absolute;
      z-index: 1;
      bottom: 0;
      left: 50%;
      right: 20px;
      margin: 0 0 0 171px;
      font-size:12px;
      color: #fff;
    }
  }
  .ncsc-mea-text {
    width: 320px;
    margin: 10px 0 0;
    background-color: #F5F5F5;
    .text-tip {
      color: #333;
      line-height: 20px;
      height: 20px;
      padding: 5px;
      em {
        color: #F60;
        margin: 0 2px;
        font-style: normal;
      }
    }
    .textarea {
      width: 290px;
      height: 80px;
      resize: none;
      margin: 0 0 0 5px;
      border: solid 1px #E1E1E1;
    }
    .textarea:focus{
      outline: none;
    }
    .button {
      padding: 10px 20px;
      text-align: center;
    }
  }
}
</style>
