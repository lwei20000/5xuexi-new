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
 * 组织机构查询参数
 *
 * @author EleAdmin
 * @since 2021-08-29 20:35:09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OrgParam", description = "组织机构查询参数")
public class OrgParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "机构id")
    @QueryField(type = QueryType.EQ)
    private Integer orgId;

    @Schema(description = "上级id, 0是顶级")
    @QueryField(type = QueryType.EQ)
    private Integer parentId;

    @Schema(description = "机构代码")
    private String orgCode;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构全称")
    private String orgFullName;

    @Schema(description = "机构全称Code")
    private String orgFullCode;

    @Schema(description = "机构类型(字典代码)")
    private String orgType;

    @Schema(description = "查询详情部门主管")
    @TableField(exist = false)
    private boolean detail;

    @Schema(description="机构Ids")
    @QueryField(type = QueryType.IN,value="orgId")
    private List<Integer> orgIds;

    @Schema(description = "备注")
    private String comments;
}
