package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.entity.UserGroup;
import com.struggle.common.system.param.GroupParam;
import com.struggle.common.system.service.GroupService;
import com.struggle.common.system.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分组控制器
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Tag(name = "分组管理",description = "GroupController")
@RestController
@RequestMapping("/api/system/group")
public class GroupController extends BaseController {
    @Resource
    private GroupService groupService;
    @Resource
    private UserGroupService userGroupService;

    @AuthorityAnnotation(value = "sys:group:list:6b1e1f29fd224c978ef50950539fa0b7")
    @OperationLog
    @Operation(method = "page",description="分页查询分组")
    @GetMapping("/page")
    public ApiResult<PageResult<Group>> page(GroupParam param) {
        return success(groupService.pageRel(param));
    }

    @AuthorityAnnotation(value = "sys:group:list:6b1e1f29fd224c978ef50950539fa0b7")
    @OperationLog
    @Operation(method = "list",description="查询全部分组")
    @GetMapping()
    public ApiResult<List<Group>> list(GroupParam param) {
        return success(groupService.listRel(param));
    }

    @AuthorityAnnotation("sys:group:save:2e2ca0f788b44a82b06295b0d13b2552")
    @OperationLog
    @Operation(method = "add",description="添加分组")
    @PostMapping()
    public ApiResult<?> add(@RequestBody Group org) {
        ValidatorUtil._validBean(org);
        if ( groupService.saveGroup(org)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:group:update:13a5e6afdbe84b54bd3b6535131d89f9")
    @OperationLog
    @Operation(method = "update",description="修改分组")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody Group org) {
        ValidatorUtil._validBean(org);
        if (groupService.update(org)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:group:remove:6223a9183d104654875054cd3f2131a9")
    @OperationLog
    @Operation(method = "remove",description="删除分组")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(groupService.count(Wrappers.<Group>lambdaQuery().eq(Group::getParentId,id))>0){
            return fail("先删除子分组，再删除");
        }
        if (groupService.removeById(id)) {
            //删除用户与组的关联
            userGroupService.remove(new LambdaQueryWrapper<UserGroup>().eq(UserGroup::getGroupId,id));
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
