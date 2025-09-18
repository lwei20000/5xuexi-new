package com.struggle.common.exam.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: TUserMajorCourseExamItemParam
 * @Description: 用户专业考试记录详情-Param对象
 * @author xsk
 */
@Schema(name = "TUserMajorCourseExamItemParam",description= "用户专业考试记录详情Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TUserMajorCourseExamItemParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="用户专业课程考试详情Id")
    @QueryField(type = QueryType.EQ)
    private Integer  id;

    @Schema(description="用户ID")
    @QueryField(type = QueryType.EQ)
    private Integer  userId;

    @Schema(description="专业课程考试ID")
    @QueryField(type = QueryType.EQ)
    private Integer  majorCourseExamId;

    @Schema(description="试卷题目ID")
    @QueryField(type = QueryType.EQ)
    private Integer  paperQuestionId;

    @Schema(description="作答")
    private String  answer;

}