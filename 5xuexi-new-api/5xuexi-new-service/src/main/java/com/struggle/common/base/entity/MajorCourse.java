package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.exam.entity.TMajorCourseExam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 专业与课程关系
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="MajorCourse", description = "专业与课程关系")
@TableName("t_major_course")
public class MajorCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="专业与课程关系id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="学期")
    private String semester;

    @Schema(description="专业id")
    private Integer majorId;

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="专业")
    @TableField(exist = false)
    private Major major;

    @Schema(description="课程")
    @TableField(exist = false)
    private Course course;

    @Schema(description="招生年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="试卷发布")
    @TableField(exist = false)
    private TMajorCourseExam majorCourseExam;

}
