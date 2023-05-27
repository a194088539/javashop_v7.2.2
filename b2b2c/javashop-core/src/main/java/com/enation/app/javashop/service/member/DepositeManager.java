package com.enation.app.javashop.service.member;

import com.enation.app.javashop.model.member.vo.MemberDepositeVO;
import com.enation.app.javashop.model.member.dos.MemberWalletDO;

/**
 * 会员预存款业务层
 * @author liuyulei
 * @version v1.0
 * @since v7.2.0
 * 2019-12-30 16:24:51
 */
public interface DepositeManager {


	/**
	 * 增加用户预存款余额
	 * @param money  金额
	 * @param memberId  会员id
	 * @param detail  操作描述
	 * @return  返回增加结果    true:增加成功    false:增加失败
	 */

    Boolean increase(Double money, Long memberId, String detail);


	/**
	 * 扣减用户预存款余额
	 * @param money 金额
	 * @param memberId 会员id
	 * @param detail 操作描述
	 * @return
	 */
    Boolean	reduce(Double money, Long memberId, String detail);


	/**
	 * 设置支付密码
	 * @param memberId  会员id
	 * @param password  支付密码  MD5加密后数据
	 */
	void setDepositePwd(Long memberId,String password);


	/**
	 * 校验密码
	 * @param memberId 会员ID
	 * @param password 支付密码
	 */
	void checkPwd(Long memberId,String password);


	/**
	 * 添加会员钱包
	 * @param wallet 会员钱包
	 * @return MemberWallet 会员钱包
	 */
	MemberWalletDO add(MemberWalletDO wallet);

	/**
	* 修改会员钱包
	* @param memberWallet 会员钱包
	* @param id 会员钱包主键
	* @return MemberWallet 会员钱包
	*/
	MemberWalletDO edit(MemberWalletDO memberWallet, Long id);

	/**
	 * 删除会员钱包
	 * @param id 会员钱包主键
	 */
	void delete(Long id);

	/**
	 * 获取会员钱包
	 * @param id 会员钱包主键
	 * @return MemberWallet  会员钱包
	 */
	MemberWalletDO getModel(Long id);

	/**
	 * 获取会员预存款余额
	 * @param memberId  会员id
	 * @return
	 */
	Double getDeposite(Long memberId);


	/**
	 * 获取预存款抵扣参数
	 * @param memberId 会员ID
	 * @return
	 */
	MemberDepositeVO getDepositeVO(Long memberId);

}
