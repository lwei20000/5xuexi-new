package com.struggle.common.report.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: OrgRemarkParam
 * @Description: 站点付费备注-Param对象
 * @author xsk
 */
@Schema(name = "OrgRemarkParam",description= "站点付费备注Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgRemarkParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="主键ID")
    @QueryField(type = QueryType.EQ)
    private Integer  id;

    @Schema(description="站点ID")
    @QueryField(type = QueryType.EQ)
    private Integer  orgId;

    @Schema(description="年份")
    @QueryField(type = QueryType.EQ)
    private String  year;

    @Schema(description="是否付费（0否，1是）")
    @QueryField(type = QueryType.EQ)
    private Integer  payFlag;

}