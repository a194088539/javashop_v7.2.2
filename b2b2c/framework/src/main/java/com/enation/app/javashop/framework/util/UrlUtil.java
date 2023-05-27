package com.enation.app.javashop.framework.util;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * url域名等工具类
 * @author kingapex
 * @version v1.0
 * @since v7.0.0
 * 2018年3月23日 上午10:26:41
 */
public class UrlUtil {

	private  static final Pattern PATTERN = Pattern.compile("[^.]+(\\.com\\.cn|\\.net\\.cn|\\.org\\.cn|\\.gov\\.cn|\\.com|\\.net|\\.cn|\\.org|\\.cc|\\.me|\\.tel|\\.mobi|\\.asia|\\.biz|\\.info|\\.name|\\.tv|\\.hk|\\.公司|\\.中国|\\.网络)");

	/**
	 * 根据网址获取顶级域名，不包括二级域名
	 * @param url
	 * @return
	 */
	public static String getTopDomain(String url){
		String host = "";
		try{
			// 此处获取值转换为小写
			host = new URL(url).getHost().toLowerCase();
		}catch(Exception ex){}
		Matcher matcher = PATTERN.matcher(host);
		while (matcher.find()) {
			return matcher.group();
		}
		return null;
	}
	
}
