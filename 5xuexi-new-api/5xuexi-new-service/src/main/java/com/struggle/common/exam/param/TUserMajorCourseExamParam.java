package com.struggle.common.exam.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: TUserMajorCourseExamParam
 * @Description: 用户专业考试记录-Param对象
 * @author xsk
 */
@Schema(name = "TUserMajorCourseExamParam",description= "用户专业考试记录Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TUserMajorCourseExamParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="用户专业课程考试Id")
    private Integer  id;

    @Schema(description="用户专业课程考试Ids")
    @TableField(exist = false)
    private List<Integer>  ids;

    @Schema(description="用户ID")
    @QueryField(type = QueryType.EQ)
    private Integer  userId;

    @Schema(description="专业课程考试ID")
    @QueryField(type = QueryType.EQ)
    private Integer  majorCourseExamId;

    @Schema(description="考试开始时间")
    private Date startTime;

    @Schema(description="考试结束时间")
    private Date  endTime;

    @Schema(description="成绩")
    private Integer  score;

    @Schema(description="状态：0 ：答题进行中 1：提交未批改 2：提交已批改 3：未考试")
    private Integer  status;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="专业ID")
    @TableField(exist = false)
    private Integer  majorId;

    @Schema(description="课程ID")
    @TableField(exist = false)
    private Integer  courseId;

    @Schema(description="机构Id")
    @TableField(exist = false)
    private Integer orgId;

    @Schema(description="机构Ids")
    @TableField(exist = false)
    private List<Integer> orgIds;

    @Schema(description="专业年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="学年")
    @TableField(exist = false)
    private String academicYear;

    @Schema(description="学期")
    @TableField(exist = false)
    private String semester;

    @Schema(description="上/下学年")
    @TableField(exist = false)
    private String academicSemester;
}