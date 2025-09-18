package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.DefaultMenu;
import com.struggle.common.system.entity.DefaultRoleMenu;
import com.struggle.common.system.service.DefaultMenuService;
import com.struggle.common.system.service.DefaultRoleMenuService;
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
@Tag(name = "默认角色菜单管理",description = "DefaultRoleMenuController")
@RestController
@RequestMapping("/api/system/default-role-menu")
public class DefaultRoleMenuController extends BaseController {
    @Resource
    private DefaultRoleMenuService roleMenuService;
    @Resource
    private DefaultMenuService menuService;

    @AuthorityAnnotation("sys:defaultRole:list:bedf76b244884ac599971a8742b3accc")
    @OperationLog
    @Operation(method = "list",description="查询角色菜单")
    @GetMapping("/{appType}/{id}")
    public ApiResult<List<DefaultMenu>> list(@PathVariable("appType") String appType,@PathVariable("id") Integer roleId) {
        List<DefaultMenu> menus = menuService.list(new LambdaQueryWrapper<DefaultMenu>().eq(DefaultMenu::getAppType,appType).orderByAsc(DefaultMenu::getSortNumber));
        List<DefaultRoleMenu> roleMenus = roleMenuService.list(new LambdaQueryWrapper<DefaultRoleMenu>().select(DefaultRoleMenu::getRoleId,DefaultRoleMenu::getMenuId).eq(DefaultRoleMenu::getRoleId, roleId));
        if(!CollectionUtils.isEmpty(roleMenus)){
            Map<Integer,Integer> defaultRoleMenuMap = new HashMap<>(roleMenus.size());
            for(DefaultRoleMenu defaultRoleMenu:roleMenus){
                defaultRoleMenuMap.put(defaultRoleMenu.getMenuId(),defaultRoleMenu.getRoleId());
            }
            for (DefaultMenu menu : menus) {
                menu.setChecked(defaultRoleMenuMap.get(menu.getMenuId()) !=null);
            }
        }

        return success(menus);
    }

    @AuthorityAnnotation("sys:defaultRole:update:7c548eca9215461dab0df93bcfbc730f")
    @OperationLog
    @Operation(method = "update",description="修改角色菜单")
    @PostMapping("/{appType}/{roleId}")
    @Transactional
    public ApiResult<?> update(@PathVariable("appType") String appType,@PathVariable("roleId") Integer roleId, @RequestBody List<Integer> menuIds) {
        List<DefaultMenu> menus = menuService.list(new LambdaQueryWrapper<DefaultMenu>().select(DefaultMenu::getMenuId).eq(DefaultMenu::getAppType,appType));
        if(!CollectionUtils.isEmpty(menus)){
            List<Integer> _menuIds = new ArrayList<>(menus.size());
            for(DefaultMenu defaultMenu:menus){
                _menuIds.add(defaultMenu.getMenuId());
            }
            roleMenuService.remove(new LambdaUpdateWrapper<DefaultRoleMenu>().eq(DefaultRoleMenu::getRoleId, roleId).in(DefaultRoleMenu::getMenuId,_menuIds));
        }
        if (!CollectionUtils.isEmpty(menuIds)) {
            List<DefaultRoleMenu> roleMenuList = new ArrayList<>(menuIds.size());
            for (Integer menuId : menuIds) {
                DefaultRoleMenu roleMenu = new DefaultRoleMenu();
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
