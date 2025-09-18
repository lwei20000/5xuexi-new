package com.struggle.common.cas.pojo;

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
@ConfigurationProperties(prefix = "integrate.uaap.cas")
public class IntegrateUaapCasProperties {

    /**
     * 认证服务端地址前缀,包含协议,域名,端口,上下文
     */
    private String serverUrlPrefix;

    /**
     * 集成认证的客户端url(处理cas返回的url), 只包含协议,域名, 端口, 非微服务情况下不需要上下文, 微服务环境需要包括网关的前缀
     */
    private String clientHostUrl;

    /**
     * 集成认证的前端url, 如果前端在调用接口时没有传入这个值, 将会采用配置文件中的配置进行跳转回前端的url的拼接
     */
    private String frontUrl;

}
