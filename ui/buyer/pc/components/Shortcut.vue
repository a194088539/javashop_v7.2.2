<template>
  <div id="shortcut">
    <div class="w">
      <div v-if="user" class="login-status logined">
        <span>您好<nuxt-link to="/member">{{ user.nickname || user.uname }}</nuxt-link>，欢迎来到</span>
        <nuxt-link to="/">{{ site.site_name }} </nuxt-link>
        <span>[<a href="javascript:" @click="handleLogout">退出</a>]</span>
      </div>
      <div v-else class="login-status">
        <span>您好，欢迎来到</span>
        <nuxt-link to="/" style="color: #333">{{ site.site_name }}</nuxt-link>
        <!--  -->
        <span>[<nuxt-link :to="'/login' + MixinForward">登录</nuxt-link>] [<nuxt-link :to="'/register' + MixinForward">注册</nuxt-link>]</span>
      </div>
      <ul class="links">
        <li>
          <div class="dt">
            <a :href="user.have_shop ? MixinDomain.seller : '/shop/apply/'">{{user.have_shop ? '我的店铺' : '商家入驻'}}</a>
          </div>
        </li>
        <li class="spacer"></li>
        <li class="dorpdown">
          <div class="dt hs-icon">
            <nuxt-link to="/member/my-order">我的订单</nuxt-link>
            <i class="iconfont ea-icon-arrow-down"></i>
          </div>
          <div class="dd dorpdown-layer">
            <dl>
              <dd><nuxt-link to="/member/my-order?order_status=WAIT_PAY">待付款订单</nuxt-link></dd>
              <dd><nuxt-link to="/member/my-order?order_status=WAIT_ROG">待收货订单</nuxt-link></dd>
              <dd><nuxt-link to="/member/my-order?order_status=WAIT_COMMENT">待评价订单</nuxt-link></dd>
            </dl>
          </div>
        </li>
        <li class="spacer"></li>
        <li class="dorpdown">
          <div class="dt hs-icon">
            <nuxt-link to="/member/goods-collection">我的收藏</nuxt-link>
            <i class="iconfont ea-icon-arrow-down"></i>
          </div>
          <div class="dd dorpdown-layer">
            <dl>
              <dd><nuxt-link to="/member/goods-collection">收藏的商品</nuxt-link></dd>
              <dd><nuxt-link to="/member/shop-collection?type=shop">收藏的店铺</nuxt-link></dd>
            </dl>
          </div>
        </li>
        <li class="spacer"></li>
        <li class="dorpdown">
          <div class="dt hs-icon">
            <a href="#">站点导航</a>
            <i class="iconfont ea-icon-arrow-down"></i>
          </div>
          <div class="dd dorpdown-layer">
            <dl>
              <dd><a href="/">商城首页</a></dd>
              <dd><a :href="user.have_shop ? MixinDomain.seller : '/shop/apply/'">{{user.have_shop ? '我的店铺' : '商家入驻'}}</a></dd>
              <dd><nuxt-link to="/member">个人中心</nuxt-link></dd>
            </dl>
          </div>
        </li>
        <li class="spacer"></li>
        <li class="dorpdown">
          <div class="dt">
            <nuxt-link to="/help">帮助中心</nuxt-link>
          </div>
        </li>
        <li class="spacer"></li>
        <li>
          <div class="dt">
            <nuxt-link to="/member/message-index">
              站内消息[{{ unreadMessageNum > 99 ? '99+' : unreadMessageNum }}]
            </nuxt-link>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
  import Storage from '@/utils/storage'
  import * as API_Message from '@/api/message'
  import { mapActions, mapGetters } from 'vuex'
  export default {
    name: 'EnShortcut',
    head() {
      return {
        meta: [
          { hid: 'keywords', name: 'keywords', content: this.site.keywords },
          { hid: 'description', name: 'description', content: this.site.descript }
        ]
      }
    },
    data() {
      return {
        message_total: 0
      }
    },
    computed: {
      ...mapGetters(['user', 'site', 'unreadMessageNum'])
    },
    mounted() {
      // 如果是首页，并且有uuid，那么替换掉cookie中的uuid，并且移除url中的uuid
      const { name, query } = this.$route;
      if (name === 'index' && query.uuid) {
        Storage.setItem('uuid', query.uuid);
        Storage.removeItem('uuid_connect');
        location.href = '/';
        return
      }
      // 如果有刷新Token，重新获取用户信息【第一次访问和用户刷新页面，会触发】
      if (Storage.getItem('refresh_token')) {
        this.getUserData()
      }
      this.GET_UnreadMessage()
    },
    methods: {
      ...mapActions({
        logout: 'user/logoutAction',
        getUserData: 'user/getUserDataAction',
        cleanCartStore: 'cart/cleanCartStoreAction',
        getUnreadMessageNum: 'getUnreadMessageNumAction'
      }),
      /** 账户登出 */
      handleLogout() {
        this.logout().then(() => {
          this.cleanCartStore();
          document.location.reload()
        })
      },
      /** 获取未读消息 */
      async GET_UnreadMessage() {
        if (!Storage.getItem('refresh_token')) return;
        await this.getUnreadMessageNum();
        // 消息轮询，5分钟查一次
        setTimeout(() => {
          this.GET_UnreadMessage()
        }, 1000 * 60 * 5)
      }
    }
  }
</script>

<style type="text/scss" lang="scss" scoped>
  @import "../assets/styles/color";
  #shortcut {
    border-bottom: 1px solid #ddd;
    background-color: #e3e4e5;
    .w {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 30px;
      line-height: 30px;
    }
    .login-status {
      a { color: #666 }
    }
    .logined a { color: #333 }
    a:hover {
      color: $color-main
    }
  }
  .links {
    z-index: 100;
    a {
      color: #999;
      display: block;
    }
    li {
      position: relative;
      float: left;
      &.spacer {
        overflow: hidden;
        margin: 11px 5px 0;
        width: 1px;
        height: 10px;
        background-color: #ccc;
      }
      &.dorpdown:hover {
        .dorpdown-layer {
          display: block;
        }
        .dt {z-index: 3;}
        .iconfont {transform: rotate(180deg);}
      }
      .dt {
        padding-left: 7px;
        padding-right: 7px;
        &.hs-icon {
          height: 28px;
          line-height: 28px;
          overflow: hidden;
          position: relative;
          z-index: 1;
          float: left;
          padding-right: 20px;
        }
        .iconfont {
          position: absolute;
          right: 5px;
          top: 10px;
          width: 12px;
          height: 12px;
          line-height: 12px;
          font-size: 12px;
          transition: all .3s ease-out;
        }
      }

      .dorpdown-layer {
        background-color: #FFF;
        display: none;
        width: 78px;
        border: solid 1px #F0F0F0;
        position: absolute;
        z-index: 1;
        top: 0;
        left: 0;
        padding-top: 30px;
        dd {
          cursor: pointer;
          &:hover {background-color: #f5f6f5;}
        }
        a {
          line-height: 24px;
          padding-left: 7px;
        }
      }
    }
  }
</style>
