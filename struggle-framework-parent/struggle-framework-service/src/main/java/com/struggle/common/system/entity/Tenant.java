package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Data
@Schema(name = "Tenant", description = "租户")
@TableName("sys_tenant")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "租户id")
    @TableId(value = "tenant_id", type = IdType.AUTO)
    private Integer tenantId;

    @Schema(description="上级id, 0是顶级")
    private Integer parentId;

    @Schema(description = "租户名称")
    @NotBlank(message = "租户名称不能为空")
    @Length(max = 200, message = "租户名称最大长度不能超过{max}")
    private String tenantName;

    @Schema(description = "租户全称")
    @Length(max = 500, message = "租户全称最大长度不能超过{max}")
    private String tenantFullName;

    @Schema(description = "租户代码")
    @Length(max = 300, message = "租户代码最大长度不能超过{max}")
    private String tenantCode;

    @Schema(description="租户类型")
    @NotBlank(message = "租户类型不能为空")
    @Length(max = 100, message = "租户类型最大长度不能超过{max}")
    private String tenantType;

    @Schema(description = "租户LOGO")
    @Length(max = 100, message = "租户LOGO最大长度不能超过{max}")
    private String tenantLogo;

    @Schema(description = "租户简介")
    private String comments;

    @Schema(description = "是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="用户id")
    @TableField(exist = false)
    private Integer userId;
}
