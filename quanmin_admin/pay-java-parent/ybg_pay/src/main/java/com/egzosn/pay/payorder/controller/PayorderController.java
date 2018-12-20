package com.egzosn.pay.payorder.controller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayMessage;
import com.egzosn.pay.common.bean.PayOutMessage;
import com.egzosn.pay.common.util.MatrixToImageWriter;
import com.egzosn.pay.common.util.str.StringUtils;
import com.egzosn.pay.payorder.DefaultPaySetting;
import com.egzosn.pay.payorder.domain.PayorderVO;
import com.egzosn.pay.payorder.service.PayorderService;
import com.egzosn.pay.product.domain.HuiyuanProductUtil;
import com.egzosn.pay.seller.service.PayResponse;
import com.egzosn.pay.seller.service.SellerService;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.ybg.base.jdbc.BaseMap;
import com.ybg.base.util.ServletUtil;
import com.ybg.rbac.user.domain.UserVO;

/** @author https://gitee.com/YYDeament/88ybg
 * @date
 * @version v1.0 */
@Controller
@RequestMapping("/pay/huiyuan/")
public class PayorderController {
	
	@Resource
	private SellerService	sellerservice;
	@Autowired
	PayorderService			payorderService;
	
	@RequestMapping("index.do")
	public String index() {
		return "/pay/huiyuan/huiyuan";
	}
	
