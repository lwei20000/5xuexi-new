package com.struggle.common.oauth2.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 集成认证 oauth 2 的属性
 *
 * @author 张晋荣 <a href="mailto:phenom_work@163.com">点此联系我</a>
 */
@Data
@Component
@ConfigurationProperties(prefix = "integrate.uaap.oauth2")
public class IntegrateUaapOauth2Properties {

    /**
     * 认证分发的客户端标识
     */
    private String clientId;

    /**
     * 认证分发的客户端的密钥
     */
    private String clientSecret;

    /**
     * 获得认证code的url前缀
     */
    private String authorizeUrlPrefix;

    /**
     * 集成认证的前端url, 如果前端在调用接口时没有传入这个值, 将会采用配置文件中的配置进行跳转回前端的url的拼接
     */
    private String frontUrl;

    /**
     * 通过code请求accessToKen的认证的url前缀
     */
    private String accessTokenUrlPrefix;

    /**
     * 通过accessToKen获得用户信息的认证的url前缀
     */
    private String profileUrlPrefix;
}
