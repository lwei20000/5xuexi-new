package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.entity.RoleMenu;
import com.struggle.common.system.service.MenuService;
import com.struggle.common.system.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色菜单控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:01
 */
@Tag(name = "角色菜单管理",description = "RoleMenuController")
@RestController
@RequestMapping("/api/system/role-menu")
public class RoleMenuController extends BaseController {
    @Resource
    private RoleMenuService roleMenuService;
    @Resource
    private MenuService menuService;

    @AuthorityAnnotation("sys:role:list:4be7267f6e9a47d0b1aa39e38c5815f7")
    @OperationLog
    @Operation(method = "list",description="查询角色菜单")
    @GetMapping("/{appType}/{roleId}")
    public ApiResult<List<Menu>> list(@PathVariable("appType") String appType,@PathVariable("roleId") Integer roleId) {
        List<Menu> menus = menuService.list(new LambdaQueryWrapper<Menu>().eq(Menu::getAppType,appType).orderByAsc(Menu::getSortNumber));
        List<RoleMenu> roleMenus = roleMenuService.list(new LambdaQueryWrapper<RoleMenu>().select(RoleMenu::getRoleId,RoleMenu::getMenuId).eq(RoleMenu::getRoleId, roleId));
        if(!CollectionUtils.isEmpty(roleMenus)){
            Map<Integer,Integer> roleMenuMap = new HashMap<>(roleMenus.size());
            for(RoleMenu roleMenu:roleMenus){
                roleMenuMap.put(roleMenu.getMenuId(),roleMenu.getRoleId());
            }
            for (Menu menu : menus) {
                menu.setChecked(roleMenuMap.get(menu.getMenuId())!=null);
            }
        }
        return success(menus);
    }

    @AuthorityAnnotation("sys:role:update:ea93c40c0e32419c9e3b5201f357b988")
    @OperationLog
    @Operation(method = "update",description="修改角色菜单")
    @PostMapping("/{appType}/{roleId}")
    @Transactional
    public ApiResult<?> update(@PathVariable("appType") String appType,@PathVariable("roleId") Integer roleId, @RequestBody List<Integer> menuIds) {
        List<Menu> menus = menuService.list(new LambdaQueryWrapper<Menu>().select(Menu::getMenuId).eq(Menu::getAppType,appType));
        if(!CollectionUtils.isEmpty(menus)){
            List<Integer> _menuIds = new ArrayList<>(menus.size());
            for (Menu menu:menus){
                _menuIds.add(menu.getMenuId());
            }
            roleMenuService.remove(new LambdaUpdateWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId).in(RoleMenu::getMenuId,_menuIds));
        }

        if (!CollectionUtils.isEmpty(menuIds)) {
            List<RoleMenu> roleMenuList = new ArrayList<>(menuIds.size());
            for (Integer menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuList.add(roleMenu);
            }
            if (!roleMenuService.saveBatch(roleMenuList)) {
                throw new BusinessException("保存失败");
            }
        }
        return success("保存成功");
    }
}
