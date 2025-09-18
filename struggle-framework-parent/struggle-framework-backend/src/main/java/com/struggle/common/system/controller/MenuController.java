package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.DefaultMenu;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.entity.RoleMenu;
import com.struggle.common.system.param.MenuParam;
import com.struggle.common.system.service.DefaultMenuService;
import com.struggle.common.system.service.MenuService;
import com.struggle.common.system.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:23
 */
@Tag(name = "菜单管理",description = "MenuController")
@RestController
@RequestMapping("/api/system/menu")
public class MenuController extends BaseController {
    @Resource
    private MenuService menuService;
    @Resource
    private DefaultMenuService defaultMenuService;
    @Resource
    private RoleMenuService roleMenuService;

    @AuthorityAnnotation("sys:menu:list:5ba451db2eb94070b454cb182ab32a6c")
    @OperationLog
    @Operation(method = "page",description="分页查询菜单")
    @GetMapping("/page")
    public ApiResult<PageResult<Menu>> page(MenuParam param) {
        PageParam<Menu, MenuParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return success(menuService.page(page, page.getWrapper()));
    }

    @AuthorityAnnotation("sys:menu:list:5ba451db2eb94070b454cb182ab32a6c")
    @OperationLog
    @Operation(method = "list",description="查询全部菜单")
    @GetMapping()
    public ApiResult<List<Menu>> list(MenuParam param) {
        PageParam<Menu, MenuParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return success(menuService.list(page.getOrderWrapper()));
    }

    @AuthorityAnnotation("sys:menu:save:8000d1a8cad74796a4c78fefe0ccac41")
    @OperationLog
    @Operation(method = "add",description="添加菜单")
    @PostMapping()
    public ApiResult<?> add(@RequestBody Menu menu) {
        ValidatorUtil._validBean(menu);
        menu.setSystemDefault(0);
        if(StringUtils.hasText(menu.getAuthority())){
            long count = defaultMenuService.count(new LambdaQueryWrapper<DefaultMenu>().eq(DefaultMenu::getAuthority, menu.getAuthority()));
            if(count>0){
                return fail("权限标识已在默认菜单里面使用，不允许使用");
            }
        }

        if (menuService.save(menu)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:menu:update:9a7aab59-b6a749c68ab1502276a1cdcd")
    @OperationLog
    @Operation(method = "update",description="修改菜单")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody Menu menu) {
        ValidatorUtil._validBean(menu);
        if(menuService.count(Wrappers.<Menu>lambdaQuery().eq(Menu::getMenuId, menu.getMenuId()).gt(Menu::getSystemDefault,0))>0){
            return fail("全局的菜单，不能修改");
        }
        if(StringUtils.hasText(menu.getAuthority())){
            long count = defaultMenuService.count(new LambdaQueryWrapper<DefaultMenu>().eq(DefaultMenu::getAuthority, menu.getAuthority()));
            if(count>0){
                return fail("权限标识已在默认菜单里面使用，不允许使用");
            }
        }
        if(menu.getParentId() !=null && menu.getParentId() !=0){
            if(menu.getMenuId().equals(menu.getParentId())){
                throw new BusinessException("上级菜单不能是当前菜单");
            }
            List<Integer> childrenIds = new ArrayList<>();
            menuService.getMenuChildrenIds(menu,childrenIds);
            if(childrenIds.contains(menu.getParentId())){
                throw new BusinessException("上级菜单不能是当前菜单的子菜单");
            }
        }
        if (menuService.update(menu, Wrappers.<Menu>lambdaUpdate()
                .set(Menu::getParentId,menu.getParentId())
                .set(Menu::getPath,menu.getPath())
                .set(Menu::getComponent,menu.getComponent())
                .set(Menu::getAuthority,menu.getAuthority())
                .set(Menu::getIcon,menu.getIcon())
                .set(Menu::getMeta,menu.getMeta())
                .eq(Menu::getMenuId,menu.getMenuId()))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:menu:remove:47e47bdd5fe444e4846a3a9df67dcd54")
    @OperationLog
    @Operation(method = "remove",description="删除菜单")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(menuService.count(Wrappers.<Menu>lambdaQuery().eq(Menu::getParentId,id))>0){
            return fail("先删除子菜单后，再删除");
        }
        if(menuService.count(Wrappers.<Menu>lambdaQuery().eq(Menu::getMenuId, id).gt(Menu::getSystemDefault,0))>0){
            return fail("全局的菜单，不能删除");
        }
        if (menuService.removeById(id)) {
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:menu:remove:47e47bdd5fe444e4846a3a9df67dcd54")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除菜单")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(menuService.count(Wrappers.<Menu>lambdaQuery().in(Menu::getParentId,ids))>0){
            return fail("先删除子菜单后，再删除");
        }
        if(menuService.count(Wrappers.<Menu>lambdaQuery().in(Menu::getMenuId, ids).gt(Menu::getSystemDefault,0))>0){
            return fail("全局的菜单，不能删除");
        }
        if (menuService.removeByIds(ids)) {
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getMenuId,ids));
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
