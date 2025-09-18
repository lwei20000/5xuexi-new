package com.struggle.common.base.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * CopyMajorParam
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="CopyMajorParam", description = "CopyMajorParam")
public class CopyMajorParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="来源年份")
    @QueryField(type = QueryType.EQ)
    private String majorYear;

    @Schema(description="专业idList")
    private List<Integer> majorIds;

    @Schema(description="目标年份")
    @QueryField(type = QueryType.EQ)
    private String targetMajorYear;
}
