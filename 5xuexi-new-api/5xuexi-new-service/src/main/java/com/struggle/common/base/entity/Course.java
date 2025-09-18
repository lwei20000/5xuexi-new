package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程信息
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="Course", description = "课程信息")
@TableName("t_course")
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="课程id")
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;

    @Schema(description="课程代码")
    private String courseCode;

    @Schema(description="课程名称")
    private String courseName;

    @Schema(description="课程图片")
    private String coursePicture;

    @Schema(description="排序号")
    private Integer sortNumber;

    @Schema(description="备注")
    private String comments;

    @Schema(description="租户id")
    private Integer tenantId;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}
