package com.struggle.common.base.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学习记录查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserLearningParam", description = "学习记录查询参数")
public class UserLearningParam extends BaseParam {

    private static final long serialVersionUID = 1L;

    @Schema(description="用户id")
    @QueryField(type = QueryType.EQ)
    private Integer userId;

    @Schema(description="专业id")
    @QueryField(type = QueryType.EQ)
    private Integer majorId;

    @Schema(description="课程id")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;

    @Schema(description="课件id")
    @QueryField(type = QueryType.EQ)
    private Integer coursewareId;
}
