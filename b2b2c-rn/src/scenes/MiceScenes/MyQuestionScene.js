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

class MyQuestionScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '提问',
    };
  };

  constructor(props) {
    super(props);
    this.goods = this.props.navigation.state.params.goods;
    this.state = {
      ask_content: '',
      anonymous: 'YES',
      goods_id: this.goods.goods_id,
    };
  }

  _onContentChangeText = ask_content => this.setState({ask_content});

  _save = async () => {
    let {ask_content, anonymous, goods_id} = this.state;
    let {navigation} = this.props;
    if (ask_content == null) {
      messageActions.error('提问内容不能为空！');
    }
    const params = JSON.parse(JSON.stringify(this.state));
    await API_Member.saveQuestion(params, this.goods.goods_id);
    navigation.goBack();
  };

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.g_container}>
          <Image
            style={styles.g_item_image}
            source={{
              uri: this.goods.thumbnail,
            }}
          />
          <View style={styles.g_item_detail}>
            <F16Text numberOfLines={2}>{this.goods.goods_name}</F16Text>
          </View>
        </View>
        <View style={styles.a_container}>
          <TextInput
            style={styles.a_container_text}
            multiline={true}
            maxLength={120}
            minLength={3}
            placeholder={'请输入您的提问，字数请控制在3-120个。'}
            onChangeText={this._onContentChangeText}
          />
        </View>
        <View style={styles.box_container}>
          <Checkbox
            checked={true}
            label={'匿名提问'}
            labelStyle={styles.btn_label}
          />
        </View>
        <View style={styles.btn_view}>
          <BigButton
            style={styles.big_btn}
            title="发布我的提问"
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

export default connect()(MyQuestionScene);
