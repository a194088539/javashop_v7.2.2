/**
 * Created by Andste on 2019-01-30.
 */
import React, {PureComponent} from 'react';
import {
  Alert,
  View,
  Image,
  Text,
  TouchableOpacity,
  StyleSheet,
  Linking,
} from 'react-native';
import {connect} from 'react-redux';
import {messageActions, userActions} from '../../redux/actions';
import {navigate} from '../../navigator/NavigationService';
import {appId} from '../../../config';
import {Screen} from '../../utils';
import * as API_Connect from '../../apis/connect';
import * as Wechat from 'react-native-wechat';
import * as QQAPI from 'react-native-qq';
import Alipay from '@0x5e/react-native-alipay';
// import * as WeiboAPI from 'react-native-mweibo';

class LoginByConnect extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isWXAppInstalled: false,
      isQQAppInstalled: false,
      isAlipayAppInstalled: false,
      isWeiBoAppInstalled: false,
    };
  }

  componentDidMount() {
    //查看是否安装微信
    this._getIsAPP('weixin://', 1);
    //查看是否安装QQ
    this._getIsAPP('mqq://', 2);
    //查看是否安装支付宝
    this._getIsAPP('alipay://', 3);
  }

  _getIsAPP = (appUrl, num) => {
    Linking.canOpenURL(appUrl).then(supported => {
      let flag = false;
      if (supported) {
        flag = true;
      } else {
        flag = false;
      }
      if (num === 1) {
        this.setState({isWXAppInstalled: flag});
      } else if (num === 2) {
        this.setState({isQQAppInstalled: flag});
      } else if (num === 3) {
        this.setState({isAlipayAppInstalled: flag});
      } else if (num === 4) {
        this.setState({isWeiBoAppInstalled: flag});
      }
    });
  };

  /**
   * 获取微信授权
   * @returns {Promise<void>}
   * @private
   */
  _getWechatAuth = async () => {
    const {dispatch} = this.props;
    const isWXAppInstalled = await Wechat.isWXAppInstalled();
    if (!isWXAppInstalled) {
      dispatch(messageActions.error('您的设备没有安装微信！'));
      return;
    }
    try {
      const res = await Wechat.sendAuthRequest('snsapi_userinfo');
      let params = {
        appid: appId.wechatAppId,
        secret: appId.wechatAppSecret,
        code: res.code,
        grant_type: 'authorization_code',
      };
      const _userinfo = await API_Connect.getWechatUserInfo(params);
      await this._getOpenidBinded(_userinfo.unionid, 'WECHAT');
    } catch (e) {
      dispatch(messageActions.error('获取微信授权失败，请稍后再试！'));
    }
  };

  /**
   * 获取QQ授权
   * @private
   */
  _getQQAuth = async () => {
    const {dispatch} = this.props;
    try {
      const res = await QQAPI.login('get_simple_userinfo');
      await this._getOpenidBinded(res.openid, 'QQ');
    } catch (e) {
      dispatch(messageActions.error('获取QQ授权失败，请稍后再试！'));
    }
  };

  /**
   * 获取微博授权
   * @returns {Promise<void>}
   * @private
   */
  _getWeiboAuth = async () => {
    const {dispatch} = this.props;
    try {
      // const res = await WeiboAPI.login({
      //   scope: 'all',
      //   redirectURI: appId.weiboRedirectURI,
      // });
      // await this._getOpenidBinded(res.userID, 'WEIBO');
    } catch (e) {
      let errMsg = '获取微博授权失败';
      if (e.message === '用户取消发送') {
        errMsg = '用户取消授权';
      }
      dispatch(messageActions.error(errMsg));
    }
  };

  /**
   * 获取支付宝授权
   * @returns {Promise<void>}
   * @private
   */
  _getAlipayAuth = async () => {
    const {dispatch} = this.props;
    const infoObj = {
      apiname: 'com.alipay.account.auth',
      method: 'alipay.open.auth.sdk.code.get',
      app_name: 'mc',
      biz_type: 'openservice',
      product_id: 'APP_FAST_LOGIN',
      scope: 'kuaijie',
      target_id: new Date().getTime().toString(),
      auth_type: 'AUTHACCOUNT',
      sign_type: 'RSA2',
      ...appId.alipayConfig,
    };
    let infoArray = [];
    for (let key in infoObj) {
      infoArray.push(`${key}=${infoObj[key]}`);
    }
    infoObj.sign = infoArray.join('&');
    try {
      const response = await Alipay.authWithInfo(infoObj.sign);
      if (String(response.resultStatus) === '9000') {
        const userId = response.result.match(/user_id=(\d+)/)[1];
        await this._getOpenidBinded(userId, 'ALIPAY');
      } else if (String(response[''] === '6001')) {
        dispatch(messageActions.error('用户取消授权'));
      } else {
        throw Error(response.memo);
      }
    } catch (e) {
      dispatch(messageActions.error('获取支付宝授权失败，请稍后再试！'));
    }
  };

  /**
   * 获取openid是否已绑定账号
   * @param code
   * @param type
   * @returns {Promise<void>}
   * @private
   */
  _getOpenidBinded = async (code, type) => {
    const {dispatch} = this.props;
    try {
      const res = await API_Connect.getOpenidBinded(code, type);
      if (res.is_bind) {
        await dispatch(userActions.loginSuccessAction(res));
        dispatch(userActions.getUserAction(this.props.nav.goBack));
        dispatch(messageActions.success('登录成功！'));
      } else {
        const params = {openid: code, type};
        Alert.alert('提示', '您暂未绑定任何账户，直接登录绑定', [
          {text: '登录', onPress: () => this.props.loginByConnect(params)},
        ]);
      }
    } catch (e) {
      dispatch(messageActions.error('获取绑定状态失败！'));
    }
  };

  /**
   * 点击第三方登录图标
   * @param type
   * @private
   */
  _onPress = type => {
    switch (type) {
      case 'wechat':
        this._getWechatAuth();
        break;
      case 'qq':
        this._getQQAuth();
        break;
      case 'weibo':
        this._getWeiboAuth();
        break;
      case 'alipay':
        this._getAlipayAuth();
        break;
    }
  };

  render() {
    const {
      isWXAppInstalled,
      isQQAppInstalled,
      isAlipayAppInstalled,
      isWeiBoAppInstalled,
    } = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.title}>
          <View style={styles.title_line} />
          <Text style={styles.title_text}>其它方式登录</Text>
          <View style={styles.title_line} />
        </View>
        <View style={styles.icons}>
          {isQQAppInstalled ? (
            <ConnectIcon
              icon={require('../../images/icon-qq-logo.png')}
              onPress={() => {
                this._onPress('qq');
              }}
            />
          ) : (
            undefined
          )}
          {isWXAppInstalled ? (
            <ConnectIcon
              icon={require('../../images/icon-wechat-logo.png')}
              onPress={() => {
                this._onPress('wechat');
              }}
            />
          ) : (
            undefined
          )}
          {isAlipayAppInstalled ? (
            <ConnectIcon
              icon={require('../../images/icon-alipay-logo.png')}
              onPress={() => {
                this._onPress('alipay');
              }}
            />
          ) : (
            undefined
          )}
          {/*<ConnectIcon*/}
          {/*  icon={require('../../images/icon-weibo-logo.png')}*/}
          {/*  onPress={() => {*/}
          {/*    this._onPress('weibo');*/}
          {/*  }}*/}
          {/*/>*/}
        </View>
      </View>
    );
  }
}

const ConnectIcon = ({icon, ...props}) => {
  return (
    <TouchableOpacity {...props}>
      <Image style={styles.icon} source={icon} />
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    marginTop: 100,
    paddingHorizontal: 20,
  },
  title: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title_line: {
    width: 85,
    height: Screen.onePixel,
    backgroundColor: '#CCC',
  },
  title_text: {
    color: '#8c8c8c',
    fontSize: 16,
  },
  icons: {
    paddingHorizontal: 20,
    marginTop: 20,
    flexDirection: 'row',
    justifyContent: 'space-around',
    alignItems: 'center',
  },
  icon: {
    width: 45,
    height: 45,
  },
});

export default connect()(LoginByConnect);
