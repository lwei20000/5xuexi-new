package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;
import java.util.Date;
import java.util.List;

/**
 * 用户
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:13
 */
@Data
@Schema(name = "User", description = "用户")
@TableName("sys_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户id")
    @TableId(type = IdType.AUTO)
    private Integer userId;

    @Schema(description="账号")
    @NotBlank(message = "账号不能为空" ,groups = {UserDefault.class})
    @Length(max = 100, message = "账号最大长度不能超过{max}",groups = {UserDefault.class})
    private String username;

    @Schema(description="密码")
    private String password;

    @Schema(description="姓名")
    @NotBlank(message = "姓名不能为空",groups = {Default.class,UserUpdate.class})
    @Length(max = 200, message = "姓名最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String realname;

    @Schema(description="头像")
    @Length(max = 200, message = "头像最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String avatar;

    @Schema(description="性别, 字典标识")
    @Length(max = 10, message = "性别最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String sex;

    @Schema(description="手机号")
    @Length(min = 11,max = 11, message = "手机号只能是{max}位数",groups = {Default.class,UserUpdate.class})
    private String phone;

    @Schema(description="邮箱")
    @Length(max = 200, message = "邮箱最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String email;

    @Schema(description="身份证号")
    @Length(max = 200, message = "身份证号最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String idCard;

    @Schema(description="民族")
    private String nation;

    @Schema(description="政治面貌")
    private String politics;

    @Schema(description="工作单位")
    @Length(max = 500, message = "工作单位最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String organization;

    @Schema(description="家庭地址")
    @Length(max = 500, message = "家庭地址最大长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String address;

    @Schema(description="出生日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",timezone = "Asia/Shanghai")
    private Date birthday;

    @Schema(description="登记照")
    @Length(max = 200, message = "登记照片长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String idPhoto;

    @Schema(description="身份证正面照片")
    @Length(max = 200, message = "身份证正面照片长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String idCard1;

    @Schema(description="身份证反面照片")
    @Length(max = 200, message = "身份证反面照片长度不能超过{max}",groups = {Default.class,UserUpdate.class})
    private String idCard2;

    @Schema(description="默认账号")
    private Integer systemDefault;

    @Schema(description="个人简介")
    private String introduction;

    @Schema(description="状态, 0正常, 1冻结")
    private Integer status;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="注册时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="最近使用的租户")
    private Integer currentTenantId;

    @Schema(description="角色列表")
    @TableField(exist = false)
    private List<Role> roles;

    @Schema(description="分组列表")
    @TableField(exist = false)
    private List<Group> groups;

    @Schema(description="所在机构列表")
    @TableField(exist = false)
    private List<Org> orgs;

    @Schema(description="权限列表")
    @TableField(exist = false)
    private List<Menu> authorities;

    @Schema(description="租户列表")
    @TableField(exist = false)
    private List<Tenant> tenants;

    @Schema(description = "机构Id",hidden = true)
    @TableField(exist = false)
    private Integer orgId;


    @Schema(description = "用户关联租户的时间",hidden = true)
    @TableField(exist = false)
    private Date userTenantCreateTime;

    @Schema(description="用户类型")
    @TableField(exist = false)
    private String userType;

    @Schema(description = "租户ID")
    @TableField(exist = false)
    private Integer  tenantId;

    public interface UserUpdate extends Default {

    }

    public interface UserDefault extends Default {

    }
}
