package com.struggle.common.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    private Map<String,Object> ignoreTableNameMap;

    private Map<String,Object> tableNameMap;
}
