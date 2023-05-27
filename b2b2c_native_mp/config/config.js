// 开发环境deveploment  生产环境production
const env = 'deveploment'
/**
 * base    : 基础业务API
 * buyer   : 买家API
 * seller  : 商家中心API
 * admin   ：后台管理API
 */
const api = {
  // 开发环境
  dev: {
    base: 'https://base-api-v721.javamall.com.cn:81',
    buyer: 'https://buyer-api-v721.javamall.com.cn:81',
    seller: 'https://seller-api-v721.javamall.com.cn:81',
    admin: 'https://admin-api-v721.javamall.com.cn:81',
    zhibo: 'http://192.168.2.111:7007'
  },
  // 生产环境
  pro: {
    base: 'https://api.javamall.com.cn/base',
    buyer: 'https://api.javamall.com.cn/buyer',
    seller: 'https://api.javamall.com.cn/seller',
    admin: 'https://api.javamall.com.cn/admin'
  }
}

module.exports = {
  api: env === 'deveploment' ? api.dev : api.pro,
  env,
  /**
   * 直播功能开关
   * 如果您需要开启直播相关功能，请设置为true
   */
  is_liveVideo: true
}


