package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户机构
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:23
 */
@Data
@Schema(name = "UserOrg", description = "用户机构")
@TableName("sys_user_org")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserOrg implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="主键id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户id")
    @NotNull(message = "用户不能为空")
    private Integer userId;

    @Schema(description="机构id")
    @NotNull(message = "机构不能为空")
    private Integer orgId;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="机构名称")
    @TableField(exist = false)
    private String orgName;

}
