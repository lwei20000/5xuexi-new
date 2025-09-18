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
import com.struggle.common.system.entity.DefaultRole;
import com.struggle.common.system.entity.DefaultRoleMenu;
import com.struggle.common.system.param.DefaultRoleParam;
import com.struggle.common.system.service.DefaultRoleMenuService;
import com.struggle.common.system.service.DefaultRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 默认角色控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@Tag(name = "默认角色管理",description = "DefaultRoleController")
@RestController
@RequestMapping("/api/system/default-role")
public class DefaultRoleController extends BaseController {
    @Resource
    private DefaultRoleService defaultroleService;
    @Resource
    private DefaultRoleMenuService roleMenuService;

    @AuthorityAnnotation("sys:defaultRole:list:bedf76b244884ac599971a8742b3accc")
    @OperationLog
    @Operation(method = "page",description="分页查询角色")
    @GetMapping("/page")
    public ApiResult<PageResult<DefaultRole>> page(DefaultRoleParam param) {
        PageParam<DefaultRole, DefaultRoleParam> page = new PageParam<>(param);
        return success(defaultroleService.page(page, page.getWrapper()));
    }

    @AuthorityAnnotation("sys:defaultRole:list:bedf76b244884ac599971a8742b3accc")
    @OperationLog
    @Operation(method = "list",description="查询全部角色")
    @GetMapping()
    public ApiResult<List<DefaultRole>> list(DefaultRoleParam param) {
        return success(defaultroleService.listRel(param));
    }

    @AuthorityAnnotation("sys:defaultRole:save:c8507652b3ce41dabd15cedebfafdc1b")
    @OperationLog
    @Operation(method = "save",description="添加角色")
    @PostMapping()
    public ApiResult<?> save(@RequestBody DefaultRole role) {
        ValidatorUtil._validBean(role);
        if (defaultroleService.saveRole(role)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f")
    @OperationLog
    @Operation(method = "update",description="修改角色")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody DefaultRole role) {
        ValidatorUtil._validBean(role);
        if (defaultroleService.updateRole(role)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:defaultRole:remove:4c2709638d8e4e2ebd4aa6c19b06fda6")
    @OperationLog
    @Operation(method = "remove",description="删除角色")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {

        if(defaultroleService.count(Wrappers.<DefaultRole>lambdaQuery().eq(DefaultRole::getParentId, id))>0){
            return fail("先删除子角色，再删除");
        }
        if (defaultroleService.removeById(id)) {
            //删除默认角色与默认角色的关联
            roleMenuService.remove(new LambdaQueryWrapper<DefaultRoleMenu>().eq(DefaultRoleMenu::getRoleId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f")
    @OperationLog
    @Operation(method = "sync",description="同步角色")
    @GetMapping ("/sync/{tenantType}")
    public ApiResult<?> sync(@PathVariable("tenantType") String tenantType) {
        defaultroleService.updateTenant(tenantType);
        return success("同步成功");
    }

    @AuthorityAnnotation("sys:defaultRole:remove:4c2709638d8e4e2ebd4aa6c19b06fda6")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除角色")
    @GetMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(defaultroleService.count(Wrappers.<DefaultRole>lambdaQuery().in(DefaultRole::getParentId, ids))>0){
            return fail("先删除子角色，再删除");
        }
        if (defaultroleService.removeByIds(ids)) {
            //删除默认角色与默认角色的关联
            roleMenuService.remove(new LambdaQueryWrapper<DefaultRoleMenu>().in(DefaultRoleMenu::getRoleId,ids));
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
