package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.entity.RoleMenu;
import com.struggle.common.system.entity.RoleOrg;
import com.struggle.common.system.entity.UserRole;
import com.struggle.common.system.param.RoleParam;
import com.struggle.common.system.service.RoleMenuService;
import com.struggle.common.system.service.RoleOrgService;
import com.struggle.common.system.service.RoleService;
import com.struggle.common.system.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@Tag(name = "角色管理",description = "RoleController")
@RestController
@RequestMapping("/api/system/role")
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;
    @Resource
    private RoleOrgService roleOrgService;
    @Resource
    private UserRoleService userRoleService;
    @Resource
    private RoleMenuService roleMenuService;

    @AuthorityAnnotation("sys:role:list:4be7267f6e9a47d0b1aa39e38c5815f7")
    @OperationLog
    @Operation(method = "page",description="分页查询角色")
    @GetMapping("/page")
    public ApiResult<PageResult<Role>> page(RoleParam param) {
        PageParam<Role, RoleParam> page = new PageParam<>(param);
        return success(roleService.page(page, page.getWrapper()));
    }

    @AuthorityAnnotation("sys:role:list:4be7267f6e9a47d0b1aa39e38c5815f7")
    @OperationLog
    @Operation(method = "list",description="查询全部角色")
    @GetMapping()
    public ApiResult<List<Role>> list(RoleParam param) {
        return success(roleService.listRel(param));
    }

    @AuthorityAnnotation("sys:role:save:43733cfe2cf94e3db1c953e1275e6bfa")
    @OperationLog
    @Operation(method = "save",description="添加角色")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Role role) {
        ValidatorUtil._validBean(role);
        if (roleService.saveRole(role)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:role:update:ea93c40c0e32419c9e3b5201f357b988")
    @OperationLog
    @Operation(method = "update",description="修改角色")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody Role role) {
        ValidatorUtil._validBean(role);
        if (roleService.updateRole(role)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:role:remove:ba89c990e23a4fe5adca033827705f23")
    @OperationLog
    @Operation(method = "remove",description="删除角色")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(roleService.count(Wrappers.<Role>lambdaQuery().eq(Role::getRoleId, id).gt(Role::getSystemDefault,0))>0){
            return fail("全局的角色，不能删除");
        }
       if(roleService.count(Wrappers.<Role>lambdaQuery().eq(Role::getParentId, id))>0){
           return fail("先删除子角色，再删除");
       }
        if (roleService.removeById(id)) {
            roleOrgService.remove(new LambdaQueryWrapper<RoleOrg>().eq(RoleOrg::getRoleId,id));
            userRoleService.remove(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRoleId,id));
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:role:remove:ba89c990e23a4fe5adca033827705f23")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除角色")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(roleService.count(Wrappers.<Role>lambdaQuery().in(Role::getRoleId, ids).gt(Role::getSystemDefault,0))>0){
            return fail("包含全局的角色，不能删除");
        }
        if(roleService.count(Wrappers.<Role>lambdaQuery().in(Role::getParentId, ids))>0){
            return fail("先删除子角色，再删除");
        }
        if (roleService.removeByIds(ids)) {
            roleOrgService.remove(new LambdaQueryWrapper<RoleOrg>().in(RoleOrg::getRoleId,ids));
            userRoleService.remove(new LambdaQueryWrapper<UserRole>().in(UserRole::getRoleId,ids));
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId,ids));
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
