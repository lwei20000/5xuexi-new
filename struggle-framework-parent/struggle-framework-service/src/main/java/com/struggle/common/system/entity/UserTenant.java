package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户租户
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:23
 */
@Data
@Schema(name = "UserTenant", description = "用户租户")
@TableName("sys_user_tenant")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTenant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="主键id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户id")
    @NotNull(message = "用户不能为空")
    private Integer userId;

    @Schema(description="用户类型")
    @Length(max = 200, message = "用户类型最大长度不能超过{max}")
    private String userType;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}
