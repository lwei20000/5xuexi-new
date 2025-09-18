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
import com.struggle.common.system.entity.DefaultRoleMenu;
import com.struggle.common.system.param.DefaultMenuParam;
import com.struggle.common.system.service.DefaultMenuService;
import com.struggle.common.system.service.DefaultRoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认菜单控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:23
 */
@Tag(name = "默认菜单管理",description = "DefaultMenuController")
@RestController
@RequestMapping("/api/system/default-menu")//
public class DefaultMenuController extends BaseController {
    @Resource
    private DefaultMenuService defaultmenuService;
    @Resource
    private DefaultRoleMenuService roleMenuService;

    @AuthorityAnnotation("sys:defaultMenu:list:b14b559a8c6f416db01105f421deb03c")
    @OperationLog
    @Operation(method = "page",description="分页查询菜单")
    @GetMapping("/page")
    public ApiResult<PageResult<DefaultMenu>> page(DefaultMenuParam param) {
        PageParam<DefaultMenu, DefaultMenuParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return success(defaultmenuService.page(page, page.getWrapper()));
    }

    @AuthorityAnnotation("sys:defaultMenu:list:b14b559a8c6f416db01105f421deb03c")
    @OperationLog
    @Operation(method = "list",description="查询全部菜单")
    @GetMapping()
    public ApiResult<List<DefaultMenu>> list(DefaultMenuParam param) {
        PageParam<DefaultMenu, DefaultMenuParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return success(defaultmenuService.list(page.getOrderWrapper()));
    }

    @AuthorityAnnotation("sys:defaultMenu:save:37ff04741d194be5848aa514e29b5e65")
    @OperationLog
    @Operation(method = "add",description="添加菜单")
    @PostMapping()
    public ApiResult<?> add(@RequestBody DefaultMenu menu) {
        ValidatorUtil._validBean(menu);
        if (defaultmenuService.save(menu)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:defaultMenu:update:5292bdfaa7204c9da427b8c5950c19e2")
    @OperationLog
    @Operation(method = "update",description="修改菜单")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody DefaultMenu menu) {
        ValidatorUtil._validBean(menu);
        if(menu.getParentId() !=null && menu.getParentId() !=0){
            if(menu.getMenuId().equals(menu.getParentId())){
                throw new BusinessException("上级菜单不能是当前菜单");
            }
            List<Integer> childrenIds = new ArrayList<>();
            defaultmenuService.getMenuChildrenIds(menu,childrenIds);
            if(childrenIds.contains(menu.getParentId())){
                throw new BusinessException("上级菜单不能是当前菜单的子菜单");
            }
        }
        if (defaultmenuService.update(menu, Wrappers.<DefaultMenu>lambdaUpdate()
                .set(DefaultMenu::getParentId,menu.getParentId())
                .set(DefaultMenu::getPath,menu.getPath())
                .set(DefaultMenu::getComponent,menu.getComponent())
                .set(DefaultMenu::getAuthority,menu.getAuthority())
                .set(DefaultMenu::getIcon,menu.getIcon())
                .set(DefaultMenu::getMeta,menu.getMeta())
                .eq(DefaultMenu::getMenuId,menu.getMenuId()))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:defaultMenu:remove:a20ab8856bff4c8b9ee4eb9846fafb24")
    @OperationLog
    @Operation(method = "remove",description="删除菜单")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(defaultmenuService.count(Wrappers.<DefaultMenu>lambdaQuery().eq(DefaultMenu::getParentId,id))>0){
            return fail("先删除子菜单后，再删除");
        }
        if (defaultmenuService.removeById(id)) {
            //删除默认菜单与默认角色的关联
            roleMenuService.remove(new LambdaQueryWrapper<DefaultRoleMenu>().eq(DefaultRoleMenu::getMenuId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
