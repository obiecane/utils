package com.ahzak.utils.exception;

/**
 * 远程调用API类错误码枚举(如微信、支付宝、短信) 号段范围 12500~13000 调用支付宝、微信、银联等第三方机构api出现的业务错误
 *
 * @author: wangqq
 * @date: 2018年6月25日 上午11:33:47
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public enum RPCErrorCodeEnum {
    /**
     * 消息校验Token输入是否符合规则 必须是长度为16位的字符串，只能是字母和数字
     **/
    TOKEN_INPUT_ERROR("12500", "messageValidateToken is input error!"),
    /**
     * 消息加解密Key输入是否符合规则 必须是长度为43位的字符串，只能是字母和数字
     **/
    KEY_INPUT_ERROR("12501", "messageDecryptKey is input error!"),
    /**
     * 今日群发次数已用尽
     **/
    MASS_SUBSCRIPTION_ERROR("12502", "The number of mass deliveries today has been exhausted"),
    /**
     * 本月群发次数已用尽
     **/
    MASS_SERVICE_ERROR("12503", "The number of mass deliveries has been exhausted this month"),
    /**
     * 签名错误
     */
    SIGN_ERROR("12504", "Signature error"),
    /**
     * 统一下单失败
     */
    UNIFIEDORDER_ERROR("12505", "Failed to place an order"),
    /**
     * 余额不足
     */
    NOT_SUFFICIENT_FUNDS("12506", "Sorry, your credit is running low!"),
    /**
     * 支付密码未设置
     */
    PAY_PASSWORD_NOT_SET("12507", "Payment password is not set!"),
    /**
     * 支付密码错误
     */
    PAY_PASSWORD_ERROR("12508", "Payment password is error!"),
    /**
     * 交易失败
     */
    TRANSACTION_FAIL("12509", "Transaction is failure!"),
    /**
     * 第三方调用失败
     */
    THIRD_PARTY_CALL_ERROR("12510", "third party call error!"),
    /**
     * 未配置第三方信息
     */
    THIRD_PARTY_INFO_UNCONFIGURATION("12511", "thirdParty info is unconfiguration"),
    /**
     * 邮箱地址无效
     */
    EMAIL_ADDRESS_INVALID("12512", "Invalid e-mail address!"),
    /**
     * 消息队列消息处理失败
     */
    QUEUE_MESSAGE_HANDLER_FAILED("12513", "Message queue message processing failed!"),
    /**
     * 电话号码无效
     */
    PHONE_NUMBER_INVALID("12514", "Invalid phone address!"),
    /**
     * 未获取到用户信息
     */
    USER_INFO_NOT_OBTAINED("12515", "No user info was obtained!"),
    /**
     * 未获取到用户凭证
     */
    USER_CREDENTIALS_NOT_OBTAINED("12516", "No user credentials were obtained!"),
    /**
     * 业务已经处理完毕
     */
    BUSINESS_ALREADY_EXIST_PROCESSED("12517", "business already exist processed!"),
    /**
     * 小程序已经绑定了小程序
     */
    WECHAT_IS_ALREADY_BOUND_TO_THE_APPLET("12518", "WeChat is already bound to the applet!"),
    /**
     * 系统错误
     */
    SYSTEM_ERROR("12519", "system error!"),
    /**
     * 未配置支付插件
     */
    UNCONFIG_PAYMENT_PLUGIN("12520", "No Payment Plug-in Configured!"),
    /**
     * 保证金不能小于零
     **/
    BANLANCE_NOTLESS_ZERO("12521", "The margin must not be less than zero!"),
    /**
     * 时间设置错误
     **/
    TIME_SET_ERROR("12522", "Time setting error"),
    /**
     * 第三方返回参数不合法
     */
    ILLEGAL_THIRD_PARTY_RETURN_PARAMETERS("12523", "Illegal third party return parameters!"),
    /**
     * 未正确配置支付插件，请联系后台客服处理
     */
    THE_PAYMENT_PLUGIN_ALLOCATION_ERROR("12524", "The payment plugin is not configured correctly, please contact the background customer service!"),


    /**
     * 默认
     */
    DEFAULT("12000", "");
    /**
     * 异常代码。
     */
    private String code;

    /**
     * 异常对应的默认提示信息。
     */
    private String defaultMessage;

    /**
     * 异常对应的原始提示信息。
     */
    private String originalMessage;

    /**
     * 当前请求的URL。
     */
    private String requestUrl;

    /**
     * 需转向（重定向）的URL，默认为空。
     */
    private String defaultRedirectUrl = "";

    /**
     * 异常对应的响应数据。
     */
    private Object data = new Object();

    /**
     * Description: 根据异常的代码、默认提示信息构建一个异常信息对象。
     *
     * @param code           异常的代码。
     * @param defaultMessage 异常的默认提示信息。
     */
    RPCErrorCodeEnum(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getDefaultRedirectUrl() {
        return defaultRedirectUrl;
    }

    public void setDefaultRedirectUrl(String defaultRedirectUrl) {
        this.defaultRedirectUrl = defaultRedirectUrl;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
