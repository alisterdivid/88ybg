package com.ybg.base.util;
import java.util.Properties;

/** 系统常量类 @author https://gitee.com/YYDeament/88ybg **/
public class SystemConstant {
	
	/** 是否调试模式 **/
	private static Properties	props	= new Properties();
	static SystemConfig			config	= null;
	
	public static SystemConfig getConfig() {
		if (config == null) {
			config = (SystemConfig) SpringContextUtils.getBean(SystemConfig.class);
		}
		return config;
	}
	
	/** 系统域名 www.???.com **/
	public static String getSystemdomain() {
		return getConfig().getDomain();
	}
	
	/** 系统名称 **/
	public static String getSystemName() {
		return getConfig().getName();
	}
	
	/** 系统配置邮箱 **/
	public static String getEmailAdress() {
		return getConfig().getEmail();
	}
	
	/** 系统配置邮箱密码 **/
	public static String getEmailPwd() {
		return getConfig().getEmailpwd();
	}
	
	/** 系统归属人 **/
	public static String getSystemAuth() {
		return getConfig().getAuth();
	}
	
	/** 系统备案号 **/
	public static String getICP() {
		return getConfig().getIcp();
	}
	
	private SystemConstant() {
		// 禁止实例化
	}
}
