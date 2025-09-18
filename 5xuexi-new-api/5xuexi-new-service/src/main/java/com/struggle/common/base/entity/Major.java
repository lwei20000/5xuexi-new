package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 招生专业
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="Major", description = "招生专业")
@TableName("t_major")
public class Major implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="专业id")
    @TableId(value = "major_id", type = IdType.AUTO)
    private Integer majorId;

    @Schema(description="专业年份")
    private String majorYear;

    @Schema(description="专业代码")
    private String majorCode;

    @Schema(description="专业名称")
    private String majorName;

    @Schema(description="专业图片")
    private String majorPicture;

    @Schema(description="专业教育类别")
    private String majorType;

    @Schema(description="专业层次")
    private String majorGradation;

    @Schema(description="专业学习形式")
    private String majorForms;

    @Schema(description="学制")
    private String majorLength;

    @Schema(description="排序号")
    private Integer sortNumber;

    @Schema(description="专业简介")
    private String comments;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="最新课程学期")
    @TableField(exist = false)
    private Integer lastSemester;
}
