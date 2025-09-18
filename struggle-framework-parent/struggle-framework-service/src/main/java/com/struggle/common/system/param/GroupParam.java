package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分组查询参数
 *
 * @author EleAdmin
 * @since 2021-08-29 20:35:09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "GroupParam", description = "分组查询参数")
public class GroupParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分组id")
    @QueryField(type = QueryType.EQ)
    private Integer groupId;

    @Schema(description = "上级id, 0是顶级")
    @QueryField(type = QueryType.EQ)
    private Integer parentId;

    @Schema(description = "分组代码")
    private String groupCode;

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "备注")
    private String comments;
}
