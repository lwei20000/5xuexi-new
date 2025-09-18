package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.param.SysMessageParam;
import com.struggle.common.system.param.SysMessageReadParam;
import com.struggle.common.system.service.ISysMessageReadService;
import com.struggle.common.system.service.ISysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysMessageController
 * @Description: 消息-controller层
 * @author xsk
 */
@Tag(name = "消息",description = "SysMessageController")
@RestController
@RequestMapping("/api/system/message")
public class SysMessageController extends BaseController{
    @Resource
    private ISysMessageService sysMessageService;
    @Resource
    private ISysMessageReadService sysMessageReadService;

    @OperationLog
    @Operation(method = "admin_page",description="分页查询")
    @GetMapping("/admin/page")
    @AuthorityAnnotation("system:sysMessage:list:253e30f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysMessage>> admin_page(SysMessageParam param) {
        return success(sysMessageService.pageRel(param));
    }

    @AuthorityAnnotation(value = "system:sysMessage:list:253e30f789b44a82b06295b0d253e3")
    @OperationLog
    @Operation(method = "get",description="管理端根据id查询")
    @GetMapping("/detail/{id}")
    public ApiResult<SysMessage> get(@PathVariable("id") Integer id) {

        return success(sysMessageService.getDetailById(id));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysMessage:save:a253e30f789b44a253e306295b0da253e3")
    public ApiResult<?> save(@RequestBody SysMessage sysMessage) {
        ValidatorUtil._validBean(sysMessage);
        sysMessageService.saveMessage(sysMessage);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysMessage:update:a23e30f789b44aa23e306295b0da23e3")
    public ApiResult<?> update(@RequestBody SysMessage sysMessage) {
        ValidatorUtil._validBean(sysMessage);
        sysMessageService.updateMessage(sysMessage);
        return success();
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysMessage:delete:a26e39b44a8a26e36295b0da26e3")
    public ApiResult<?> delete(@RequestBody  List<Integer> idList) {
        sysMessageService.deleteMessage(idList);
        return success();
    }

    @OperationLog
    @Operation(method = "read_page",description="分页查询用户")
    @GetMapping("/read_page")
    @AuthorityAnnotation("system:sysMessage:list:253e30f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysMessageRead>> read_page(SysMessageReadParam param) {
        return success(sysMessageReadService.read_page(param));
    }
}