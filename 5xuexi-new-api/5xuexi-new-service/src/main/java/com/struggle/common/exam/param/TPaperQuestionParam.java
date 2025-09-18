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
 * @ClassName: TPaperQuestionParam
 * @Description: 课程试卷题目-Param对象
 * @author xsk
 */
@Schema(name = "TPaperQuestionParam",description= "课程试卷题目Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TPaperQuestionParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="试卷题目ID")
    @QueryField(type = QueryType.EQ)
    private Integer  paperQuestionId;

    @Schema(description="试卷ID")
    @QueryField(type = QueryType.EQ)
    private Integer  paperId;

    @Schema(description="题目类型 1单选，2多选，3判断, 4填空, 5主观题")
    @QueryField(type = QueryType.EQ)
    private Integer  questionType;

    @Schema(description="题目标题")
    private String  questionTitle;

    @Schema(description="题目选项")
    private String  questionOptions;

    @Schema(description="题目答案")
    private String  questionAnswer;

    @Schema(description="题目解析")
    private String  questionAnalysis;

    @Schema(description="题目分数")
    private Integer  questionScore;

    @Schema(description="题目排序")
    @QueryField(type = QueryType.EQ)
    private Integer  questionSort;

}