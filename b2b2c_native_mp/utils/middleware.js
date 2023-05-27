// 登录验证 中间件
function identityFilter(pageObj) {
  if (pageObj.onShow) {
    let _onShow = pageObj.onShow
    pageObj.onShow = function () {
      if (!wx.getStorageSync('refresh_token')) {
        // 跳转到登录页
        wx.navigateTo({ url: "/pages/auth/login/login" })
      } else {
        // 获取页面实例，防止this劫持
        let currentInstance = getPageInstance()
        _onShow.call(currentInstance)
      }
    }
  }
  return pageObj
}

// 获取当前页实例
function getPageInstance() {
  const pages = getCurrentPages()
  return pages[pages.length - 1]
}

exports.identityFilter = identityFilter
