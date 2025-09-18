package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
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
@Tag(name = "COM意见反馈")
@RestController
@RequestMapping(value = "/api/system/sysFeedback")
public class ComFeedbackController extends BaseController{
    @Resource
    private ISysFeedbackService sysFeedbackService;

    @OperationLog
    @Operation(method="user_page",description="用户端分页查询")
    @GetMapping(value = "/user_page")
    public ApiResult<PageResult<SysFeedback>> user_page(SysFeedbackParam param) {
        param.setUserId(ThreadLocalContextHolder.getUserId());
        return success(sysFeedbackService.pageRel(param,false));
    }

    @OperationLog
    @Operation(method="user_save",description="用户端新增")
    @PostMapping(value = "/user_save")
    public ApiResult<?> user_save(@RequestBody SysFeedback sysFeedback) {
        sysFeedback.setUserId(ThreadLocalContextHolder.getUserId());
        sysFeedback.setStatus(0);
        sysFeedback.setTime(new Date());
        sysFeedbackService.save(sysFeedback);
        return success();
    }

    @OperationLog
    @Operation(method="user_update",description="用户端编辑")
    @PostMapping( "/user_update")
    public ApiResult<?> user_update(@RequestBody SysFeedback sysFeedback) {
        sysFeedbackService.update(null,new LambdaUpdateWrapper<SysFeedback>()
                        .set(SysFeedback::getTitle,sysFeedback.getTitle())
                        .set(SysFeedback::getPictures,sysFeedback.getPictures())
                        .set(SysFeedback::getComments,sysFeedback.getComments())
                        .set(SysFeedback::getPhone,sysFeedback.getPhone())
                        .set(SysFeedback::getStatus,0)
                        .eq(SysFeedback::getId,sysFeedback.getId())
                        .eq(SysFeedback::getUserId,ThreadLocalContextHolder.getUserId()));
        return success();
    }

    @OperationLog
    @Operation(method="user_update_status",description="用户端更新状态")
    @GetMapping( "/user_update_status")
    public ApiResult<?> user_update_status(@RequestParam(value = "id") Integer id,@RequestParam(value = "status") Integer status) {
        if(id == null){
            return fail("ID不能为空");
        }
        if(status == null){
            return fail("状态不能为空");
        }
        if(status != 0 && status != 2){
            return fail("状态不正确");
        }

        sysFeedbackService.update(null,new LambdaUpdateWrapper<SysFeedback>()
                .set(SysFeedback::getStatus,status)
                .eq(SysFeedback::getId,id)
                .eq(SysFeedback::getUserId,ThreadLocalContextHolder.getUserId()));
        return success();
    }

    @OperationLog
    @Operation(method="user_batch_remove",description= "用户端删除")
    @PostMapping("/user_batch")
    public ApiResult<?> user_batch_remove(@RequestBody List<Integer> idList) {
        sysFeedbackService.remove(new LambdaUpdateWrapper<SysFeedback>()
                .eq(SysFeedback::getUserId,ThreadLocalContextHolder.getUserId())
                .in(SysFeedback::getId,idList));
        return success();
    }
}