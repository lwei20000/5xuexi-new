package com.struggle.common.oss.qiniu.config;

import lombok.Data;

@Data
public class QiniuToken {
    /**
     * token
     */
    private String token;
    /**
     * domain 域名前缀
     */
    private String domainPrefix;
    /**
     * domain 域名
     */
    private String domain;
}