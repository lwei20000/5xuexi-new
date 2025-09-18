package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 文档查询参数
 *
 * @author EleAdmin
 * @since 2021-08-29 20:35:09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DocParam", description = "文档查询参数")
public class DocParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="文档id")
    @QueryField(type = QueryType.EQ)
    private Integer docId;

    @Schema(description="名称")
    private String name;

    @Schema(description="文档类型")
    @QueryField(type = QueryType.EQ)
    private String docType;

    @Schema(description="默认角色ID")
    @QueryField(type = QueryType.EQ)
    private Integer defaultRoleId;

    @Schema(description="默认角色IDs")
    @TableField(exist = false)
    private List<Integer> defaultRoleIds;
}
