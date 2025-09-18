package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 组织机构
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Data
@Schema(name = "Org", description = "组织机构")
@TableName("sys_org")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Org implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "机构id")
    @TableId(type = IdType.AUTO)
    private Integer orgId;

    @Schema(description = "上级id, 0是顶级")
    @NotNull(message = "上级机构不能为空")
    private Integer parentId;

    @Schema(description = "机构代码")
    @NotBlank(message = "机构代码不能为空")
    @Length(max = 200, message = "机构代码最大长度不能超过{max}")
    private String orgCode;

    @Schema(description = "机构名称")
    @NotBlank(message = "机构名称不能为空")
    @Length(max = 200, message = "机构名称最大长度不能超过{max}")
    private String orgName;

    @Schema(description = "机构全称")
    @Length(max = 500, message = "机构全称最大长度不能超过{max}")
    private String orgFullName;

    @Schema(description = "机构全称代码")
    @Length(max = 300, message = "机构全称代码最大长度不能超过{max}")
    private String orgFullCode;

    @Schema(description = "机构类型, 字典标识")
    @NotBlank(message = "机构类型不能为空")
    @Length(max = 100, message = "机构类型最大长度不能超过{max}")
    private String orgType;

    @Schema(description = "排序号")
    private Integer sortNumber;

    @Schema(description = "备注")
    @Length(max = 400, message = "备注最大长度不能超过{max}")
    private String comments;

    @Schema(description = "是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description="用户ID",hidden = true)
    @TableField(exist = false)
    private Integer userId;

    @Schema(description="角色ID",hidden = true)
    @TableField(exist = false)
    private Integer roleId;

    @Schema(description="主管列表")
    @TableField(exist = false)
    private List<User> leaders;
}
