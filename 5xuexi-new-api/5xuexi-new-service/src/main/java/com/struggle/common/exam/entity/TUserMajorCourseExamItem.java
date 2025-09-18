package com.struggle.common.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: TUserMajorCourseExamItem
 * @Description: 用户专业考试记录详情-实体对象
 * @author xsk
 */
@Schema(name = "TUserMajorCourseExamItem",description= "用户专业考试记录详情")
@Data
@TableName("t_user_major_course_exam_item")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TUserMajorCourseExamItem implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="用户专业课程考试详情Id")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description="专业课程考试ID")
    @NotNull(message = "专业课程考试不能为空")
    private Integer  majorCourseExamId;

    @Schema(description="用户ID")
    @NotNull(message = "用户不能为空")
    private Integer  userId;

    @Schema(description="试卷题目ID")
    @NotNull(message = "试卷题目不能为空")
    private Integer  paperQuestionId;

    @Schema(description="作答")
    private String  answer;

    @Schema(description="得分")
    @Range(min = 0, max = 150, message = "得分取值范围为0-150")
    private Integer  score;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

}