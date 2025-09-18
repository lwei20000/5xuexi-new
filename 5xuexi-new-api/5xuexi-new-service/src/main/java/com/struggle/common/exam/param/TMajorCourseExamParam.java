package com.struggle.common.exam.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: TMajorCourseExamParam
 * @Description: 专业课程考试-Param对象
 * @author xsk
 */
@Schema(name = "TMajorCourseExamParam",description= "专业课程考试Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TMajorCourseExamParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="专业课程考试ID")
    @QueryField(type = QueryType.EQ)
    private Integer  majorCourseExamId;

    @Schema(description="专业课程ID")
    @QueryField(type = QueryType.EQ)
    private Integer  majorCourseId;

    @Schema(description="试卷id")
    @QueryField(type = QueryType.EQ)
    private Integer  paperId;

    @Schema(description="考试开始时间")
    private Date startTime;

    @Schema(description="考试结束时间")
    private Date  endTime;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;
}