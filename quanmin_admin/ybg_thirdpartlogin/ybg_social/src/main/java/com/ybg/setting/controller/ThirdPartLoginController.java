package com.ybg.setting.controller;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ybg.api.service.WeixinApiService;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.jdbc.util.QvoConditionUtil;
import com.ybg.base.util.Json;
import com.ybg.rbac.user.domain.UserVO;
import com.ybg.setting.domain.SocialUserVO;
import com.ybg.setting.qvo.SocialUserQuery;
import com.ybg.setting.service.SocialUserService;
import com.ybg.social.ali.service.AliSocialSettingService;
import com.ybg.social.baidu.service.BaiduSocialSettingService;
import com.ybg.social.github.service.GithubSocialSettingService;
import com.ybg.social.qq.service.QqSocialSettingService;
import com.ybg.social.sina.service.WeiboUserService;
import io.swagger.annotations.Api;

/*** @author https://gitee.com/YYDeament/88ybg
 * 
 * @date 2016/10/1 */
@Api(tags = "第三方登陆设置项")
@Controller
@RequestMapping("/thirdoartlogin_do/")
public class ThirdPartLoginController {
	
	@Autowired
	WeiboUserService			weiboUserService;
	@Autowired
	BaiduSocialSettingService	baiduUserService;
	@Autowired
	WeixinApiService			weixinApiService;
	@Autowired
	QqSocialSettingService		qQuserService;
	@Autowired
	SocialUserService			socialUserService;
	@Autowired
	GithubSocialSettingService	githubuserService;
	@Autowired
	AliSocialSettingService		aliSocialSettingService;
	
	@RequestMapping(value = "index.do", method = { RequestMethod.GET, RequestMethod.POST })
	public String index() {
		return "/thirdpartlogin/setting";
	}
	
	@ResponseBody
	@RequestMapping(value = "info.do", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> info() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("sina", weiboUserService.getSetting());
		map.put("weixin", weixinApiService.getSetting());
		map.put("baidu", baiduUserService.getSetting());
		map.put("qq", qQuserService.getSetting());
		map.put("github", githubuserService.getSetting());
		map.put("ali", aliSocialSettingService.getSetting());
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "update.do", method = { RequestMethod.GET, RequestMethod.POST })
	public Json update(String qqid, String qqSERCRET, String baiduid, String baiduSERCRET, String sinaid, String sinaSERCRET, String weixinid, String weixinSERCRET, String githubid, String githubSERCRET, String aliid, String aliSERCRET, String alipublickey) {
		Json j = new Json();
		j.setMsg("操作成功");
		// 1.4版本 删除码云登陆， 回调地址 不需要再填写
		weiboUserService.updateSetting(sinaid, sinaSERCRET, "");
		weixinApiService.updateSetting(weixinid, weixinSERCRET);
		baiduUserService.updateSetting(baiduid, baiduSERCRET, "");
		qQuserService.updateSetting(qqid, qqSERCRET, "");
		aliSocialSettingService.updateSetting(aliid, aliSERCRET, alipublickey);
		j.setSuccess(true);
		return j;
	}
	
	/** 用户绑定信息
	 * 
	 * @throws Exception
	 **/
	@ResponseBody
	@RequestMapping("boundinfo.do")
	public Map<String, Object> boundinfo(@AuthenticationPrincipal UserVO user) throws Exception {
		Map<String, Object> map = new LinkedHashMap<>();
		if (user == null) {
			return null;
		}
		String userid = user.getId();
		SocialUserQuery qvo = new SocialUserQuery();
		qvo.setUserid(userid);
		List<SocialUserVO> list = socialUserService.query(qvo);
		map.put("list", list);
		map.put("baidu", checkproviderid("baidu", list));
		map.put("qq", checkproviderid("qq", list));
		map.put("sina", checkproviderid("sina", list));
		map.put("weixin", checkproviderid("weixin", list));
		map.put("github", checkproviderid("github", list));
		return map;
	}
	
	private boolean checkproviderid(String providerid, List<SocialUserVO> list) {
		boolean flag = false;
		for (SocialUserVO bean : list) {
			if (bean.getProviderid().toLowerCase().equals(providerid.toLowerCase())) {
				return true;
			}
		}
		return flag;
	}
	
	@ResponseBody
	@RequestMapping("delsocialbind.do")
	public Json delbaidu(@AuthenticationPrincipal UserVO user, String providerid) throws Exception {
		if (user == null || !QvoConditionUtil.checkString(providerid)) {
			return null;
		}
		SocialUserQuery qvo = new SocialUserQuery();
		qvo.setUserid(user.getId());
		qvo.setProviderid(providerid);
		List<SocialUserVO> list = socialUserService.query(qvo);
		if (!QvoConditionUtil.checkList(list)) {
			Json j = new Json();
			j.setSuccess(true);
			j.setMsg("操作失败,数据异常。");
			return j;
		}
		SocialUserVO bean = list.get(0);
		if (user.getUsername().equals(bean.getProviderid() + bean.getProvideruserid())) {
			Json j = new Json();
			j.setSuccess(true);
			j.setMsg("操作失败,直接绑定不可删除。");
			return j;
		}
		if (QvoConditionUtil.checkString(providerid)) {
			BaseMap<String, Object> conditionmap = new BaseMap<>();
			conditionmap.put("userid", user.getId());
			conditionmap.put("providerid", providerid);
			socialUserService.remove(conditionmap);
		}
		Json j = new Json();
		j.setSuccess(true);
		j.setMsg("操作成功");
		return j;
	}
}
