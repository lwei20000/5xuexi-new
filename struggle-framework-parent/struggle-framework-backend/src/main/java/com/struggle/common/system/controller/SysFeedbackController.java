package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysFeedback;
import com.struggle.common.system.param.SysFeedbackParam;
import com.struggle.common.system.service.ISysFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


/**
 *
 * @ClassName: SysFeedbackController
 * @Description: 意见反馈-controller层
 * @author xsk
 */
@Tag(name = "意见反馈",description = "SysFeedbackController")
@RestController
@RequestMapping(value = "/api/system/sysFeedback")
public class SysFeedbackController extends BaseController{

    @Resource
    private ISysFeedbackService sysFeedbackService;

    @OperationLog
    @Operation(method = "page",description="管理端分页查询")
    @GetMapping(value = "/page")
    @AuthorityAnnotation("system:sysFeedback:list:6110f78911b44a811b06265b0d11")
    public ApiResult<PageResult<SysFeedback>> page(SysFeedbackParam param) {
        return success(sysFeedbackService.pageRel(param,true));
    }

    @OperationLog
    @Operation(method = "update",description="管理端编辑回复")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysFeedback:update:110f7869b44a110626695b60d11")
    public ApiResult<?> update(@RequestBody SysFeedback sysFeedback) {
        sysFeedbackService.update(null,new LambdaUpdateWrapper<SysFeedback>()
                .set(SysFeedback::getReplyPictures,sysFeedback.getReplyPictures())
                .set(SysFeedback::getReplyComments,sysFeedback.getReplyComments())
                .set(SysFeedback::getStatus,1)
                .set(SysFeedback::getReplyTime,new Date())
                .set(SysFeedback::getReplyUserId,ThreadLocalContextHolder.getUserId())
                .eq(SysFeedback::getId,sysFeedback.getId()));

        return success();
    }

    @OperationLog
    @Operation(method = "delete",description= "管理端删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysFeedback:remove:69b4114a8662119115b0d6")
    public ApiResult<?> delete(@RequestBody List<Integer> idList) {
       return success(sysFeedbackService.removeBatchByIds(idList));
    }
}