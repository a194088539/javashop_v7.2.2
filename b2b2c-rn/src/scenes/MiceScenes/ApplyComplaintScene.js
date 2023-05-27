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
  Container,
  Content,
  Form,
  Item,
  Input,
  Header,
  Label,
  View,
  Text,
} from 'native-base';
import * as API_Order from '../../apis/order';
import {Foundation, request, Screen} from '../../utils';
import ImageViewer from 'react-native-image-zoom-viewer';
import {Modal} from '../../components';
import Icon from 'react-native-vector-icons/Ionicons';
import {colors} from '../../../config';
import {messageActions} from '../../redux/actions';
import ImagePicker from 'react-native-image-crop-picker';
import * as API_Common from '../../apis/common';

class ApplyComplaintScene extends Component {
  static navigationOptions = ({navigation}) => {
    return {
      title: '申请投诉',
    };
  };

  constructor(props) {
    super(props);
    this.state = {
      loading: false,
      applyForm: {
        complain_topic: '',
        order_sn: this.props.navigation.state.params.order_sn,
        sku_id: this.props.navigation.state.params.sku_id,
        content: '',
        images: [],
        base64Img: [],
        formDatas: [],
      },
    };
  }

  _onSelectImage = () => {
    const {applyForm} = this.state;
    const {dispatch} = this.props;
    let {images, formDatas, base64Img} = applyForm;
    if (base64Img.length >= 3) {
      dispatch(messageActions.error('图片最多上传3张'));
      return;
    }
    ImagePicker.openPicker({
      multiple: true,
      forceJpg: true,
      includeBase64: true,
      compressImageQuality: 0.2,
      maxFiles: 3 - base64Img.length,
      loadingLabelText: '正在加载...',
    }).then(photos => {
      formDatas = [];
      //直接展示base64图片
      photos.map(async (photo, index) => {
        let {path, filename, size, mime, data} = photo;
        //base64直接展示
        base64Img.push(data);
        const formData = new FormData();
        formData.append('file', {uri: path, type: mime, name: filename});
        formDatas.push(formData);
      });
      this.setState({applyForm});
      //上传图片
      this._uploadPhoto(formDatas, images, applyForm);
    });
  };

  //上传图片
  _uploadPhoto = (formDatas, images, applyForm) => {
    formDatas.map(async (formData, index) => {
      try {
        const res = await request({
          url: API_Common.upload,
          data: formData,
          method: 'POST',
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        this.setState({loading: false});
        images.push(res.url);
        this.setState({applyForm});
      } catch (err) {
        this.setState({loading: false});
      }
    });
  };

  _delImage = imgIndex => {
    let {applyForm} = this.state;
    applyForm.base64Img.splice(imgIndex, 1);
    applyForm.images.splice(imgIndex, 1);
    this.setState({applyForm});
  };

  _onSubmit = async () => {
    let {applyForm} = this.state;
    const {dispatch} = this.props;
    //判断是否正在上传图片
    if (
      applyForm.base64Img.length != 0 &&
      applyForm.images.length != applyForm.base64Img.length
    ) {
      this.props.dispatch(messageActions.error('正在上传图片，请稍等'));
      return;
    }
    applyForm.base64Img = [];
    applyForm.images = applyForm.images.toString()
    //console.log(applyForm.images = applyForm.images.toString());
    console.log(applyForm);
    API_Order.applyComplains(applyForm);
    this.props.dispatch(messageActions.success('提交成功'));
    this.props.navigation.goBack();
  };

  render() {
    const {loading, applyForm} = this.state;
    return (
      <Container>
        <Content>
          <Form>
            <Item>
              <Label>投诉主题:</Label>
              <Input
                placeholder="请输入投诉主题"
                value={applyForm.complain_topic}
                onChangeText={text =>
                  this.setState({
                    applyForm: {...applyForm, complain_topic: text},
                  })
                }
              />
            </Item>
            <Item>
              <Label>
                <Label style={{color: 'red'}}>*</Label>投诉内容:
              </Label>
              <Input
                placeholder="请输入投诉内容"
                value={applyForm.content}
                onChangeText={text =>
                  this.setState({
                    applyForm: {...applyForm, content: text},
                  })
                }
              />
            </Item>
            <Item>
              <Label>投诉凭证:</Label>
              {applyForm.base64Img &&
                applyForm.base64Img.map((uri, _index) => (
                  <View style={styles.image_view}>
                    <Image
                      style={styles.comment_image}
                      source={{uri: `data:image/jpeg;base64,${uri}`}}
                    />
                    <TouchableOpacity
                      style={styles.del_btn}
                      activeOpacity={1}
                      onPress={() => this._delImage(_index)}>
                      <Icon name="md-trash" size={20} color="red" />
                    </TouchableOpacity>
                  </View>
                ))}
              <View style={styles.images_view}>
                <TouchableOpacity
                  style={styles.image_view}
                  activeOpacity={1}
                  onPress={() => this._onSelectImage()}>
                  <Image
                    style={styles.camera_image}
                    source={require('../../images/icon-camera.png')}
                  />
                </TouchableOpacity>
              </View>
            </Item>
          </Form>
          <Button
            style={{marginVertical: 8, margin: 10}}
            block
            danger
            onPress={this._onSubmit}>
            <Text>申请投诉</Text>
          </Button>
        </Content>
      </Container>
    );
  }
}

const IMAGE_WIDTH = (Screen.width - 20 - 50) / 4;
const styles = StyleSheet.create({
  images_view: {
    flexDirection: 'row',
    justifyContent: 'flex-start',
    alignItems: 'center',
    flexWrap: 'wrap',
    width: Screen.width,
    backgroundColor: '#FFFFFF',
    paddingHorizontal: 10,
    paddingTop: 10,
  },
  camera_image: {
    width: 35,
    height: 35,
  },
  image_view: {
    justifyContent: 'center',
    alignItems: 'center',
    width: IMAGE_WIDTH + 10,
    height: IMAGE_WIDTH + 40,
    marginBottom: 10,
    borderColor: colors.cell_line_backgroud,
    borderWidth: Screen.onePixel,
    borderRadius: 3,
  },
  comment_image: {
    width: IMAGE_WIDTH,
    height: IMAGE_WIDTH + 10,
  },
});

export default connect()(ApplyComplaintScene);
