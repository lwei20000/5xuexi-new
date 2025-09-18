package com.struggle.common.oss.tianyiyun.config;

import lombok.Data;

@Data
public class TianyiyunToken {

    /**
     * 临时accessKey
     */
    private String ak;
    /**
     * 临时secretAccessKey
     */
    private String sk;
    /**
     * 临时sessionToken
     */
    private String tk;
    /**
     * domain 域名前缀
     */
    private String domainPrefix;
    /**
     * domain 域名
     */
    private String domain;
    /**
     * bucket
     */
    private String bucket;
}