import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  TextInput,
  Image,
  Button,
} from 'react-native';
import {connect} from 'react-redux';
import * as API_Member from '../../apis/members';
import {F16Text} from '../../widgets/Text';
import {Screen} from '../../utils';
import {BigButton, Checkbox} from '../../widgets';
import {navigate} from '../../navigator/NavigationService';
import {messageActions} from '../../redux/actions';
import {DeviceEventEmitter} from 'react-native';
class MyAnswerScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '我来回答',
    };
  };

  constructor(props) {
    super(props);
    this.ask = this.props.navigation.state.params.ask;
    this.state = {
      reply_content: '',
      anonymous: true,
      ask_id: this.ask.ask_id,
    };
  }

  _onContentChangeText = reply_content => this.setState({reply_content});

  _onSetAnonymous = value => {
    let {anonymous} = this.state;
    this.setState({anonymous: !anonymous});
  }

  _save = async () => {
    console.log('asdasdasd')
    let {reply_content, anonymous, ask_id} = this.state;
    let {navigation, dispatch} = this.props;
    if (!reply_content) {
      dispatch(messageActions.error('回复内容不能为空！'));
      return;
    }
    console.log(reply_content.length)
    if (reply_content.length < 3) {
      dispatch(messageActions.error('回复内容太少！'));
      return;
    }
    const params = JSON.parse(JSON.stringify(this.state));
    params.anonymous = anonymous ? 'YES' : 'NO';
    await API_Member.saveReply(params, ask_id);
    navigation.goBack();
    //发送刷新通知给列表
    DeviceEventEmitter.emit('refresh', {
      newMessage: 'refresh',
    });
  };

  render() {
    const {anonymous} = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.g_container}>
          <Image
            style={styles.g_item_image}
            source={{
              uri: this.ask.goods_img,
            }}
          />
          <View style={styles.g_item_detail}>
            <F16Text numberOfLines={2}>{this.ask.goods_name}</F16Text>
          </View>
        </View>
        <F16Text style={styles.q_text} numberOfLines={5}>
          {this.ask.ask_content}
        </F16Text>
        <View style={styles.a_container}>
          <TextInput
            style={styles.a_container_text}
            multiline={true}
            maxLength={120}
            minLength={3}
            placeholder={'请输入您的回答，字数请控制在3-120个。'}
            onChangeText={this._onContentChangeText}
          />
        </View>
        <View style={styles.box_container}>
          <Checkbox
            checked={anonymous}
            label={'匿名回答'}
            labelStyle={styles.btn_label}
            onPress={this._onSetAnonymous}
          />
        </View>
        <View style={styles.btn_view}>
          <BigButton
            style={styles.big_btn}
            title="发布我的回答"
            onPress={this._save}
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
  },
  g_container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    width: Screen.width,
    height: 90,
    paddingHorizontal: 10,
    paddingVertical: 15,
    backgroundColor: '#FFFFFF',
    marginTop: 1,
  },
  g_item_detail: {
    justifyContent: 'center',
    width: Screen.width - 20 - 90,
    height: 80,
    marginLeft: 15,
  },
  g_item_image: {
    width: 80,
    height: 80,
    borderRadius: 3,
  },
  q_text: {
    fontSize: 18,
    fontWeight: '600',
    backgroundColor: '#FFFFFF',
    paddingLeft: 10,
  },
  a_container: {
    height: 113,
    backgroundColor: '#FFFFFF',
  },
  a_container_text: {
    margin: 10,
    height: 100,
    borderColor: '#afb4b4',
    borderWidth: 1,
    textAlignVertical: 'top',
  },
  box_container: {
    marginLeft: 10,
    backgroundColor: '#FFFFFF',
  },
  btn_view: {
    flex: 1,
    flexDirection: 'row',
  },
  big_btn: {
    width: Screen.width,
  },
});

export default connect()(MyAnswerScene);
