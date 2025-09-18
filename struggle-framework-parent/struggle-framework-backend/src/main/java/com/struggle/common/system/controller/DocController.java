package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.Doc;
import com.struggle.common.system.param.DocParam;
import com.struggle.common.system.service.DocService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文档控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@Tag(name = "文档管理",description = "DocController")
@RestController
@RequestMapping("/api/system/doc")
public class DocController extends BaseController {
    @Resource
    private DocService docService;

    @AuthorityAnnotation("sys:doc:save:43733cfe2cf94e3db1c953e1275e6bfa")
    @OperationLog
    @Operation(method = "save",description="添加文档")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Doc doc) {
        ValidatorUtil._validBean(doc);
        if (docService.saveDoc(doc)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("sys:doc:list:4be7267f6e9a47d0b1aa39e38c5815f7")
    @OperationLog
    @Operation(method = "list",description="查询全部文档")
    @GetMapping("/admin")
    public ApiResult<List<Doc>> list(DocParam param) {
        return success(docService.listRel(param));
    }

    @AuthorityAnnotation("sys:doc:update:ea93c40c0e32419c9e3b5201f357b988")
    @OperationLog
    @Operation(method = "update",description="修改文档")
    @PostMapping("/update")
    public ApiResult<?> update(@RequestBody Doc doc) {
        ValidatorUtil._validBean(doc);
        if (docService.updateDoc(doc)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("sys:doc:remove:ba89c990e23a4fe5adca033827705f23")
    @OperationLog
    @Operation(method = "remove",description="删除文档")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        if(docService.count(Wrappers.<Doc>lambdaQuery().eq(Doc::getParentId, id))>0){
            return fail("先删除子文档，再删除");
        }
        if (docService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("sys:doc:remove:ba89c990e23a4fe5adca033827705f23")
    @OperationLog
    @Operation(method = "removeBatch",description="批量删除文档")
    @PostMapping("/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        if(docService.count(Wrappers.<Doc>lambdaQuery().in(Doc::getParentId, ids))>0){
            return fail("先删除子文档，再删除");
        }
        if (docService.removeByIds(ids)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
