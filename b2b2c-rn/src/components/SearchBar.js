/**
 * Created by Andste on 2017/7/21.
 * 搜索栏
 * 首页、分类共用
 */
import React, {PureComponent} from 'react';
import {Text, TouchableOpacity, StyleSheet, View} from 'react-native';
import {connect} from 'react-redux';
import Icon from 'react-native-vector-icons/MaterialIcons';
import {Screen} from '../utils';
import {colors} from '../../config';

class SearchBar extends PureComponent {
  render() {
    let keywords = this.props.keywords;
    const {is_light, onPress, keyword} = this.props;
    if (keywords == null) {
      keywords = keyword;
    }
    console.log(keywords + '====' + keyword);
    let bg_color = is_light ? '#FFFFFF' : '#D8D8D8';
    let theme_color = is_light ? colors.navigator_tint_color : '#FFFFFF';
    return (
      <TouchableOpacity
        style={[styles.container, {backgroundColor: bg_color}]}
        onPress={onPress}>
        <Icon
          name="search"
          size={20}
          style={[styles.searh_icon, {color: theme_color}]}
        />
        <Text style={[styles.search_title, {color: theme_color}]}>
          {keywords || '客官您要搜点什么？'}
        </Text>
      </TouchableOpacity>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    width: Screen.width - 110,
    height: 30,
    backgroundColor: '#FFFFFF',
    borderRadius: 15,
    flexDirection: 'row',
    alignItems: 'center',
  },
  searh_icon: {
    marginLeft: 5,
    backgroundColor: colors.transparent,
  },
  search_title: {
    fontSize: 12,
  },
});

export default connect(state => ({keyword: state.search.keyword}))(SearchBar);
