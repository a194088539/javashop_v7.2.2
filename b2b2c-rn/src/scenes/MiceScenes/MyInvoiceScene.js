import React, {Component} from 'react';
import {
  Alert,
  View,
  StatusBar,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
} from 'react-native';
import {connect} from 'react-redux';
import {messageActions} from '../../redux/actions';
import * as API_Member from '../../apis/members';
import MyHistoryItem from './MyHistoryItem';
import {BigButton, GoodsItemPro} from '../../widgets';
import {Foundation, Screen} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {colors} from '../../../config';
import {navigate} from '../../navigator/NavigationService';

class MyInvoiceScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '我的发票',
    };
  };

  constructor() {
    super();
    this.params = {
      page_no: 1,
      page_size: 10,
    };
    this.state = {
      loading: false,
      dataList: [],
    };
  }

  componentDidMount(): void {
    this._onGetReceiptList();
  }

  _onGetReceiptList = async () => {
    const {page_no} = this.params;
    const {dataList} = this.state;
    try {
      const res = (await API_Member.receiptList(this.params)) || [];
      const {data = []} = res;
      await this.setState({
        loading: false,
        dataList: page_no === 1 ? data : dataList.concat(data),
      });
    } catch (e) {
      await this.setState({
        loading: false,
      });
    }
  };

  render() {
    const {dataList} = this.state;
    return (
      <View style={styles.container}>
        {dataList.map((orderItem, index) => {
          return (
            <TouchableOpacity
              onPress={() => navigate('MyInvoiceDetail', {receipt: orderItem})}>
              <View style={styles.receipt_order_view}>
                <Text style={styles.receipt_order_text_view}>
                  订单号：{orderItem.order_sn}
                </Text>
                <Text style={styles.receipt_order_status_view}>
                  {orderItem.status == 0 ? '未开票' : '已开票'}
                </Text>
              </View>
              {orderItem.order_sku_list.length >= 0 ? (
                <View style={styles.receipt_detail_view}>
                  {orderItem.order_sku_list.map((sku, skuIndex) => {
                    return (
                      <GoodsItemPro
                        goods_img={sku.goods_image}
                        goods_name={sku.name}
                      />
                    );
                  })}
                </View>
              ) : (
                <View style={styles.receipt_detail_view}>
                  <Text>adasd</Text>
                </View>
              )}
            </TouchableOpacity>
          );
        })}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  receipt_order_view: {
    backgroundColor: '#FFFFFF',
    paddingLeft: 10,
    height: 35,
    paddingTop: 7,
    marginTop: 5,
    flexDirection: 'row',
    justifyContent: 'space-between',
    paddingRight: 10,
  },
  receipt_order_text_view: {
    fontSize: 16,
  },
  receipt_order_status_view: {
    fontSize: 16,
    color: '#fa2e29',
  },
  receipt_detail_view: {
    backgroundColor: '#FFFFFF',
    width: Screen.width - 10,
    marginTop: 1,
  },
})

export default connect()(MyInvoiceScene);
