package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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
@Schema(name ="CourseAlias", description = "课程别名信息")
@TableName("t_course_alias")
public class CourseAlias implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="课程别名id")
    @TableId(value = "course_alias_id", type = IdType.AUTO)
    private Integer courseAliasId;

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="课程代码别名")
    private String courseCodeAlias;

    @Schema(description="课程名称别名")
    private String courseNameAlias;

    @Schema(description="备注")
    private String comments;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="租户id")
    private Integer tenantId;
}
