package com.struggle.common.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class ConfProperties {
    /**
     * key
     */
    private String accessKey;

    private String secretKey;
    /**
     * domain 域名前缀
     */
    private String domainPrefix;
    /**
     * domain 视频域名
     */
    private String domain;
    /**
     * bucket
     */
    private String bucket;

    /**
     * domain 存储区域
     */
    private String endpoint;
}
