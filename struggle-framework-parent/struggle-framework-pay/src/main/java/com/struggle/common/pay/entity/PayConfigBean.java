package com.struggle.common.pay.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfigBean {
    /**
     * 支付宝配置
     */
    private AliPay alipay;
}