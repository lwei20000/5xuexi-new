package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.entity.SysSystemMessageRead;
import com.struggle.common.system.param.SysSystemMessageParam;
import com.struggle.common.system.param.SysSystemMessageReadParam;
import com.struggle.common.system.service.ISysSystemMessageReadService;
import com.struggle.common.system.service.ISysSystemMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysSystemMessageController
 * @Description: 系统消息-controller层
 * @author xsk
 */
@Tag(name = "系统消息",description = "SysSystemMessageController")
@RestController
@RequestMapping("/api/system/system_message")
public class SysSystemMessageController extends BaseController{
    @Resource
    private ISysSystemMessageService sysSystemMessageService;
    @Resource
    private ISysSystemMessageReadService sysSystemMessageReadService;

    @OperationLog
    @Operation(method = "admin_page",description="分页查询")
    @GetMapping("/admin/page")
    @AuthorityAnnotation("system:sysSystemMessage:list:253e30f789b44a282b06295b0d253e3")
    public ApiResult<PageResult<SysSystemMessage>> admin_page(SysSystemMessageParam param) {
        return success(sysSystemMessageService.pageRel(param));
    }

    @AuthorityAnnotation(value = "system:sysSystemMessage:list:253e30f789b44a282b06295b0d253e3")
    @OperationLog
    @Operation(method = "get",description="管理端根据id查询")
    @GetMapping("/detail/{id}")
    public ApiResult<SysSystemMessage> get(@PathVariable("id") Integer id) {

        return success(sysSystemMessageService.getDetailById(id));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysSystemMessage:save:a253e30f789b644a253e306295b0da253e3")
    public ApiResult<?> save(@RequestBody SysSystemMessage sysSystemMessage) {
        ValidatorUtil._validBean(sysSystemMessage);
        sysSystemMessageService.saveSystemMessage(sysSystemMessage);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysSystemMessage:update:a23e30f789b444aa23e306295b0da23e3")
    public ApiResult<?> update(@RequestBody SysSystemMessage sysSystemMessage) {
        ValidatorUtil._validBean(sysSystemMessage);
        sysSystemMessageService.updateSystemMessage(sysSystemMessage);
        return success();
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysSystemMessage:delete:a26e39b44a18a26e36295b0da26e3")
    public ApiResult<?> delete(@RequestBody  List<Integer> idList) {
        sysSystemMessageService.deleteSystemMessage(idList);
        return success();
    }

    @OperationLog
    @Operation(method = "read_page",description="分页查询用户")
    @GetMapping("/read_page")
    @AuthorityAnnotation("system:sysSystemMessage:list:253e30f789b44a282b06295b0d253e3")
    public ApiResult<PageResult<SysSystemMessageRead>> read_page(SysSystemMessageReadParam param) {
        return success(sysSystemMessageReadService.read_page(param));
    }
}