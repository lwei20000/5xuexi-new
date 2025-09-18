package com.struggle.common.pay.controller.alipay;

import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.ijpay.alipay.AliPayApi;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.alipay.AliPayApiConfigKit;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.pay.entity.AliPay;
import com.struggle.common.pay.entity.PayConfigBean;
import com.struggle.common.pay.entity.PayOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>支付宝支付 Demo</p>
 *
 * @author Javen
 */
@Service
public class AliPayService {
	private static final Logger log = LoggerFactory.getLogger(AliPayService.class);

	@Resource
	private PayConfigBean payConfigBean;

	@Resource
	private ConfigProperties configProperties;

	// 普通公钥模式
	private final static String NOTIFY_URL = "/api/aliPay/notify_url";
    private final static String RETURN_URL = "/api/aliPay/return_url";

	public AliPayApiConfig initApiConfig(){
		AliPayApiConfig aliPayApiConfig;
		AliPay alipay = payConfigBean.getAlipay();
		try {
			aliPayApiConfig = AliPayApiConfigKit.getApiConfig(alipay.getAppId());
		} catch (Exception e) {
			aliPayApiConfig = AliPayApiConfig.builder()
				.setAppId(alipay.getAppId())
				.setAliPayPublicKey(alipay.getPublicKey())
				.setCharset("UTF-8")
				.setPrivateKey(alipay.getPrivateKey())
				.setServiceUrl(alipay.getServerUrl())
				.setSignType("RSA2")
				// 普通公钥方式
				.build();
			AliPayApiConfigKit.setThreadLocalAliPayApiConfig(aliPayApiConfig);
		}
		return aliPayApiConfig;
	}
//
//	/**
//	 * app支付
//	 */
//	public ApiResult<?> appPay() {
//		String orderInfo = "";
//		try {
//			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
//			model.setBody("我是测试数据-By Javen");
//			model.setSubject("App支付测试-By Javen");
//			model.setOutTradeNo("dddddddddddddddddddddddddddddd");
//			model.setTimeoutExpress("30m");
//			model.setTotalAmount("0.01");
//			model.setPassbackParams("callback params");
//			model.setProductCode("QUICK_MSECURITY_PAY");
//			orderInfo = AliPayApi.appPayToResponse(model, aliPayBean.getDomain() + NOTIFY_URL).getBody();
//		} catch (AlipayApiException e) {
//			e.printStackTrace();
//		}
//		return  new ApiResult(0, "操作成功", orderInfo);
//	}

	/**
	 * H5支付
	 */
	public void wapPay(HttpServletResponse response, PayOrder payOrder) {
		this.initApiConfig();
		String body = payOrder.getBody();
		String subject = payOrder.getTitle();
		String totalAmount = payOrder.getPrice()+"";
		String returnUrl = configProperties.getBackendDomain() + RETURN_URL;
		String notifyUrl = configProperties.getBackendDomain() + NOTIFY_URL;

		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setPassbackParams(payOrder.getPassbackParams());
		model.setOutTradeNo(payOrder.getOrderCode());
		model.setProductCode("QUICK_WAP_PAY");
		model.setQuitUrl(payOrder.getQuitUrl());
		try {
			AliPayApi.wapPay(response, model, returnUrl, notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * PC支付
	 */
	public void pcPay(HttpServletResponse response,PayOrder payOrder) {
		this.initApiConfig();
		try {
			String totalAmount = payOrder.getPrice()+"";
			String returnUrl = configProperties.getBackendDomain() + RETURN_URL;
			String notifyUrl = configProperties.getBackendDomain() + NOTIFY_URL;
			AlipayTradePagePayModel model = new AlipayTradePagePayModel();

			model.setOutTradeNo(payOrder.getOrderCode());
			model.setProductCode("FAST_INSTANT_TRADE_PAY");
			model.setTotalAmount(totalAmount);
			model.setSubject(payOrder.getTitle());
			model.setBody(payOrder.getBody());
			model.setPassbackParams(payOrder.getPassbackParams());
			model.setRequestFromUrl(payOrder.getQuitUrl());
			/**
			 * 花呗分期相关的设置,测试环境不支持花呗分期的测试
			 * hb_fq_num代表花呗分期数，仅支持传入3、6、12，其他期数暂不支持，传入会报错；
			 * hb_fq_seller_percent代表卖家承担收费比例，商家承担手续费传入100，用户承担手续费传入0，仅支持传入100、0两种，其他比例暂不支持，传入会报错。
			 */
//            ExtendParams extendParams = new ExtendParams();
//            extendParams.setHbFqNum("3");
//            extendParams.setHbFqSellerPercent("0");
//            model.setExtendParams(extendParams);

			AliPayApi.tradePage(response, model, notifyUrl, returnUrl);
			// https://opensupport.alipay.com/support/helpcenter/192/201602488772?ant_source=antsupport
			// Alipay Easy SDK（新版）目前只支持输出form表单，不支持打印出url链接。
			// AliPayApi.tradePage(response, "GET", model, notifyUrl, returnUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	/**
//	 * 扫码支付
//	 */
//	public String tradePreCreatePay() {
//		String subject = "Javen 支付宝扫码支付测试";
//		String totalAmount = "86";
//		String storeId = "123";
//        String notifyUrl = aliPayBean.getDomain() + NOTIFY_URL;
////		String notifyUrl = aliPayBean.getDomain() + "/aliPay/cert_notify_url";
//
//		AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
//		model.setSubject(subject);
//		model.setTotalAmount(totalAmount);
//		model.setStoreId(storeId);
//		model.setTimeoutExpress("5m");
//		model.setOutTradeNo("dddddddddddddddddddddddddddddddddddddddddddddd");
//		try {
//			String resultStr = AliPayApi.tradePrecreatePayToResponse(model, notifyUrl).getBody();
//			JSONObject jsonObject = JSONObject.parseObject(resultStr);
//			return jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
