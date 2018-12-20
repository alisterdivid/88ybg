package com.ybg.social.ali.dao;
import java.util.Map;

/** @author Deament
 * @date 2017/10/1 **/
public interface AliSocialSettingDao {
	
	/** 返回阿里社交登陆设置
	 * 
	 * @return Map<String, String> **/
	Map<String, String> getSetting();
	
	/** 更新阿里社交登陆设置
	 * @param appid 应用ID
	 * @param value 应用密码
	 * @param alipublickey 支付宝公钥
	 *  **/
	void updateSetting(String appid, String value, String alipublickey);
}
