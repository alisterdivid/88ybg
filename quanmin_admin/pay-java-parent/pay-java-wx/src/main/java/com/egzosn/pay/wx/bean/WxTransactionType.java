package com.egzosn.pay.wx.bean;

import com.egzosn.pay.common.bean.TransactionType;

/**
 * 微信交易类型
 * @author egan
 *
 * email egzosn@gmail.com
 * date 2016/10/19 22:58
 */
public enum  WxTransactionType implements TransactionType {
    /**
     * 公众号支付
     */
    JSAPI("pay/unifiedorder"),
    /**
     * 扫码付
     */
    NATIVE("pay/unifiedorder"),
    /**
     * 移动支付
     */
    APP("pay/unifiedorder"),
    /**
     * H5支付
     */
    MWEB("pay/unifiedorder"),
    /**
     * 刷卡付
     */
    MICROPAY("pay/micropay"),
    // TODO 2017/3/8 19:14 author: egan  交易辅助接口
    /**
     * 查询订单
     */
    QUERY("pay/orderquery"),
    /**
     * 关闭订单
     */
    CLOSE("pay/closeorder"),
    /**
     * 申请退款
     */
    REFUND("secapi/pay/refund"),
    /**
     * 查询退款
     */
    REFUNDQUERY("pay/refundquery"),
    /**
     * 下载对账单
     */
    DOWNLOADBILL("pay/downloadbill")
    ;

    WxTransactionType(String method) {
        this.method = method;
    }

    private String method;

    @Override
    public String getType() {
        return this.name();
    }
    @Override
    public String getMethod() {
        return this.method;
    }
}
