package com.struggle.common.base.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 专业与课程关系查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="MajorCourseParam", description = "专业与课程关系查询参数")
public class MajorCourseParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="专业与课程关系id")
    @QueryField(type = QueryType.EQ)
    private Integer id;

    @Schema(description="专业年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="学年")
    @TableField(exist = false)
    private String academicYear;

    @Schema(description="上/下学年")
    @TableField(exist = false)
    private String academicSemester;

    @Schema(description="学期")
    private String semester;

    @Schema(description="专业id")
    @QueryField(type = QueryType.EQ)
    private Integer majorId;

    @Schema(description="课程id")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="试卷状态 0：未发布 1：已发布")
    @TableField(exist = false)
    private Integer  paperStatus;

    @Schema(description="考试开始时间")
    @TableField(exist = false)
    private Date startTime;

    @Schema(description="考试结束时间")
    @TableField(exist = false)
    private Date  endTime;
}
