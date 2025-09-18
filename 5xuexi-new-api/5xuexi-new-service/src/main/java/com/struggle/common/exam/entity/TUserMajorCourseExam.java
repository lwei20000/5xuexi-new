package com.struggle.common.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.entity.Major;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: TUserMajorCourseExam
 * @Description: 用户专业考试记录-实体对象
 * @author xsk
 */
@Schema(name = "TUserMajorCourseExam",description= "用户专业考试记录")
@Data
@TableName("t_user_major_course_exam")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TUserMajorCourseExam implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="用户专业课程考试Id")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description="用户ID")
    @NotNull(message = "用户不能为空")
    private Integer  userId;

    @Schema(description="专业课程考试ID")
    @NotNull(message = "专业课程不能为空")
    private Integer  majorCourseExamId;

    @Schema(description="试卷id")
    @NotNull(message = "试卷不能为空")
    private Integer  paperId;

    @Schema(description="考试开始时间")
    private  Date  startTime;

    @Schema(description="考试结束时间")
    private  Date  endTime;

    @Schema(description="成绩")
    @Range(min = 0, max = 150, message = "成绩取值范围为0-150")
    private Integer  score;

    @Schema(description="状态：0 ：答题进行中 1：提交未批改 2：提交已批改 3：未考试")
    @Range(min = 0, max = 2, message = "状态：0 ：保存 1：提交未批改 2：提交已批改 3：未考试")
    private Integer  status;

    @Schema(description="是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除, 0否, 1是")
    @TableLogic
    private Integer  deleted;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

    @Schema(description="题目作答")
    @TableField(exist = false)
    private List<TUserMajorCourseExamItem> itemList;

    @Schema(description="站点ID")
    @TableField(exist = false)
    private Integer  orgId;

    @Schema(description="课程ID")
    @TableField(exist = false)
    private Integer  courseId;

    @Schema(description="专业ID")
    @TableField(exist = false)
    private Integer  majorId;

    @Schema(description="学期")
    @TableField(exist = false)
    private String semester;


    @Schema(description="专业")
    @TableField(exist = false)
    private Major major;

    @Schema(description="用户")
    @TableField(exist = false)
    private User user;

    @Schema(description="课程")
    @TableField(exist = false)
    private Course course;

    @Schema(description="机构")
    @TableField(exist = false)
    private Org org;

    @Schema(description="是否可以退回重答")
    @TableField(exist = false)
    private boolean back;
}