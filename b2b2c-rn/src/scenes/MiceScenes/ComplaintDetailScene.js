import React, {Component} from 'react';
import {
  StyleSheet,
  StatusBar,
  FlatList,
  Image,
  TouchableOpacity,
} from 'react-native';
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
import * as API_Order from '../../apis/order';
import {Foundation, Screen} from '../../utils';
import ImageViewer from 'react-native-image-zoom-viewer';
import {Modal} from '../../components';
import StepIndicator from 'react-native-step-indicator';

const labels = ['数据错误'];
const customStyles = {
  stepIndicatorSize: 25,
  currentStepIndicatorSize: 30,
  separatorStrokeWidth: 2,
  currentStepStrokeWidth: 3,
  stepStrokeCurrentColor: '#1add37',
  stepStrokeWidth: 3,
  stepStrokeFinishedColor: '#1add37',
  stepStrokeUnFinishedColor: '#aaaaaa',
  separatorFinishedColor: '#1add37',
  separatorUnFinishedColor: '#aaaaaa',
  stepIndicatorFinishedColor: '#1add37',
  stepIndicatorUnFinishedColor: '#ffffff',
  stepIndicatorCurrentColor: '#ffffff',
  stepIndicatorLabelFontSize: 13,
  currentStepIndicatorLabelFontSize: 13,
  stepIndicatorLabelCurrentColor: '#1add37',
  stepIndicatorLabelFinishedColor: '#ffffff',
  stepIndicatorLabelUnFinishedColor: '#aaaaaa',
  labelColor: '#999999',
  labelSize: 13,
  currentStepLabelColor: '#1add37',
};

class ComplaintDetailScene extends Component {
  static navigationOptions = ({navigation}) => {
    return {
      title: '投诉详情',
    };
  };

  constructor(props) {
    super(props);
    this.item = this.props.navigation.state.params.item;
    const images = this.item.images.toString().split(',');
    this.item.images = images;
    this.params = {
      complain_id: this.item.complain_id,
    };
    const imageUrls = images.map(item => ({
      url: item,
    }));
    this.state = {
      data: this.item,
      showModal: false,
      imageIndex: 0,
      imageUrls: imageUrls,
      //流程文字
      labels: labels,
      //流程第几步
      currentPosition: 0,
    };
  }

  componentDidMount(): void {
    this._getOrderComplainsStatus();
  }

  /**
   * 读取投诉状态流程
   * @returns {Promise<void>}
   * @private
   */
  _getOrderComplainsStatus = async () => {
    const res = await API_Order.getOrderComplainsStatus(this.params);
    if (res) {
      //流程文字
      const labels = [];
      //流程第几步
      let currentPosition = 0;
      res.forEach((item, index) => {
        labels.push(item.text);
        if (item.show_status === 1) {
          currentPosition = index;
        }
      });
      this.setState({
        labels,
        currentPosition,
      });
    }
  };

  //展示投诉图片Modal
  _onShowModal = index => {
    this.setState({showModal: true, imageIndex: index});
  };

  //关闭投诉图片Modal
  _onCloseModal = () => {
    this.setState({showModal: false, imageIndex: 0});
  };

  render() {
    const {data, showModal, imageIndex, imageUrls, labels} = this.state;
    console.log(labels)
    return (
      <Container style={{backgroundColor: 'rgb(247, 247, 247)'}}>
        <Content>
          <View
            style={{flex: 1, backgroundColor: '#FFFFFF', paddingVertical: 10}}>
            <StepIndicator
              customStyles={customStyles}
              currentPosition={this.state.currentPosition}
              labels={labels}
            />
          </View>
          <Card transparent>
            <CardItem
              header
              bordered
              style={{
                flexDirection: 'row',
                justifyContent: 'space-between',
              }}>
              <View>
                <Text>投诉信息</Text>
              </View>
            </CardItem>
            <CardItem
              header
              bordered
              style={{
                flexDirection: 'row',
                justifyContent: 'space-between',
              }}>
              <View>
                <Text style={styles.text}>投诉商品：{data.goods_name}</Text>
                <Text style={styles.text}>投诉主题：{data.complain_topic}</Text>
                <Text style={styles.text}>
                  投诉时间：{Foundation.unixToDate(data.create_time)}
                </Text>
                <Text style={styles.text}>投诉内容：{data.content}</Text>
                <View style={styles.text}>
                  <Text style={styles.text}>投诉凭证</Text>
                  <View style={styles.item_image_view}>
                    {data.images.map((img, _index) => {
                      return (
                        <TouchableOpacity
                          key={_index}
                          activeOpacity={1}
                          onPress={() => this._onShowModal(_index)}>
                          <Image
                            style={styles.item_image}
                            source={{uri: img}}
                          />
                        </TouchableOpacity>
                      );
                    })}
                  </View>
                </View>
              </View>
            </CardItem>
          </Card>
        </Content>
        <Modal
          title="图片凭证"
          isOpen={showModal}
          onClosed={this._onCloseModal}
          transparent={false}>
          <ImageViewer
            style={styles.issue_img}
            imageUrls={imageUrls}
            index={imageIndex}
            enablePreload={true}
            saveToLocalByLongPress={false}
            enableSwipeDown={true}
            onSwipeDown={this._onCloseModal}
            onCancel={this._onCloseModal}
            enableImageZoom={true}
          />
        </Modal>
      </Container>
    );
  }
}

const IMAGE_WIDTH = (Screen.width - 20 - 40) / 4;
const styles = StyleSheet.create({
  text: {
    marginVertical: 5,
    color: '#6c7070',
  },
  item_image_view: {
    flexDirection: 'row',
    width: Screen.width,
    justifyContent: 'flex-start',
    flexWrap: 'wrap',
  },
  item_image: {
    width: IMAGE_WIDTH,
    height: IMAGE_WIDTH,
    margin: 3,
  },
});

export default connect()(ComplaintDetailScene);
