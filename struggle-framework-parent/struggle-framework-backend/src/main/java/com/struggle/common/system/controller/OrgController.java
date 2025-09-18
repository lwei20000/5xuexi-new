package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.OrgLeader;
import com.struggle.common.system.entity.RoleOrg;
import com.struggle.common.system.entity.UserOrg;
import com.struggle.common.system.param.OrgParam;
import com.struggle.common.system.service.OrgLeaderService;
import com.struggle.common.system.service.OrgService;
import com.struggle.common.system.service.RoleOrgService;
import com.struggle.common.system.service.UserOrgService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 组织机构控制器
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Tag(name = "组织机构管理",description = "OrgController")
@RestController
@RequestMapping("/api/system/org")
public class OrgController extends BaseController {
    @Resource
    private OrgService orgService;
    @Resource
    private OrgLeaderService orgLeaderService;
    @Resource
    private UserOrgService userOrgService;
    @Resource
    private RoleOrgService roleOrgService;

    @AuthorityAnnotation(value = "sys:org:list:8b1e1f29fd224c978ef50950539fa0b9",permission = true)
    @OperationLog
    @Operation(method = "page",description="分页查询组织机构")
    @GetMapping("/page")
    public ApiResult<PageResult<Org>> page(OrgParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        return success(orgService.pageRel(param));
    }

    @AuthorityAnnotation(value = "sys:org:list:8b1e1f29fd224c978ef50950539fa0b9",permission = true)
    @OperationLog
    @Operation(method = "list",description="查询全部组织机构")
    @GetMapping()
    public ApiResult<List<Org>> list(OrgParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        return success(orgService.listRel(param));
    }

    @AuthorityAnnotation(value = "sys:org:list:8b1e1f29fd224c978ef50950539fa0b9",permission = true)
    @OperationLog
    @Operation(method = "get",description="根据id查询机构")
    @GetMapping("/{id}")
    public ApiResult<Org> get(@PathVariable("id") Integer id) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        OrgParam param = new OrgParam();
        param.setOrgId(id);
        param.setOrgIds(permission.getOrgIds());
        List<Org> orgs = orgService.listRel(param);
        if(!CollectionUtils.isEmpty(orgs)){
            return success(orgs.get(0));
        }
        return fail("未找到机构",null);
    }

    @AuthorityAnnotation("sys:org:save:2e2ca0f789b44a82b06295b0d13b2551")
    @OperationLog
    @Operation(method = "add",description="添加组织机构")
    @PostMapping()
    public ApiResult<?> add(@RequestBody Org org) {
        ValidatorUtil._validBean(org);
        if ( orgService.saveOrg(org)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:org:update:13a5e5afdbe84b54bd3b6535131d89f9")
    @OperationLog
    @Operation(method = "update",description="修改组织机构")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody Org org) {
        ValidatorUtil._validBean(org);
        if (orgService.update(org)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:org:remove:6233a9183d104654875074cd3f2131a9")
    @OperationLog
    @Operation(method = "remove",description="删除组织机构")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(orgService.count(Wrappers.<Org>lambdaQuery().eq(Org::getParentId,id))>0){
            return fail("先删除子机构，再删除");
        }
        Org byId = orgService.getById(id);
        if(byId != null){
            if(byId.getParentId() == 0){
                return fail("主机构不能删除");
            }
            if (orgService.removeById(id)) {
                orgLeaderService.remove(new LambdaQueryWrapper<OrgLeader>().eq(OrgLeader::getOrgId,id));
                userOrgService.remove(new LambdaQueryWrapper<UserOrg>().eq(UserOrg::getOrgId,id));
                roleOrgService.remove(new LambdaQueryWrapper<RoleOrg>().eq(RoleOrg::getOrgId,id));
                return success("删除成功");
            }
        }
        return fail("删除失败");
    }
}
