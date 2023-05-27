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
  Image,
} from 'react-native';
import {connect} from 'react-redux';
import {Foundation, Screen} from '../../utils';
import {isIphoneX} from 'react-native-iphone-x-helper';
import * as API_Member from '../../apis/members';
import {F14Text} from '../../widgets/Text';
import StarRating from 'react-native-star-rating';
import {colors} from '../../../config';
import Icon from 'react-native-vector-icons/Ionicons';

class MyCommentDetailScene extends Component {
  static navigationOptions = ({navigation}) => {
    const {params = {}} = navigation.state;
    return {
      title: params.title || '评价详情',
    };
  };

  constructor(props) {
    super(props);
    console.log(this.props.navigation.state.params)
    this.comment_id = this.props.navigation.state.params.comment_id;
    this.state = {
      commentData: {},
    };
  }

  componentDidMount(): void {
    this._onGetComment();
  }

  /**
   * 读取我的商品评论详情
   * @returns {Promise<null>}
   * @private
   */
  _onGetComment = async () => {
    const res = (await API_Member.commentDetail(this.comment_id)) || {};
    this.setState({
      commentData: res,
    });
  };

  render() {
    const {commentData} = this.state;
    return (
      <View style={styles.container}>
        <View style={styles.the_first_container}>
          <Text style={styles.the_first_text}>
            初评日期：
            {Foundation.unixToDate(commentData.create_time)}
          </Text>
          <Text style={styles.the_first_text}>
            {commentData.grade == 'good'
              ? '好评'
              : commentData.grade == 'neutral'
              ? '中评'
              : '差评'}
          </Text>
          <Text style={styles.the_first_text}>{commentData.content}</Text>
        </View>
        {commentData.additional_comment != null ? (
          <View style={styles.the_first_container}>
            <Text style={styles.the_first_text}>
              追评日期：
              {Foundation.unixToDate(
                commentData.additional_comment.create_time,
              )}
            </Text>
            <Text style={styles.the_first_text}>
              {commentData.additional_comment.content}
            </Text>
          </View>
        ) : (
          <Text />
        )}
        <View style={styles.shop_grade_view}>
          <View style={styles.shop_grade_title}>
            <Image
              style={{width: 25, height: 25, marginRight: 10}}
              source={require('../../images/icon-shop_comment.png')}
            />
            <F14Text>店铺评分</F14Text>
          </View>
          <View style={styles.shop_grade}>
            <F14Text>描述相符</F14Text>
            <View style={{width: 150, marginLeft: 10}}>
              <StarRating
                disabled={false}
                maxStars={5}
                rating={
                  commentData.member_shop_score != null
                    ? commentData.member_shop_score.description_score
                    : 5
                }
                starSize={20}
                fullStarColor={colors.main}
                selectedStar={rating =>
                  this.setState({description_score: rating})
                }
              />
            </View>
          </View>
          <View style={styles.shop_grade}>
            <F14Text>服务态度</F14Text>
            <View style={{width: 150, marginLeft: 10}}>
              <StarRating
                disabled={false}
                maxStars={5}
                rating={
                  commentData.member_shop_score != null
                    ? commentData.member_shop_score.service_score
                    : 5
                }
                starSize={20}
                fullStarColor={colors.main}
                selectedStar={rating => this.setState({service_score: rating})}
              />
            </View>
          </View>
          <View style={styles.shop_grade}>
            <F14Text>物流服务</F14Text>
            <View style={{width: 150, marginLeft: 10}}>
              <StarRating
                disabled={false}
                maxStars={5}
                rating={
                  commentData.member_shop_score != null
                    ? commentData.member_shop_score.description_score
                    : 5
                }
                starSize={20}
                fullStarColor={colors.main}
                selectedStar={rating => this.setState({delivery_score: rating})}
              />
            </View>
          </View>
        </View>
      </View>
    );
  }
}

const IMAGE_WIDTH = (Screen.width - 20 - 50) / 5;
const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  the_first_container: {
    minHeight: 100,
    backgroundColor: '#FFFFFF',
    padding: 20,
    marginTop: 1,
  },
  the_first_text: {
    height: 30,
  },
  shop_grade_view: {
    backgroundColor: '#fff',
    paddingHorizontal: 10,
    marginTop: 1,
  },
  shop_grade_title: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 30,
  },
  shop_grade: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 30,
  },
  item_label: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    paddingHorizontal: 10,
    paddingVertical: 10,
    backgroundColor: '#FFFFFF',
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

export default connect()(MyCommentDetailScene);
