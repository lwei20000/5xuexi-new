package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 角色
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:01
 */
@Data
@Schema(name = "Role", description = "角色")
@TableName("sys_role")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="角色id")
    @TableId(type = IdType.AUTO)
    private Integer roleId;

    @Schema(description="上级id, 0是顶级")
    private Integer parentId;

    @Schema(description="角色标识")
    @NotBlank(message = "角色标识不能为空")
    @Length(max = 200, message = "角色标识最大长度不能超过{max}")
    private String roleCode;

    @Schema(description="角色名称")
    @NotBlank(message = "角色名称不能为空")
    @Length(max = 200, message = "角色名称最大长度不能超过{max}")
    private String roleName;

    @Schema(description="数据范围标识")
    @NotBlank(message = "数据范围标识不能为空")
    private String scopeType;

    @Schema(description = "排序号")
    private Integer sortNumber;

    @Schema(description="备注")
    @Length(max = 400, message = "备注最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String comments;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="默认角色Id")
    private Integer systemDefault;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description = "用户ID",hidden = true)
    @TableField(exist = false)
    private Integer userId;

    @Schema(description="机构列表")
    @TableField(exist = false)
    private List<Org> orgs;
}
