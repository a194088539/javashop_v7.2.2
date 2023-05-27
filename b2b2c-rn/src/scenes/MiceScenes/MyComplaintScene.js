import React, {Component} from 'react';
import {StyleSheet, StatusBar, FlatList, TouchableOpacity} from 'react-native';
import {connect} from 'react-redux';
import {
  Button,
  Tabs,
  Tab,
  Container,
  Content,
  Body,
  ListItem,
  Left,
  Right,
  View,
  Text,
  Card,
  CardItem,
  Icon,
  List,
} from 'native-base';
import {colors} from '../../../config';
import {GoodsItemPro} from '../../widgets';
import * as API_Order from '../../apis/order';
import {DataEmpty} from '../../components/EmptyViews';

class MyComplaintScene extends Component {
  static navigationOptions = ({navigation}) => {
    return {
      title: '交易投诉',
    };
  };

  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      dataList: [],
    };
    this.params = {
      page_no: 1,
      page_size: 10,
      tag: '',
    };
  }

  _onShop() {
    console.log('店铺');
  }

  componentDidMount(): void {
    this._getOrderComplains();
  }

  _getOrderComplains = async () => {
    const {page_no} = this.params;
    const res = await API_Order.getOrderComplains(this.params);
    this.setState({
      dataList: res.data,
    });
  }

  _onChangeTab(value) {
    if (value.i === 0) {
      this.params.tag = 'ALL';
    } else if (value.i === 1) {
      this.params.tag = 'COMPLAINING';
    } else if (value.i === 2) {
      this.params.tag = 'COMPLETE';
    } else if (value.i === 3) {
      this.params.tag = 'CANCELED';
    }
    this._getOrderComplains();
  }

  _onDetail = async item => {
    this.props.navigation.navigate('ComplaintDetail',{item});
    //navigator('ComplaintDetail');
  }

  _renderItem = ({item}) => {
    return <OrderView item={item} onDetail={this._onDetail} />;
  };

  render() {
    const {dataList} = this.state;
    return (
      <Container style={{backgroundColor: 'rgb(247, 247, 247)'}}>
        <StatusBar barStyle="dark-content" />
        <Tabs
          tabBarUnderlineStyle={{backgroundColor: colors.main}}
          onChangeTab={value => this._onChangeTab(value)}>
          <Tab
            heading="全部投诉"
            activeTextStyle={{color: colors.main}}
            tabStyle={{backgroundColor: '#FFFFFF'}}
            activeTabStyle={{backgroundColor: '#FFFFFF'}}>
            <FlatList
              style={{backgroundColor: 'rgb(247, 247, 247)'}}
              data={dataList}
              ListEmptyComponent={<DataEmpty />}
              renderItem={item => this._renderItem(item)}
            />
          </Tab>
          <Tab
            heading="进行中"
            activeTextStyle={{color: colors.main}}
            tabStyle={{backgroundColor: '#FFFFFF'}}
            activeTabStyle={{backgroundColor: '#FFFFFF'}}>
            <FlatList
              style={{backgroundColor: 'rgb(247, 247, 247)'}}
              data={dataList}
              ListEmptyComponent={<DataEmpty />}
              renderItem={item => <OrderView data={item} />}
            />
          </Tab>
          <Tab
            heading="已完成"
            activeTextStyle={{color: colors.main}}
            tabStyle={{backgroundColor: '#FFFFFF'}}
            activeTabStyle={{backgroundColor: '#FFFFFF'}}>
            <FlatList
              style={{backgroundColor: 'rgb(247, 247, 247)'}}
              data={dataList}
              ListEmptyComponent={<DataEmpty />}
              renderItem={item => <OrderView data={item} />}
            />
          </Tab>
          <Tab
            heading="已撤销"
            activeTextStyle={{color: colors.main}}
            tabStyle={{backgroundColor: '#FFFFFF'}}
            activeTabStyle={{backgroundColor: '#FFFFFF'}}>
            <FlatList
              style={{backgroundColor: 'rgb(247, 247, 247)'}}
              data={dataList}
              ListEmptyComponent={<DataEmpty />}
              renderItem={item => <OrderView data={item} />}
            />
          </Tab>
        </Tabs>
      </Container>
    );
  }
}

const OrderView = ({item, onDetail}) => {
  return (
    <Content style={{paddingTop: 5, backgroundColor: 'rgb(247, 247, 247)'}}>
      <Card transparent>
        <CardItem
          header
          bordered
          style={{
            flexDirection: 'row',
            justifyContent: 'space-between',
          }}>
          <View>
            <Text>投诉编号：{item.complain_id}</Text>
            <Text style={{fontSize: 13, color: '#6c7070'}}>
              订单号：{item.order_sn}
            </Text>
          </View>
          <Text style={{color: 'red'}}>
            {item.status === 'NEW' && '新投诉'}
            {item.status === 'CANCEL' && '已撤销'}
            {item.status === 'WAIT_APPEAL' && '待申诉'}
            {item.status === 'COMMUNICATION' && '对话中'}
            {item.status === 'WAIT_ARBITRATION' && '等待仲裁'}
            {item.status === 'COMPLETE' && '已完成'}
          </Text>
        </CardItem>
        <CardItem bordered>
          <Text>主题：{item.complain_topic}</Text>
        </CardItem>
        <CardItem style={{justifyContent: 'space-between'}}>
          <Text style={{color: '#214fff'}}>{item.seller_name}</Text>
          <Right>
            <Icon name="arrow-forward" />
          </Right>
        </CardItem>
        <CardItem bordered style={{marginLeft: -10}}>
          <Body>
            <GoodsItemPro
              style={{width: 200}}
              goods_img={item.goods_image}
              goods_name={item.goods_name}
            />
          </Body>
        </CardItem>
        <CardItem footer bordered style={{justifyContent: 'flex-end'}}>
          <TouchableOpacity onPress={() => onDetail(item)}>
            <Text>查看详情</Text>
          </TouchableOpacity>
        </CardItem>
      </Card>
    </Content>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  btn: {
    width: 80,
    marginLeft: 10,
  },
});

export default connect()(MyComplaintScene);
