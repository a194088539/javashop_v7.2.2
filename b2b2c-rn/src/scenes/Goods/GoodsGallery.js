/**
 * Created by Andste on 2018/11/21.
 */
import React, {PureComponent} from 'react';
import {
  View,
  Image,
  Modal,
  TouchableOpacity,
  StyleSheet,
  Text,
} from 'react-native';
import {colors} from '../../../config';
import {Screen} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';
import Swiper from 'react-native-swiper';
import ImageViewer from 'react-native-image-zoom-viewer';
import Video from 'react-native-video';

export default class GoodsGallery extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      showModal: false,
      isPaused: true,
      rate: 0,
      videoUrl: this.props.videoUrl,
      imageIndex: 0,
      images: props.data.map(item => ({
        url: item.big,
      })),
    };
    if (this.state.videoUrl != null && this.state.videoUrl.length > 0) {
      let videoMap = {url: this.state.videoUrl};
      this.state.images.unshift(videoMap);
    }
  }

  _onShowModal = index => {
    this.setState({showModal: true, imageIndex: index});
  };

  _onCloseModal = () => {
    this.setState({showModal: false, imageIndex: 0});
  };

  _onPaused = index => {
    if (index == 0) {
      this.setState({rate: 1});
    } else {
      this.setState({rate: 0});
    }
  };

  render() {
    const {showModal, imageIndex, images, videoUrl} = this.state;
    return (
      <View style={styles.container}>
        <Swiper
          height={Screen.width}
          loop={true}
          autoplay={false}
          showsPagination={true}
          paginationStyle={{bottom: 3}}
          onIndexChanged={this._onPaused}
          activeDotColor={colors.main}>
          {images.map((item, index) => (
            <View>
              {videoUrl != null && videoUrl.length > 0 && index == 0 ? (
                <Video
                  source={{
                    uri: item.url,
                  }}
                  ref={ref => {
                    this.player = ref;
                  }}
                  controls={true}
                  paused={true}
                  style={styles.backgroundVideo}
                />
              ) : (
                <TouchableOpacity
                  onPress={() => this._onShowModal(index)}
                  key={index}
                  activeOpacity={1}>
                  <Image source={{uri: item.url}} style={styles.goods_img} />
                </TouchableOpacity>
              )}
            </View>
          ))}
        </Swiper>
        <Modal
          visible={showModal}
          transparent={true}
          animationType="fade"
          onRequestClose={this._onCloseModal}>
          <ImageViewer
            imageUrls={images}
            index={imageIndex}
            enablePreload={true}
            saveToLocalByLongPress={false}
            enableSwipeDown={true}
            onSwipeDown={this._onCloseModal}
            onCancel={this._onCloseModal}
            enableImageZoom={true}
          />
        </Modal>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    height: Screen.width,
    paddingTop: isIphoneX() ? 40 : 25,
  },
  goods_img: {
    width: Screen.width,
    height: Screen.width,
  },
  goods_img_viewer: {
    width: Screen.width,
    height: Screen.height,
  },
  backgroundVideo: {
    position: 'absolute',
    top: 0,
    left: 0,
    bottom: 0,
    right: 0,
    width: Screen.width + 10,
    height: Screen.width - 37,
  },
});
