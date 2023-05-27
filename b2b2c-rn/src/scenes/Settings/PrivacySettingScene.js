/**
 * Created by Andste on 2018/10/25.
 */
import React, {Component} from 'react';
import {
  View,
  Text,
  Platform,
  ScrollView,
  StyleSheet,
  Alert,
} from 'react-native';
import Toast from 'react-native-root-toast';
import Permissions, {
  check,
  PERMISSIONS,
  RESULTS,
  request,
  openSettings,
} from 'react-native-permissions';
import {Cell} from '../../widgets';

export default class PrivacySettingScene extends Component {
  static navigationOptions = {
    title: '隐私设置',
  };

  constructor(props) {
    super(props);
    this.state = {
      camera: null,
      photo: null,
      contacts: null,
    };
  }

  componentDidMount() {
    this._checkAllPermissions();
  }

  _checkAllPermissions = async () => {
    if (Platform.OS === 'ios') {
      const perms = await Promise.all([
        check(PERMISSIONS.IOS.CAMERA),
        check(PERMISSIONS.IOS.PHOTO_LIBRARY),
        check(PERMISSIONS.IOS.CONTACTS),
      ]);
      await this.setState({
        camera: perms[0],
        photo: perms[1],
        contacts: perms[2],
      });
    } else if (Platform.OS === 'android') {
      const perms = await Promise.all([
        check(PERMISSIONS.ANDROID.CAMERA),
        check(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION),
        check(PERMISSIONS.ANDROID.READ_CONTACTS),
        check(PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE),
      ]);
      await this.setState({
        camera: perms[0],
        photo: perms[1],
        contacts: perms[2],
      });
    }
  };

  _permissionStatus = type => {
    switch (type) {
      case null:
        return '检查中';
      case 'authorized':
        return '已开启';
      default:
        return '去设置';
    }
  };

  _requestPermission = async type => {
    if (!type) {
      return;
    }
    if (Platform.OS === 'ios') {
      if (this.state[type] === 'undetermined') {
        this.setState({[type]: await Permissions.request(type)});
      } else {
        Permissions.openSettings();
      }
    } else {
      let typeStr = '';
      let message = '';
      if (type === 'camera') {
        typeStr = '相机';
        message = '扫码、图片评论和修改头像需要访问您的相机。';
        type = PERMISSIONS.ANDROID.CAMERA;
      } else if (type === 'photo') {
        typeStr = '相册';
        message = '图片评论和修改头像需要访问您的相册。';
        type = PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION;
      } else {
        typeStr = '联系人';
        message = '为了方便您添加收货人信息，我们需要访问您的联系人。';
        type = PERMISSIONS.ANDROID.READ_CONTACTS;
      }
      const result = await Permissions.request(type);
      switch (result) {
        case RESULTS.UNAVAILABLE:
          //不支持该功能
          Alert.alert('', '您的设备不支持该功能', [{text: '确定'}]);
          return;
        case RESULTS.DENIED:
          //该权限尚未被请求、被拒绝，但可请求
          let req = await request(PERMISSIONS.ANDROID.WRITE_EXTERNAL_STORAGE);
          if (req == RESULTS.DENIED) {
            Toast.show('您已经拒绝授权', {
              duration: Toast.durations.SHORT,
              position: Toast.positions.CENTER,
              animation: true, //不显示动画
            });
            return;
          }
          break;
        case RESULTS.GRANTED:
          //授予权限
          break;
        case RESULTS.BLOCKED:
          //该权限被拒绝
          Alert.alert('提示', message, [
            {
              text: '关闭',
              onDismiss: () => {},
            },
            {
              text: `去设置开启${typeStr}权限`,
              onPress: () => {
                openSettings();
              },
            },
          ]);
      }
      console.log({[type]: result});
      this.setState({[type]: result});
    }
  };

  render() {
    const {camera, photo, contacts} = this.state;
    return (
      <ScrollView style={styles.container}>
        <Cell
          title="允许访问相机"
          onPress={() => {
            this._requestPermission('camera');
          }}
          label={this._permissionStatus(camera)}
        />
        <PlaceText title="扫码，更换头像功能需要此权限" />
        <Cell
          title="允许访问相册"
          onPress={() => {
            this._requestPermission('photo');
          }}
          label={this._permissionStatus(photo)}
        />
        <PlaceText title="更换头像功能需要此权限" />
        <Cell
          title="允许访问通讯录"
          onPress={() => {
            this._requestPermission('contacts');
          }}
          label={this._permissionStatus(contacts)}
        />
        <PlaceText title="方便您添加其它收货人信息" />
      </ScrollView>
    );
  }
}

const PlaceText = ({title}) => (
  <View style={styles.place_view}>
    <Text style={styles.place_text}>{title}</Text>
  </View>
);

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  place_view: {
    height: 35,
    paddingHorizontal: 10,
    justifyContent: 'center',
  },
  place_text: {
    fontSize: 14,
    color: '#777777',
  },
});
