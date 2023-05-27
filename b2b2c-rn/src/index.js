/**
 * @format
 * Created by Andste on 2018/9/29.
 */
import 'react-native-gesture-handler';
import {gestureHandlerRootHOC} from 'react-native-gesture-handler';
import {AppRegistry, YellowBox} from 'react-native';
import * as WeChat from 'react-native-wechat';
import App from './App';
import {appId} from '../config';
import {name as appName} from '../app.json';
import {wechat_universal_link} from '../config/api';

if (!__DEV__) {
  global.console = {
    info: () => {},
    log: () => {},
    assert: () => {},
    warn: () => {},
    debug: () => {},
    error: () => {},
    time: () => {},
    timeEnd: () => {},
  };
}
// console.ignoredYellowBox = ['Warning: BackAndroid is deprecated. Please use BackHandler instead.','source.uri should not be an empty string','Invalid props.style key']
// console.disableYellowBox = true

YellowBox.ignoreWarnings(['`-[RCTRootView cancelTouches]`']);
WeChat.registerApp(appId.wechatAppId, wechat_universal_link);
AppRegistry.registerComponent(appName, () => gestureHandlerRootHOC(App));
AppRegistry.registerComponent(appName, () => App);
