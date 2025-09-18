package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 菜单
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:17
 */
@Data
@Schema(name = "Menu", description = "菜单")
@TableName("sys_menu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Menu{
    private static final long serialVersionUID = 1L;
    public static final int TYPE_MENU = 0;  // 菜单类型
    public static final int TYPE_BTN = 1;  // 按钮类型

    @Schema(description="菜单id")
    @TableId(type = IdType.AUTO)
    private Integer menuId;

    @Schema(description="上级id, 0是顶级")
    private Integer parentId;

    @Schema(description="菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    @Length(max = 200, message = "菜单名称最大长度不能超过{max}")
    private String title;

    @Schema(description="菜单路由地址")
    @Length(max = 200, message = "菜单路由地址最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String path;

    @Schema(description="菜单组件地址")
    @Length(max = 200, message = "菜单组件地址最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String component;

    @Schema(description="菜单类型, 0：目录 1：菜单 2：按钮")
    @NotNull(message = "菜单类型不能为空")
    @Range(min=0,max = 2,message ="菜单类型取值范围：{min}~{max}" )
    private Integer menuType;

    @Schema(description="排序号")
    private Integer sortNumber;

    @Schema(description="权限标识")
    @Length(max = 200, message = "权限标识最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String authority;

    @Schema(description="菜单图标")
    @Length(max = 200, message = "菜单图标最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String icon;

    @Schema(description="是否隐藏, 0否, 1是(仅注册路由不显示左侧菜单)")
    @NotNull(message = "是否隐藏不能为空")
    @Range(min=0,max = 1,message ="是否隐藏取值范围：{min}~{max}" )
    private Integer hide;

    @Schema(description="路由元信息")
    @Length(max = 800, message = "路由元信息最大长度不能超过{max}")
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String meta;

    @Schema(description="应用类型")
    @NotBlank(message = "应用类型不能为空")
    @Length(max = 100, message = "应用类型最大长度不能超过{max}")
    private String appType;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="默认菜单Id")
    private Integer systemDefault;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="子菜单")
    @TableField(exist = false)
    private List<Menu> children;

    @Schema(description="角色权限树选中状态")
    @TableField(exist = false)
    private Boolean checked;

    @Schema(description = "角色id",hidden = true)
    @TableField(exist = false)
    private Integer roleId;
}
