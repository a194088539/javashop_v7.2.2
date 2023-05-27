import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  Picker,
} from 'react-native';
import {connect} from 'react-redux';

class MyTestScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '测试也',
      state: {
        language: 'java',
      },
    };
  };

  render() {
    return (
      <View style={styles.container}>
        <Text>商品组件-居中商品名称</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
});

export default connect()(MyTestScene);
