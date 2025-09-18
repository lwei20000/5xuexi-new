package com.struggle.common.base.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * CopyCoursewareParam
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="CopyCoursewareParam", description = "CopyCoursewareParam")
public class CopyCoursewareParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="来源课程")
    @QueryField(type = QueryType.EQ)
    private Integer pcourseId;

    @Schema(description="目标课程")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;
}
