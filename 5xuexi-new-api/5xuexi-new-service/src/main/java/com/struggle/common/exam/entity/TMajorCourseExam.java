package com.struggle.common.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: TMajorCourseExam
 * @Description: 专业课程考试-实体对象
 * @author xsk
 */
@Schema(name= "TMajorCourseExam", description= "专业课程考试")
@Data
@TableName("t_major_course_exam")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TMajorCourseExam implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="专业课程考试ID")
    @TableId(type = IdType.AUTO)
    private Integer  majorCourseExamId;

    @Schema(description="专业课程ID")
    @NotNull(message = "专业课程ID不能为空")
    private Integer  majorCourseId;

    @Schema(description="试卷id")
    @NotNull(message = "试卷ID不能为空")
    private Integer  paperId;

    @Schema(description="考试开始时间")
    @NotNull(message = "考试开始时间不能为空")
    private  Date  startTime;

    @Schema(description="考试结束时间")
    @NotNull(message = "考试结束时间不能为空")
    private  Date  endTime;

    @Schema(description="是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除取值范围, 0否, 1是")
    @TableLogic
    private Integer  deleted;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

    @Schema(description="试卷")
    @TableField(exist = false)
    private TPaper paper;
}