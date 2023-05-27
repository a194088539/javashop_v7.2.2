/**
 * Created by Andste on 2018/10/25.
 */
import React, {Component} from 'react';
import {View, Platform, Text, Image, Linking, StyleSheet} from 'react-native';
import {isIphoneX} from 'react-native-iphone-x-helper';
import VersionNumber from 'react-native-version-number';
import {WebView} from 'react-native-webview';
import {appId} from '../../../config';
import {Screen} from '../../utils';
import * as API_Article from '../../apis/article';
import {Cell, CellGroup} from '../../widgets';
import {Modal} from '../../components';

const isIos = Platform.OS === 'ios';

export default class AboutAppScene extends Component {
  static navigationOptions = {
    title: '关于',
  };

  constructor(props) {
    super(props);
    this.state = {
      showModal: false,
      article: {article_name: '', content: ''},
    };
  }

  _onModalClose = () => {
    this.setState({showModal: false});
  };

  _onModalOpen = () => {
    this.setState({showModal: true});
  };

  _getArticle = async articleId => {
    const res = await API_Article.getArticleDetail(articleId);
    const article = res;
    this.setState({article});
  };

  _toStore = () => {
    Linking.canOpenURL(
      `https://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?id=${
        appId.appStoreId
      }&pageNumber=0&sortOrdering=2&type=Purple+Software&mt=8`,
    );
  };

  render() {
    const {showModal, article} = this.state;
    let _p_str = isIos ? 'iPhone' : 'Android';
    return (
      <View style={styles.container}>
        <View style={styles.app_logo_view}>
          <Image
            style={styles.app_logo_image}
            source={require('../../images/icon-app-logo.png')}
          />
          <Text style={styles.copyright_text}>{`For ${_p_str} v${
            VersionNumber.appVersion
          }`}</Text>
        </View>
        <CellGroup>
          {isIos ? (
            <Cell title="评分支持" onPress={this._toStore} label="打个分吧" />
          ) : (
            undefined
          )}
          <Cell
            title="服务条款"
            onPress={() => {
              this._getArticle(110);
              this._onModalOpen();
            }}
          />
          <Cell
            title="免责声明"
            onPress={() => {
              this._getArticle(111);
              this._onModalOpen();
            }}
          />
        </CellGroup>
        <View style={styles.copyright_view}>
          <Text style={styles.copyright_text}>
            Copyright© 2007-2018 javamall,Inc. All rights reserved
          </Text>
          <Text style={styles.copyright_text}>
            易族智汇（北京）科技有限公司
          </Text>
        </View>
        <Modal
          header={<Text>{article.article_name}</Text>}
          isOpen={showModal}
          onRequestClose={this._onModalClose}
          onClosed={this._onModalClose}>
          <WebView originWhitelist={['*']} source={{html: article.content}} />
        </Modal>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  app_logo_view: {
    alignItems: 'center',
    width: Screen.width,
    height: 220,
    paddingVertical: 30,
  },
  app_logo_image: {
    width: 80,
    height: 80,
    borderRadius: 15,
    marginBottom: 10,
  },
  copyright_view: {
    position: 'absolute',
    bottom: isIphoneX() ? 40 : 20,
    left: 0,
    width: Screen.width,
    alignItems: 'center',
  },
  copyright_text: {
    fontSize: 12,
    color: '#777777',
    paddingBottom: 5,
  },
});
