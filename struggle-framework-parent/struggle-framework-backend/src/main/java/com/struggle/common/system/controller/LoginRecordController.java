package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.LoginRecord;
import com.struggle.common.system.param.LoginRecordParam;
import com.struggle.common.system.service.LoginRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 登录日志控制器
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:31
 */
@Tag(name = "登录日志",description = "LoginRecordController")
@RestController
@RequestMapping("/api/system/login-record")
public class LoginRecordController extends BaseController {
    @Resource
    private LoginRecordService loginRecordService;

    @AuthorityAnnotation("sys:login-record:list:0ce18362fb7e4fa19c63d51f6b64480f")
    @OperationLog
    @Operation(method = "page",description="分页查询登录日志")
    @GetMapping("/page")
    public ApiResult<PageResult<LoginRecord>> page(LoginRecordParam param) {
        return success(loginRecordService.pageRel(param));
    }

    @AuthorityAnnotation("sys:login-record:list:0ce18362fb7e4fa19c63d51f6b64480f")
    @OperationLog
    @Operation(method = "list",description="查询全部登录日志")
    @GetMapping()
    public ApiResult<List<LoginRecord>> list(LoginRecordParam param) {
        return success(loginRecordService.listRel(param));
    }

    @AuthorityAnnotation("sys:login-record:list:0ce18362fb7e4fa19c63d51f6b64480f")
    @OperationLog
    @Operation(method = "get",description="根据id查询登录日志")
    @GetMapping("/{id}")
    public ApiResult<LoginRecord> get(@PathVariable("id") Integer id) {
        return success(loginRecordService.getByIdRel(id));
    }

    @Operation(method = "dataExport",description="数据导出")
    @AuthorityAnnotation("sys:login-record:list:0ce18362fb7e4fa19c63d51f6b64480f")
    @GetMapping("/dataExport")
    public ApiResult<?> dataExport(HttpServletResponse response, LoginRecordParam param) {
        loginRecordService.dataExport(response,param);
        return null;
    }
}
