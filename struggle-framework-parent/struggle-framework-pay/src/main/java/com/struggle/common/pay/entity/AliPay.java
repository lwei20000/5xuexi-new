package com.struggle.common.pay.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * <p>IJPay 让支付触手可及，封装了微信支付、支付宝支付、银联支付常用的支付方式以及各种常用的接口。</p>
 *
 * <p>不依赖任何第三方 mvc 框架，仅仅作为工具使用简单快速完成支付模块的开发，可轻松嵌入到任何系统里。 </p>
 *
 * <p>IJPay 交流群: 723992875、864988890</p>
 *
 * <p>Node.js 版: <a href="https://gitee.com/javen205/TNWX">https://gitee.com/javen205/TNWX</a></p>
 *
 * <p>支付宝配置 Bean</p>
 *
 * @author Javen
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AliPay {
	/**
	 * 应用ID
	 */
	private String appId;
	/**
	 * 应用私钥
	 */
	private String privateKey;
	/**
	 * 支付宝公钥
	 */
	private String publicKey;
	/**
	 * 支付地址： 沙箱 https://openapi-sandbox.dl.alipaydev.com/gateway.do
	 *       正式环境 https://openapi.alipay.com/gateway.do
	 */
	private String serverUrl;
}
