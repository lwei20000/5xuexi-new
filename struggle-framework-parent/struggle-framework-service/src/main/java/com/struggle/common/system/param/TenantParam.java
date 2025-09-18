package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户查询参数
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "租户查询参数")
public class TenantParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="租户id")
    @QueryField(type = QueryType.EQ)
    private Integer tenantId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "租户全称")
    private String tenantFullName;

    @Schema(description = "租户代码")
    private String tenantCode;

    @Schema(description = "租户类型")
    private String tenantType;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

}
