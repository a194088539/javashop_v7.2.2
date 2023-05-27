package com.enation.app.javashop.service.distribution;

import com.enation.app.javashop.model.distribution.dos.ShortUrlDO;

/**
 * 短链接Manager接口
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018/5/23 上午8:37
 * @Description:
 *
 */
public interface ShortUrlManager {

	/**
	 * 生成一个短链接
	 * @param memberId
	 * @param goodsId
	 * @return
	 */
    ShortUrlDO createShortUrl(Long memberId, Long goodsId);

	/**
	 * 根据短链接获得长链接
	 * @param shortUrl 短链接 （可带前缀 即：http:xxx/）
	 * @return 所对应的长链接
	 */
    ShortUrlDO getLongUrl(String shortUrl);

}
