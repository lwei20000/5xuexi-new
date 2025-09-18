package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.OperationRecord;
import com.struggle.common.system.param.OperationRecordParam;
import com.struggle.common.system.service.OperationRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:12
 */
@Tag(name = "操作日志",description = "OperationRecordController")
@RestController
@RequestMapping("/api/system/operation-record")
public class OperationRecordController extends BaseController {

    @Resource
    private OperationRecordService operationRecordService;

    @AuthorityAnnotation("sys:operation-record:list:06348d58a04548fcab9a52340840ab38")
    @Operation(method = "page",description="分页查询操作日志")
    @GetMapping("/page")
    public ApiResult<PageResult<OperationRecord>> page(OperationRecordParam param) {
        return success(operationRecordService.pageRel(param));
    }

    @AuthorityAnnotation("sys:operation-record:list:06348d58a04548fcab9a52340840ab38")
    @Operation(method = "list",description="查询全部操作日志")
    @GetMapping()
    public ApiResult<List<OperationRecord>> list(OperationRecordParam param) {
        return success(operationRecordService.listRel(param));
    }

    @Operation(method = "dataExport",description="数据导出")
    @AuthorityAnnotation("sys:operation-record:list:06348d58a04548fcab9a52340840ab38")
    @GetMapping("/dataExport")
    public ApiResult<?> dataExport(HttpServletResponse response, OperationRecordParam param) {
        operationRecordService.dataExport(response,param);
        return null;
    }

    @AuthorityAnnotation("sys:operation-record:list:06348d58a04548fcab9a52340840ab38")
    @Operation(method = "get",description="根据id查询操作日志")
    @GetMapping("/{id}")
    public ApiResult<OperationRecord> get(@PathVariable("id") Integer id) {
        return success(operationRecordService.getByIdRel(id));
    }
}
