import React, {Component} from 'react';
import {
  FlatList,
  Image,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Text,
  View,
  Picker,
  TextInput,
} from 'react-native';
import {connect} from 'react-redux';
import * as API_AfterSale from '../../../apis/after-sale';
import {Foundation, Screen} from '../../../utils';

/**
 * 售后日志
 */
class AfterSaleLog extends Component {
  static navigationOptions = {
    title: '售后日志',
  };

  constructor(props, context) {
    super(props, context);
    this.service_sn = this.props.navigation.state.params.service_sn;
    this.state = {
      loading: false,
      dataList: [],
    };
    this.params = {
      service_sn: this.service_sn,
    };
  }

  componentDidMount() {
    this._getAfterSaleLog();
  }

  /**
   * 读取售后日志
   * @returns {Promise<void>}
   * @private
   */
  _getAfterSaleLog = async () => {
    const {dataList} = this.state;
    try {
      const res = (await API_AfterSale.getAfterSaleLog(this.params)) || [];
      const {data = []} = res;
      await this.setState({
        loading: false,
        noData: !data[0],
        dataList: res,
      });
    } catch (error) {
      await this.setState({
        loading: false,
      });
    }
  }

  render() {
    return (
      <View style={styles.container}>
        {this.state.dataList.map((item, index) => {
          return (
            <View style={styles.container_view}>
              <Text style={styles.view_head}>处理信息</Text>
              <Text style={styles.view_detail}>{item.log_detail}</Text>
              <Text style={styles.view_time}>
                {item.operator} (
                {Foundation.unixToDate(item.log_time, `yyyy-MM-dd hh:mm:ss`)})
              </Text>
            </View>
          );
        })}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#FFFFFF',
    width: Screen.width,
  },
  container_view: {
    height: 100,
    width: Screen.width,
    marginLeft: 15,
    marginTop: 10,
  },
  view_head: {
    fontSize: 18,
    height: 30,
  },
  view_detail: {
    fontWeight: '600',
    maxHeight: 100,
    width: Screen.width - 50,
  },
  view_time: {
    color: '#565759',
    height: 30,
    lineHeight: 30,
  },
});

export default connect()(AfterSaleLog);
