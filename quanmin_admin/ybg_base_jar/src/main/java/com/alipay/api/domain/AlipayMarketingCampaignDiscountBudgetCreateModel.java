package com.alipay.api.domain;

import com.alipay.api.AlipayObject;
import com.alipay.api.internal.mapping.ApiField;

/**
 * 营销立减活动预算创建
 *
 * @author auto create
 * @since 1.0, 2017-11-17 11:19:16
 */
public class AlipayMarketingCampaignDiscountBudgetCreateModel extends AlipayObject {

	private static final long serialVersionUID = 5619247734758955682L;

	/**
	 * 业务名称，和out_biz_no一起进行幂等控制
	 */
	@ApiField("biz_from")
	private String bizFrom;

	/**
	 * 预算库使用结束时间，格式：yyyy-MM-dd mm:hh:ss
	 */
	@ApiField("gmt_end")
	private String gmtEnd;

	/**
	 * 预算名称
	 */
	@ApiField("name")
	private String name;

	/**
	 * 预算创建业务号，和biz_from一起进行幂等控制
	 */
	@ApiField("out_biz_no")
	private String outBizNo;

	/**
	 * 积分预算库ID
	 */
	@ApiField("out_budget_no")
	private String outBudgetNo;

	/**
	 * 发行人支付宝登录账号
	 */
	@ApiField("publisher_logon_id")
	private String publisherLogonId;

	/**
	 * 预算金额，单位：元
	 */
	@ApiField("total_amount")
	private String totalAmount;

	public String getBizFrom() {
		return this.bizFrom;
	}
	public void setBizFrom(String bizFrom) {
		this.bizFrom = bizFrom;
	}

	public String getGmtEnd() {
		return this.gmtEnd;
	}
	public void setGmtEnd(String gmtEnd) {
		this.gmtEnd = gmtEnd;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getOutBizNo() {
		return this.outBizNo;
	}
	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}

	public String getOutBudgetNo() {
		return this.outBudgetNo;
	}
	public void setOutBudgetNo(String outBudgetNo) {
		this.outBudgetNo = outBudgetNo;
	}

	public String getPublisherLogonId() {
		return this.publisherLogonId;
	}
	public void setPublisherLogonId(String publisherLogonId) {
		this.publisherLogonId = publisherLogonId;
	}

	public String getTotalAmount() {
		return this.totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

}
