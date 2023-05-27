/**
 * Created by lylyulei on 2020-04-20 16:18.
 */
import React, {Component} from 'react';
import {
  View,
  Text,
  Keyboard,
  TextInput,
  StatusBar,
  StyleSheet,
} from 'react-native';
import {connect} from 'react-redux';
import {messageActions, userActions} from '../../redux/actions';
import {colors} from '../../../config';
import {Screen, RegExp} from '../../utils';
import {DismissKeyboardHOC, ImageCodeModal} from '../../components';
import {BigButton} from '../../widgets';
import * as API_Safe from '../../apis/safe';

const DismissKeyboardView = DismissKeyboardHOC(View);

class ChangePasswordScene extends Component {
  static navigationOptions = {
    title: '修改支付密码',
  };

  constructor(props) {
    super(props);
    const {params = {}} = props.navigation.state;
    this.params = params;
    this.state = {
      imageCodeModal: false,
      password: '',
      rep_password: '',
    };
  }

  _onPasswordChange = text => this.setState({password: text});
  _onRePasswordChange = text => this.setState({rep_password: text});
  _nextStep = async () => {
    const {password, rep_password} = this.state;
    const {dispatch} = this.props;
    if (!RegExp.PAYPWD.test(password)) {
      dispatch(messageActions.error('密码格式不正确，请输入6位数字！'));
      return;
    }
    if (password !== rep_password) {
      dispatch(messageActions.error('两次密码输入不一致！'));
      return;
    }
    this.setState({imageCodeModal: true});
  };

  _imageCodeConfirm = async image_code => {
    Keyboard.dismiss();
    await this.setState({imageCodeModal: false});
    const {password} = this.state;
    const {dispatch, navigation} = this.props;
    // 设置支付密码
    await API_Safe.setDepositePwd(password);
    dispatch(messageActions.success('密码设置成功，请牢记您的支付密码！'));
    navigation.navigate('Mine');
  };

  render() {
    const {imageCodeModal, password, rep_password} = this.state;
    return (
      <View style={styles.container}>
        <StatusBar barStyle="dark-content" />
        <DismissKeyboardView>
          <StatusBar barStyle="dark-content" />
          {imageCodeModal ? (
            <ImageCodeModal
              type="SET_PAY_PWD"
              isOpen={imageCodeModal}
              onClosed={() => this.setState({imageCodeModal: false})}
              confirm={this._imageCodeConfirm}
            />
          ) : (
            undefined
          )}
          <View style={styles.register_form}>
            <View style={[styles.register_mobile, {marginTop: 10}]}>
              <View style={styles.register_mobile_area}>
                <Text style={{fontSize: 16}}>新的密码</Text>
              </View>
              <View style={{width: Screen.width - 40 - 75}}>
                <TextInput
                  style={styles.register_mobile_input}
                  value={password}
                  maxLength={6}
                  multiline={false}
                  onChangeText={this._onPasswordChange}
                  secureTextEntry={true}
                  returnKeyType="done"
                  placeholder="6位数字"
                  placeholderTextColor="#777777"
                  clearButtonMode="while-editing"
                  underlineColorAndroid="transparent"
                />
              </View>
            </View>
            <View style={[styles.register_mobile, {marginTop: 10}]}>
              <View style={styles.register_mobile_area}>
                <Text style={{fontSize: 16}}>重复密码</Text>
              </View>
              <View style={{width: Screen.width - 40 - 75}}>
                <TextInput
                  style={styles.register_mobile_input}
                  value={rep_password}
                  maxLength={6}
                  multiline={false}
                  onChangeText={this._onRePasswordChange}
                  secureTextEntry={true}
                  returnKeyType="done"
                  placeholder="请确认密码"
                  placeholderTextColor="#777777"
                  clearButtonMode="while-editing"
                  underlineColorAndroid="transparent"
                />
              </View>
            </View>
            <BigButton
              style={{
                width: Screen.width - 40,
                height: 44,
                borderRadius: 3,
                marginTop: 30,
              }}
              title={'设置密码'}
              disabled={!password || !rep_password}
              onPress={this._nextStep}
            />
          </View>
        </DismissKeyboardView>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingLeft: 20,
    paddingRight: 20,
  },
  register_form: {
    marginTop: 50,
  },
  register_mobile: {
    flexDirection: 'row',
    width: Screen.width - 40,
    height: 44,
    backgroundColor: '#FFFFFF',
    borderRadius: 2,
  },
  register_mobile_area: {
    justifyContent: 'center',
    alignItems: 'center',
    width: 75,
    height: 44,
  },
  register_mobile_input: {
    padding: 0,
    height: 44,
    fontSize: 16,
    color: colors.text,
  },
});

export default connect()(ChangePasswordScene);
