package com.struggle.common.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 系统配置属性
 *
 * @author EleAdmin
 * @since 2021-08-30 17:58:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class ConfigProperties {

    /**
     * 文件上传是否使用uuid命名
     */
    private Boolean uploadUuidName = true;

    /**
     * 验证码过期时间, 单位秒
     */
    private Long codeExpireTime = 5 * 60L;

    /**
     * token过期时间, 单位秒
     */
    private Long tokenExpireTime = 60 * 60 * 6L;

    /**
     * token快要过期自动刷新时间, 单位分钟
     */
    private int tokenRefreshTime = 30;

    /**
     * 生成token的密钥Key的base64字符
     */
    private String tokenKey;

    /**
     * redis 字典缓存前缀
     */
    private String dictionaryPrefix="DICTIONARY_DATA";

    /**
     * redis 图形验证码缓存前缀
     */
    private String codePrefix="CODE";

    /**
     * redis 用户缓存前缀
     */
    private String userPrefix="USER";

    /**
     * redis 权限菜单缓存前缀
     */
    private String menuPrefix="USER_MENU";

    /**
     * redis 用户消息缓存
     */
    private String messagePrefix="USER_MESSAGE_MESSAGE";
    /**
     * redis 用户通知缓存
     */
    private String announcementPrefix="USER_MESSAGE_ANNOUNCEMENT";
    /**
     * redis 用户公开通知缓存
     */
    private String openAnnouncementPrefix="TENANT_MESSAGE_OPEN_ANNOUNCEMENT";
    /**
     * redis 用户平台消息缓存
     */
    private String systemMessagePrefix="USER_MESSAGE_SYSTEM_MESSAGE";

    /**
     * redis 用户平台通知缓存
     */
    private String systemAnnouncementPrefix="USER_MESSAGE_SYSTEM_ANNOUNCEMENT";
    /**
     * 默认上传方式
     */
    private String defaultUpload;
    /**
     * 前端地址
     */
    private String frontDomain;
    /**
     * 后端地址
     */
    private String backendDomain;
    /**
     * 应用类型
     */
    private String appType;

    /**
     * 默认租户管理员ID
     */
    private Integer defaultUser = 1;

    /**
     * 默认租户ID
     */
    private Integer defaultTenant = 1;

    /**
     * 默认设置管理员角色
     */
    private String defaultRoleCode = "admin";

    /**
     * 默认租户机构类型
     */
    private String defaultOrgType = "默认机构";

    /**
     * 默认租户机构CODE
     */
    private String defaultOrgCode = "000000";
}
