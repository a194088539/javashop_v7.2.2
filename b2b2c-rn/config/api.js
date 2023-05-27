/**
 * Created by Andste on 2018/9/30.
 * 基础服务和买家端API
 */

//参考文档：https://developers.weixin.qq.com/doc/oplatform/Mobile_App/Access_Guide/iOS.html
export const wechat_universal_link = 'https://buyer.javamall.com.cn/wechat';

// 生产环境
export const base = 'https://api.javamall.com.cn/base';
export const buyer = 'https://api.javamall.com.cn/buyer';
export const web_domain = 'https://m-buyer.javamall.com.cn';

// 测试环境
// export const base = 'https://javashop.natapp4.cc/base-api';
// export const buyer = 'https://javashop.natapp4.cc/buyer-api';
// export const web_domain = 'https://javashopm.natapp4.cc';

// 开发环境
// export const base = 'http://192.168.2.236:7000';
// export const buyer = 'http://192.168.2.236:7002';
// export const web_domain = 'http://192.168.2.173:3001';

// 后端API模式
// export const api_model = 'dev';
export const api_model = 'pro';
// API访问超时时间【ms|毫秒】
export const timeout = 8000;
