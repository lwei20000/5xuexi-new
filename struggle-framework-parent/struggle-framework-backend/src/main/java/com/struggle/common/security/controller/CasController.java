package com.struggle.common.security.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.security.entity.Cas;
import com.struggle.common.security.param.CasParam;
import com.struggle.common.security.service.CasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Cas服务
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Tag(name = "Cas服务管理",description = "CasController")
@RestController
@RequestMapping("/api/security/cas")
public class CasController extends BaseController {
    @Resource
    private CasService casService;

    @AuthorityAnnotation("sys:cas:list:dd17c91ab3f14aac8006823a4eab13b8")
    @OperationLog
    @Operation(method="page",description="分页查询服务")
    @GetMapping("/page")
    public ApiResult<PageResult<Cas>> page(CasParam param) {
        // 使用关联查询
        return success(casService.pageRel(param));
    }

    @AuthorityAnnotation("sys:cas:list:dd17c91ab3f14aac8006823a4eab13b8")
    @OperationLog
    @Operation(method="list",description="查询全部Cas")
    @GetMapping()
    public ApiResult<List<Cas>> list(CasParam param) {
        // 使用关联查询
        return success(casService.listRel(param));
    }

    @AuthorityAnnotation("sys:cas:save:63feb064eea24feda2e1bdc36bc0e0c9")
    @OperationLog
    @Operation(method="save",description="添加Cas")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Cas cas) {
        ValidatorUtil._validBean(cas);
        if (casService.saveCas(cas)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:cas:update:07d0e575721f4dc69d64972e57932c3b")
    @OperationLog
    @Operation(method="update",description="修改Cas")
    @PostMapping(value = "/update")
    public ApiResult<?> update(@RequestBody Cas cas) {
        ValidatorUtil._validBean(cas);
        if (casService.updateCas(cas)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:cas:remove:becb660c31664810a0ed85e4e662fcda")
    @OperationLog
    @Operation(method="remove",description="删除Cas")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if (casService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
