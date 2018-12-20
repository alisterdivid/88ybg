package com.ybg.social.qq.service;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.ybg.social.qq.dao.QqSocialSettingDao;

/** @author https://gitee.com/YYDeament/88ybg
 * @date 2017/10/1 **/
@Repository
public class QqSocialSettingServiceImpl implements QqSocialSettingService {
	
	@Autowired
	QqSocialSettingDao qQuserDao;
	
	@Override
	public void updateSetting(String appid, String value, String url) {
		qQuserDao.updateSetting(appid, value, url);
	}
	
	@Override
	public Map<String, String> getSetting() {
		return qQuserDao.getSetting();
	}
}
