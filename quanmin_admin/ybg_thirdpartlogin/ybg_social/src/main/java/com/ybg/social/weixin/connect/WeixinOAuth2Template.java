/**
 * 
 */
package com.ybg.social.weixin.connect;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/** 完成微信的OAuth2认证流程的模板类。国内厂商实现的OAuth2每个都不同, spring默认提供的OAuth2Template适应不了，只能针对每个厂商自己微调。
 * 
 * @author zhailiang */
public class WeixinOAuth2Template extends OAuth2Template {
	
	private String				clientId;
	private String				clientSecret;
	private String				accessTokenUrl;
	private static final String	REFRESH_TOKEN_URL	= "https://api.weixin.qq.com/sns/oauth2/refresh_token";
	private static final String	ERRORCODE			= "errcode";
	private static final String	ERRORMSG			= "errmsg";
	
	public WeixinOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
		setUseParametersForClientAuthentication(true);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessTokenUrl = accessTokenUrl;
	}
	
	@Override
	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> parameters) {
		StringBuilder accessTokenRequestUrl = new StringBuilder(accessTokenUrl);
		accessTokenRequestUrl.append("?appid=" + clientId);
		accessTokenRequestUrl.append("&secret=" + clientSecret);
		accessTokenRequestUrl.append("&code=" + authorizationCode);
		accessTokenRequestUrl.append("&grant_type=authorization_code");
		accessTokenRequestUrl.append("&redirect_uri=" + redirectUri);
		return getAccessToken(accessTokenRequestUrl);
	}
	
	@Override
	public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
		StringBuilder refreshTokenUrl = new StringBuilder(REFRESH_TOKEN_URL);
		refreshTokenUrl.append("?appid=" + clientId);
		refreshTokenUrl.append("&grant_type=refresh_token");
		refreshTokenUrl.append("&refresh_token=" + refreshToken);
		return getAccessToken(refreshTokenUrl);
	}
	
	@SuppressWarnings("unchecked")
	private AccessGrant getAccessToken(StringBuilder accessTokenRequestUrl) {
		String response = getRestTemplate().getForObject(accessTokenRequestUrl.toString(), String.class);
		Map<String, Object> result = null;
		try {
			result = new ObjectMapper().readValue(response, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回错误码时直接返回空
		if (StringUtils.isNotBlank(MapUtils.getString(result, ERRORCODE))) {
			String errcode = MapUtils.getString(result, ERRORCODE);
			String errmsg = MapUtils.getString(result, ERRORMSG);
			throw new RuntimeException("获取access token失败, errcode:" + errcode + ", errmsg:" + errmsg);
		}
		WeixinAccessGrant accessToken = new WeixinAccessGrant(MapUtils.getString(result, "access_token"), MapUtils.getString(result, "scope"), MapUtils.getString(result, "refresh_token"), MapUtils.getLong(result, "expires_in"));
		accessToken.setOpenId(MapUtils.getString(result, "openid"));
		return accessToken;
	}
	
	/** 构建获取授权码的请求。也就是引导用户跳转到微信的地址。 */
	@Override
	public String buildAuthenticateUrl(OAuth2Parameters parameters) {
		String url = super.buildAuthenticateUrl(parameters);
		url = url + "&appid=" + clientId + "&scope=snsapi_login";
		return url;
	}
	
	@Override
	public String buildAuthorizeUrl(OAuth2Parameters parameters) {
		return buildAuthenticateUrl(parameters);
	}
	
	@Override
	/** 微信返回的contentType是html/text，添加相应的HttpMessageConverter来处理。 */
	protected RestTemplate createRestTemplate() {
		RestTemplate restTemplate = super.createRestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
	}
}
