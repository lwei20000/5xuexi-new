package com.struggle.common.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: TPaperQuestion
 * @Description: 课程试卷题目-实体对象
 * @author xsk
 */
@Schema(name = "TPaperQuestion",description= "课程试卷题目")
@Data
@TableName("t_paper_question")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TPaperQuestion implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="试卷题目ID")
    @TableId(type = IdType.AUTO)
    private Integer  paperQuestionId;

    @Schema(description="试卷ID")
    @NotNull(message = "试卷ID不能为空")
    private Integer  paperId;

    @Schema(description="题目分组")
    @NotBlank(message = "题目分组不能为空")
    private String  questionGroup;

    @Schema(description="题目类型 1单选，2多选，3判断, 4填空, 5主观题")
    @Range(min = 1, max = 5, message = "题目类型 1单选，2多选，3判断, 4填空, 5主观题")
    @NotNull(message = "题目类型不能为空")
    private Integer  questionType;

    @Schema(description="题目标题")
    private String  questionTitle;

    @Schema(description="题目选项")
    private String  questionOptions;

    @Schema(description="题目答案")
    @NotBlank(message = "题目答案不能为空")
    private String  questionAnswer;

    @Schema(description="题目解析")
    private String  questionAnalysis;

    @Schema(description="题目分数")
    @Range(min = 1, max = 150, message = "题目分数取值范围为0-150")
    @NotNull(message = "题目分数不能为空")
    private Integer  questionScore;

    @Schema(description="题目排序")
    @Range(min = 0, max = 1000, message = "题目排序取值范围为0-1000")
    private Integer  questionSort;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

}