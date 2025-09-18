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
 * 用户与专业关系
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserMajor", description = "用户与专业关系")
@TableName("t_user_major")
public class UserMajor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户与专业关系id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户id")
    private Integer userId;

    @Schema(description="专业id")
    private Integer majorId;

    @Schema(description="学号")
    private String userNumber;

    @Schema(description="录取照片")
    private String userPicture;

    @Schema(description="机构Id")
    private Integer orgId;

    @Schema(description="论文标题")
    private String thesisName;

    @Schema(description="论文附件")
    private String thesisFile;

    @Schema(description="论文分数")
    private String thesisScore;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="租户id")
    private Integer tenantId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="专业年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="专业")
    @TableField(exist = false)
    private Major major;

    @Schema(description="用户")
    @TableField(exist = false)
    private User user;

    @Schema(description="机构")
    @TableField(exist = false)
    private Org org;
}
