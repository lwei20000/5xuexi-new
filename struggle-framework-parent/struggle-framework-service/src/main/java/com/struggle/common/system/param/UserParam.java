package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户查询参数
 *
 * @author EleAdmin
 * @since 2021-08-29 20:35:09
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "用户查询参数")
public class UserParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户id")
    @QueryField(type = QueryType.EQ)
    private Integer userId;

    @Schema(description="账号")
    private String username;

    @Schema(description="姓名")
    private String realname;

    @Schema(description="性别(字典)")
    @QueryField(type = QueryType.EQ)
    private String sex;

    @Schema(description="手机号")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="身份证号")
    private String idCard;

    @Schema(description="出生日期")
    private String birthday;

    @Schema(description="状态, 0正常, 1冻结")
    @QueryField(type = QueryType.EQ)
    private Integer status;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="角色id")
    @TableField(exist = false)
    private Integer roleId;

    @Schema(description="角色CODE")
    @TableField(exist = false)
    private String roleCode;

    @Schema(description="分组id")
    @TableField(exist = false)
    private Integer groupId;

    @Schema(description="分组CODE")
    @TableField(exist = false)
    private String groupCode;

    @Schema(description="机构Id")
    @TableField(exist = false)
    private Integer orgId;

    @Schema(description="机构CODE")
    @TableField(exist = false)
    private String orgCode;

    @Schema(description="机构Ids")
    @TableField(exist = false)
    private List<Integer> orgIds;

    @Schema(description="搜索关键字")
    @TableField(exist = false)
    private String keywords;

}
