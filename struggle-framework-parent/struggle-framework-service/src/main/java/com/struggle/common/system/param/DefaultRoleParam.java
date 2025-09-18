package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 默认角色查询参数
 *
 * @author EleAdmin
 * @since 2021-08-29 20:35:09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DefaultRoleParam", description = "默认角色查询参数")
public class DefaultRoleParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="角色id")
    @QueryField(type = QueryType.EQ)
    private Integer roleId;

    @Schema(description="角色标识")
    private String roleCode;

    @Schema(description="租户类型")
    @QueryField(type = QueryType.EQ)
    private String tenantType;

    @Schema(description="角色名称")
    private String roleName;

    @Schema(description="备注")
    private String comments;

}