	/** 微信方式开通会员 **/
	@ResponseBody
	@RequestMapping(value = "toWxQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
	public byte[] toWxQrPay(Integer wxPayId, BigDecimal price, HttpServletRequest request) throws IOException {
		// 获取对应的支付账户操作工具（可根据账户id）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 这里为需要生成二维码的地址
		StringBuffer url = request.getRequestURL();
		url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
		url.append("/pay/huiyuan/toWxPay.html?");
		if (null != wxPayId) {
			url.append("wxPayId=").append(wxPayId).append("&");
		}
		url.append("price=").append(price);
		ImageIO.write(MatrixToImageWriter.writeInfoToJpgBuff(url.toString()), "JPEG", baos);
		return baos.toByteArray();
	}
	
	/** 支付宝方式开通会员 **/
	@ResponseBody
	@RequestMapping(value = "toAliQrPay.jpg", produces = "image/jpeg;charset=UTF-8")
	public byte[] toAliQrPay(@AuthenticationPrincipal UserVO user,HttpServletRequest request) throws IOException {
		String aliPayId = DefaultPaySetting.ALIPAYID;
		BigDecimal price = HuiyuanProductUtil.getHuiyuanBean().getPrice();
		// 获取对应的支付账户操作工具（可根据账户id）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 这里为需要生成二维码的地址
		StringBuffer url = request.getRequestURL();
		url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
		url.append("/pay/huiyuan/toAliPay.do?");
		if (null != aliPayId) {
			url.append("aliPayId=").append(aliPayId).append("&");
		}
		url.append("price=").append(price.floatValue());
		url.append("&userid=").append(user.getId());
		ImageIO.write(MatrixToImageWriter.writeInfoToJpgBuff(url.toString()), "JPEG", baos);
		return baos.toByteArray();
	}
	
	/** 微信平台的判断 并进行支付的转跳
	 * 
	 * @param wxPayId
	 *            微信账户id
	 * @param aliPayId
	 *            支付宝id
	 * @param price
	 *            金额
	 * @return 支付宝与微信平台的判断 */
	@RequestMapping(value = "toWxPay.html", produces = "text/html;charset=UTF-8")
	public String toWxPay(Integer wxPayId, BigDecimal price, HttpServletRequest request) throws IOException {
		StringBuilder html = new StringBuilder();
		// 这里为WAP支付的地址，根据需求自行修改
		StringBuffer url = request.getRequestURL();
		url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
		url.append("/toPay.html");
		html.append("<html><head></head><body><script type=\"text/javascript\"> ");
		// html.append("\nalert('111');\n");
		if (null != wxPayId) {
			html.append("if(isWxPay()){\n");
			html.append("window.location='");
			// 这里使用H5支付，公众号支付是否可以？请开发者自行尝试
			html.append(url.toString()).append("?payId=").append(wxPayId).append("&transactionType=").append(WxTransactionType.MWEB.getType()).append("&price=").append(price);
			html.append("';\n }else\n");
		}
		html.append("{\n alert('请使用微信App扫码'+window.navigator.userAgent.toLowerCase());\n }");
		// 判断是否为微信
		html.append("function isWxPay(){ \n" + " var ua = window.navigator.userAgent.toLowerCase();\n" + " if(ua.match(/MicroMessenger/i) == 'micromessenger'){\n" + " return true;\n" + " }\n" + " return false;\n" + "} \n");
		return html.toString();
	}
	
	/** 支付宝与微信平台的判断 并进行支付的转跳
	 * 
	 * @param wxPayId
	 *            微信账户id
	 * @param aliPayId
	 *            支付宝id
	 * @param price
	 *            金额
	 * @return 支付宝与微信平台的判断 */
	@ResponseBody
	@RequestMapping(value = "toAliPay.do", produces = "text/html;charset=UTF-8")
	public String toAliPay(Integer aliPayId, BigDecimal price,String userid, HttpServletRequest request) throws IOException {
		StringBuilder html = new StringBuilder();
		// 这里为WAP支付的地址，根据需求自行修改
		StringBuffer url = request.getRequestURL();
		url = new StringBuffer(url.substring(0, url.lastIndexOf(request.getRequestURI())));
		url.append("/pay/huiyuan/toPay.do");
		html.append("<html><head></head><body><script type=\"text/javascript\"> ");
		// html.append("\nalert('111');\n");
		if (null != aliPayId) {
			html.append("if(isAliPay()){\n");
			html.append("window.location='");
			html.append(url).append("?payId=").append(aliPayId).append("&transactionType=").append(AliTransactionType.WAP.getType()).append("&price=").append(price);
			html.append("&userid=").append(userid);
			html.append("';\n } else");
		}
		html.append("{\n alert('请使用支付宝App扫码'+window.navigator.userAgent.toLowerCase());\n }");
		// 判断是否为微信
		html.append("function isWxPay(){ \n" + " var ua = window.navigator.userAgent.toLowerCase();\n" + " if(ua.match(/MicroMessenger/i) == 'micromessenger'){\n" + " return true;\n" + " }\n" + " return false;\n" + "} \n");
		// 判断是否为支付宝
		html.append("function isAliPay(){\n" + " var ua = window.navigator.userAgent.toLowerCase();\n" + " if(ua.match(/AlipayClient/i) =='alipayclient'){\n" + "  return true;\n" + " }\n" + "  return false;\n" + "}</script> <body></html>");
		return html.toString();
	}
	
	/** 跳到支付页面 针对实时支付,即时付款
	 *
	 * @param payId
	 *            账户id
	 * @param transactionType
	 *            交易类型， 这个针对于每一个 支付类型的对应的几种交易方式
	 * @param bankType
	 *            针对刷卡支付，卡的类型，类型值
	 * @param price
	 *            金额
	 * @return 跳到支付页面
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "toPay.do", produces = "text/html;charset=UTF-8")
	public String toPay(String userid, HttpServletRequest request, String payId, String transactionType, String bankType, BigDecimal price) throws Exception {
		// 获取对应的支付账户操作工具（可根据账户id）
		PayResponse payResponse = sellerservice.getPayResponse(payId);
		// PayOrder order = new PayOrder();// new PayOrder("订单title", "摘要", null == price ? new BigDecimal(0.01) : price, UUID.randomUUID().toString().replace("-", ""), PayType.valueOf(payResponse.getStorage().getPayType()).getTransactionType(transactionType));
		PayorderVO payorder = HuiyuanProductUtil.gethuiyuanOrder(payId, userid, payResponse, transactionType);
		// ------ 微信H5使用----
		payorder.setSpbillCreateIp(request.getHeader("X-Real-IP"));
		StringBuffer requestURL = request.getRequestURL();
		// 设置网页地址
		payorder.setWapUrl(requestURL.substring(0, requestURL.indexOf("/") > 0 ? requestURL.indexOf("/") : requestURL.length()));
		// 设置网页名称
		payorder.setWapName("在线充值");
		// ------ 微信H5使用----
		// 此处只有刷卡支付(银行卡支付)时需要
		if (StringUtils.isNotEmpty(bankType)) {
			payorder.setBankType(bankType);
		}
		payorderService.save(payorder);
		Map<String, Object> orderInfo = payResponse.getService().orderInfo(payorder);
		return payResponse.getService().buildRequest(orderInfo, MethodType.POST);
	}
	
	/** 同步通知
	 * 
	 * @throws IOException
	 */
	@RequestMapping("alipaysuccess.do")
	public String aliPaySuccess(HttpServletRequest request) throws IOException {
		ServletUtil.getStartPage(request);
		String payId = DefaultPaySetting.ALIPAYID;
		// 根据账户id，获取对应的支付账户操作工具
		// 获取支付方返回的对应参数
		PayResponse payResponse = sellerservice.getPayResponse(payId);
		Map<String, Object> params = payResponse.getService().getParameter2Map(request.getParameterMap(), request.getInputStream());
		if (null == params) {
			return payResponse.getService().getPayOutMessage("fail", "失败").toMessage();
		}
		if (payResponse.getService().verify(params)) {
			// XXX 修改订单状态为可能支付
			BaseMap<String, Object> wheremap = new BaseMap<>();
			BaseMap<String, Object> updatemap = new BaseMap<>();
			updatemap.put("userpay", 1);
			updatemap.put("trade_no", params.get("trade_no").toString());
			wheremap.put("outTradeno", params.get("out_trade_no").toString());
			payorderService.update(updatemap, wheremap);
			//
		}
		return "/pay/alipaysuccess";
	}
	
	/** 支付回调地址（异步）需要内网穿透支持 支付宝例子：http://192.168.1.101/pay/huiyuan/alipayBack.json?total_amount=10.00<br>
	 * &timestamp=2017-11-21+23%3A15%3A39&<br>
	 * sign=xxx&trade_no=2017112121001004810200312390<br>
	 * 支付宝定义的订单ID &sign_type=RSA2<br>
	 * &auth_app_id=2016080500169957&charset=UTF-8&seller_id=2088102170002870<br>
	 * &method=alipay.trade.wap.pay.return<br>
	 * &app_id=2016080500169957<br>
	 * &out_trade_no=c5258442d59a4b58ba49a331d0590b82<br>
	 * 自己定义的订单ID &version=1.0
	 * 
	 * @param request
	 * @return 支付是否成功 */
	@ResponseBody
	@RequestMapping(value = "alipayBack.json")
	public String payBack(HttpServletRequest request) throws IOException {
		// ServletUtil.sayParm(request);
		String payId = DefaultPaySetting.ALIPAYID;
		// 根据账户id，获取对应的支付账户操作工具
		PayResponse payResponse = sellerservice.getPayResponse(payId);
		PayConfigStorage storage = payResponse.getStorage();
		// 获取支付方返回的对应参数
		Map<String, Object> params = payResponse.getService().getParameter2Map(request.getParameterMap(), request.getInputStream());
		if (null == params) {
			return payResponse.getService().getPayOutMessage("fail", "失败").toMessage();
		}
		// 校验
		if (payResponse.getService().verify(params)) {
			PayMessage message = new PayMessage(params, storage.getPayType(), storage.getMsgType().name());
			PayOutMessage outMessage = payResponse.getRouter().route(message);
			BaseMap<String, Object> wheremap = new BaseMap<>();
			BaseMap<String, Object> updatemap = new BaseMap<>();
			updatemap.put("userpay", 1);
			updatemap.put("alinoticepay", 1);
			wheremap.put("outTradeno", message.getOutTradeNo());
			// XXX 添加处理订单的的逻辑
			payorderService.update(updatemap, wheremap);
			return outMessage.toMessage();
		}
		return payResponse.getService().getPayOutMessage("fail", "失败").toMessage();
	}
}
