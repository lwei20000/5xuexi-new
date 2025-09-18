package com.struggle.common.security.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Cas查询参数
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CasParam", description = "Cas查询参数")
public class CasParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="id")
    @QueryField(type = QueryType.EQ)
    private Integer id;

    @Schema(description = "服务名称")
    private String name;

    @Schema(description = "服务类型")
    private String casType;

    @Schema(description = "服务键值")
    private String appKey;

    @Schema(description = "服务密钥")
    private String appSecret;

    @Schema(description="服务标识")
    private String serviceId;
}
