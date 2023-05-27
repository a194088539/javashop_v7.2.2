import React, {Component} from 'react';
import {Alert, View, StatusBar, FlatList, StyleSheet, Text} from 'react-native';
import {connect} from 'react-redux';
import {messageActions} from '../../redux/actions';
import * as API_Member from '../../apis/members';
import MyHistoryItem from './MyHistoryItem';
import {BigButton, GoodsItemPro} from '../../widgets';
import {Foundation, Screen} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';
import {colors} from '../../../config';

class MyInvoiceDetail extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '订单发票详细',
    };
  };

  constructor(props) {
    super(props);
    this.receipt = this.props.navigation.state.params.receipt;
  }

  render() {
    const receipt = this.receipt;
    console.log(JSON.stringify(receipt))
    return (
      <View style={styles.container}>
        <View>
          <View style={styles.receipt_order_view}>
            <Text style={styles.receipt_order_text_view}>
              订单号：{receipt.order_sn}
            </Text>
            <Text style={styles.receipt_order_status_view}>
              {receipt.status == 0 ? '未开票' : '已开票'}
            </Text>
          </View>
          <View style={styles.receipt_detail_view}>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>发票类型:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.receipt_type == 'VATORDINARY'
                  ? '增值税普通发票'
                  : '增值税专用发票'}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>发票内容:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.receipt_content}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>发票抬头:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.receipt_title}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>发票税号:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.tax_no}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>收票人:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.member_name}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>联系方式:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.member_mobile}
              </Text>
            </View>
            <View style={styles.receipt_content_view}>
              <Text style={styles.receipt_detail_text_view}>收票地址:</Text>
              <Text style={styles.receipt_detail_value_view}>
                {receipt.province}
                {receipt.city}
                {receipt.county}
                {receipt.detail_addr}
              </Text>
            </View>
          </View>
        </View>
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
    marginTop: 2,
    paddingLeft: 10,
    paddingTop: 10,
  },
  receipt_detail_text_view: {
    fontSize: 15,
    height: 30,
    width: 80,
    color: '#4a4a4a',
  },
  receipt_detail_value_view: {
    fontSize: 15,
    height: 30,
  },
  receipt_content_view: {
    flexDirection: 'row',
  },
})

export default connect()(MyInvoiceDetail);
