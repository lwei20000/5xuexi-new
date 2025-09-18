package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户课程信息
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserCourse", description = "用户课程信息")
@TableName("t_user_course")
public class UserCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户课程id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户id")
    private Integer userId;

    @Schema(description="专业id")
    private Integer majorId;

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="学习进度")
    private Integer learingProgress;

    @Schema(description="学习行为成绩")
    private Integer learingScore;

    @Schema(description="考试成绩")
    private Integer examScore;

    @Schema(description="总成绩")
    private Integer totalScore;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="机构Id")
    @TableField(exist = false)
    private Integer orgId;

    @Schema(description="专业")
    @TableField(exist = false)
    private Major major;

    @Schema(description="用户")
    @TableField(exist = false)
    private User user;

    @Schema(description="用户专业")
    @TableField(exist = false)
    private UserMajor userMajor;

    @Schema(description="课程")
    @TableField(exist = false)
    private Course course;

    @Schema(description="机构")
    @TableField(exist = false)
    private Org org;

    @Schema(description="学期")
    @TableField(exist = false)
    private String semester;

    @Schema(description="招生年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="学号")
    @TableField(exist = false)
    private String userNumber;

}
