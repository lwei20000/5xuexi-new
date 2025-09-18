package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.param.TenantParam;
import com.struggle.common.system.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户控制器
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Tag(name = "租户管理",description = "TenantController")
@RestController
@RequestMapping("/api/system/tenant")
public class TenantController extends BaseController {
    @Resource
    private TenantService tenantService;

    @AuthorityAnnotation("sys:tenant:list:dd17c91ab3f14aac8006823a4eab13b8")
    @OperationLog
    @Operation(method = "page",description="分页查询租户")
    @GetMapping("/page")
    public ApiResult<PageResult<Tenant>> page(TenantParam param) {
        // 使用关联查询
        return success(tenantService.pageRel(param));
    }

    @AuthorityAnnotation("sys:tenant:list:dd17c91ab3f14aac8006823a4eab13b8")
    @OperationLog
    @Operation(method = "list",description="查询全部租户")
    @GetMapping()
    public ApiResult<List<Tenant>> list(TenantParam param) {
        // 使用关联查询
        return success(tenantService.listRel(param));
    }

    @AuthorityAnnotation(value = "sys:tenant:list:dd17c91ab3f14aac8006823a4eab13b8")
    @OperationLog
    @Operation(method = "get",description="根据id查询租户")
    @GetMapping("/{id}")
    public ApiResult<Tenant> get(@PathVariable("id") Integer id) {

        return success(tenantService.getById(id));
    }

    @AuthorityAnnotation("sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9")
    @OperationLog
    @Operation(method = "save",description="添加租户")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Tenant tenant) {
        ValidatorUtil._validBean(tenant);
        if (tenantService.saveTenant(tenant)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:tenant:update:07d0e575721f4dc69d64972e57932c3b")
    @OperationLog
    @Operation(method = "update",description="修改租户")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody Tenant tenant) {
        ValidatorUtil._validBean(tenant);
        if (tenantService.updateTenant(tenant)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:tenant:remove:becb660c31664810a0ed85e4e662fcda")
    @OperationLog
    @Operation(method = "remove",description="删除租户")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(tenantService.count(Wrappers.<Tenant>lambdaQuery().eq(Tenant::getParentId, id))>0){
            return fail("先删除子租户，再删除");
        }
        if (tenantService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9")
    @Operation(method = "importBatch",description="导入租户")
    @PostMapping("/import")
    public ApiResult<?> importBatch(@RequestParam(value = "file")MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        return tenantService.importBatch(file,request,response);
    }

    @Operation(method = "templateExport",description="模板导出")
    @AuthorityAnnotation("sys:tenant:save:63feb064eea24feda2e1bdc36bc0e0c9")
    @GetMapping("/templateExport")
    public ApiResult<?> templateExport(HttpServletResponse response) {
        tenantService.templateExport(response);
        return null;
    }
}
