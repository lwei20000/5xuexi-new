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
 * 分组
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Data
@Schema(name = "Group", description = "分组")
@TableName("sys_group")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "分组id")
    @TableId(type = IdType.AUTO)
    private Integer groupId;

    @Schema(description = "上级id, 0是顶级")
    private Integer parentId;

    @Schema(description = "分组代码")
    @NotBlank(message = "分组代码不能为空")
    @Length(max = 200, message = "分组代码最大长度不能超过{max}")
    private String groupCode;

    @Schema(description = "分组名称")
    @NotBlank(message = "分组名称不能为空")
    @Length(max = 200, message = "分组名称最大长度不能超过{max}")
    private String groupName;

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
}
