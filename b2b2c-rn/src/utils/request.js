import {store} from '../redux/store';
import {
    messageActions,
    userActions,
    tokenActions,
    cartActions,
} from '../redux/actions';
import axios from 'axios';
import {api} from '../../config';
import {navigate} from '../navigator/NavigationService';
import * as Foundation from './Foundation';
import md5 from 'js-md5';
import jwt_decode from 'jwt-decode';

const qs = require('qs');

export const upload = api.base + '/uploaders';

const service = axios.create({
    timeout: api.timeout,
    baseURL: api.buyer,
});

// request拦截器
service.interceptors.request.use(async config => {
    // console.log('请求：' + config.url);
    const {needToken} = config;
    // 如果是put/post请求，用qs.stringify序列化参数
    const is_put_post = config.method === 'put' || config.method === 'post';
    const is_json = config.headers['Content-Type'] === 'application/json';
    if (is_put_post && is_json) {
        config.data = JSON.stringify(config.data);
    }
    if (is_put_post && !is_json && config.url !== upload) {
        config.data = qs.stringify(config.data, {arrayFormat: 'repeat'});
    }
    const {token, user} = store.getState();
    // uuid
    config.headers.uuid = user.uuid;
    // referer
    config.headers.Referer = api.web_domain;
    // 获取访问Token
    let accessToken = token.access_token;
    if (accessToken && needToken) {
        //去掉生产模式
        config.headers.Authorization = accessToken;
        // console.log('携带token:' + accessToken);
    }
    return config;
});

// respone拦截器
service.interceptors.response.use(
    response => response.data,
    error => {
        const error_res = error.response || {};
        const error_data = error_res.data || {};
        if (error.config.message !== false) {
            let _message = error.code === '' ? '连接超时，请稍后再试！' : null;
            _message = error_data.message || _message;
            store.dispatch(messageActions.error(_message));
        }
        if (error_res.status === 403) {
            console.log('状态403');
            cleanAllData();
            store.dispatch(messageActions.error('登录信息失效，请重新登录'));
            navigate('Login');
        }
        return Promise.reject(error);
    },
);

export const Method = {
    GET: 'GET',
    POST: 'POST',
    PUT: 'PUT',
    DELETE: 'DELETE',
};

/**
 * 内部的请求方法，使用了同步校验token
 * 如果access token过期，尝试使用refresh token换票
 * 如果换票失败则跳转至登录页
 * @param options
 * @returns {AxiosPromise<any>}
 */
async function innerRequest(options) {
    // 如果不需要token或是请求刷新token，不需要检查。
    if (!options.needToken || options.url.indexOf('passport/token') !== -1) {
        return service(options);
    }

    //同步校验token
    await checkToken();

    //继续请求
    return service(options);
}


export default function request(options) {
    return innerRequest(options);
}

/**
 * 检查token：
 * 1. user/accessToken/refreshToken都不存在。
 *    表示用户没有登录，放行所有API
 * 2. 不存在accessToken，但是user/refreshToken存在。
 *    表示accessToken过期，需要重新获取accessToken。
 *    如果重新获取accessToken返回token失效错误，说明已被登出。
 * @param options
 * @returns {Promise<any>}
 */
function checkToken() {
    return new Promise(async (resolve, reject) => {
        try {
            const {token, user: _user} = store.getState();
            // user
            const user = _user.user;
            // 访问Token
            const accessToken = token.access_token;
            // 刷新Token
            const refreshToken = token.refresh_token;

            // 现在时间戳
            const nowTime = parseInt(new Date().getTime() / 1000);
            // 访问令牌是否过期【不存在也视为过期】
            const accessTokenOverdue =
                !accessToken || nowTime >= jwt_decode(accessToken).exp;
            // 刷新令牌是否过期【不存在也视为过期】
            const refreshTokenOverdue =
                !refreshToken || nowTime >= jwt_decode(refreshToken).exp;


            // console.log('accessTokenOverdue:' + accessTokenOverdue);
            // console.log('refreshTokenOverdue:' + refreshTokenOverdue);

            /**
             * 如果user数据存在
             * 且accessToken和refreshToken都未过期
             * 说明必要条件都存在，可以直接通过，并且不需要后续操作。
             */
            if (user && !accessTokenOverdue && !refreshTokenOverdue) {
                return resolve();
            }

            /**
             * accessToken过期，但是refreshTokenOverdue未过期。
             * 说明用户已登录，只是accessToken过期，需要重新获取accessToken。
             * 如果没有needToken，说明不需要等待获取到新的accessToken后再请求。
             * 否则，需要等待
             */
            if (accessTokenOverdue && !refreshTokenOverdue) {

                // console.log('开始换票');
                // 开始请求新的Token，并加锁。
                let res = await getNewToken(refreshToken);

                // console.log('换票结果：');
                // console.log(res);
                await store.dispatch(
                    tokenActions.setAccessTokenAction(res.accessToken),
                );
                await store.dispatch(
                    tokenActions.setRefreshTokenAction(res.refreshToken),
                );
                return resolve();
            }

            return resolve();
        } catch (e) {
            console.error(e);
            reject();
        }
    });

}

/**
 * 通过接口换取新的token
 * @param refreshToken
 * @returns {Promise<unknown>}
 */
function getNewToken(refreshToken) {
    return request({
        url: 'passport/token',
        method: 'post',
        data: {refresh_token: refreshToken},
    });
}

/**
 * 清除数据
 * 清除购物车
 * 初始化用户信息
 * 移除AccessToken
 * 移除refreshToken
 */
function cleanAllData() {
    store.dispatch(cartActions.cleanCartAction());
    store.dispatch(userActions.initUserAction());
    store.dispatch(tokenActions.removeAccessTokenAction());
    store.dispatch(tokenActions.removeRefreshTokenAction());
    store.dispatch(messageActions.error('登录信息失效，请重新登录'));
}
