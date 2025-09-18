package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.entity.SysSystemAnnouncementRead;
import com.struggle.common.system.param.SysSystemAnnouncementParam;
import com.struggle.common.system.param.SysSystemAnnouncementReadParam;
import com.struggle.common.system.service.ISysSystemAnnouncementReadService;
import com.struggle.common.system.service.ISysSystemAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @ClassName: SysSystemAnnouncementController
 * @Description: 系统通知公告-controller层
 * @author xsk
 */
@Tag(name = "系统通知公告",description = "SysSystemAnnouncementController")
@RestController
@RequestMapping("/api/system/system_announcement")
public class SysSystemAnnouncementController extends BaseController{
    @Resource
    private ISysSystemAnnouncementService sysSystemAnnouncementService;
    @Resource
    private ISysSystemAnnouncementReadService sysSystemAnnouncementReadService;

    @OperationLog
    @Operation(method = "admin_page",description="管理端_分页查询")
    @GetMapping("/admin/page")
    @AuthorityAnnotation("system:sysSystemAnnouncement:list:253e130f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysSystemAnnouncement>> admin_page(SysSystemAnnouncementParam param) {
        return success(sysSystemAnnouncementService.pageRel(param));
    }

    @AuthorityAnnotation(value = "system:sysSystemAnnouncement:list:253e130f789b44a82b06295b0d253e3")
    @OperationLog
    @Operation(method = "get",description="管理端根据id查询")
    @GetMapping("/detail/{id}")
    public ApiResult<SysSystemAnnouncement> get(@PathVariable("id") Integer id) {

        return success(sysSystemAnnouncementService.getDetailById(id));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysSystemAnnouncement:save:a253e308f789b44a253e306295b0da253e3")
    public ApiResult<?> save(@RequestBody SysSystemAnnouncement sysSystemAnnouncement) {
        ValidatorUtil._validBean(sysSystemAnnouncement);
        sysSystemAnnouncementService.saveSystemAnnouncement(sysSystemAnnouncement);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysSystemAnnouncement:update:a23e30f789b44aa23e9306295b0da23e3")
    public ApiResult<?> update(@RequestBody SysSystemAnnouncement sysSystemAnnouncement) {
        ValidatorUtil._validBean(sysSystemAnnouncement);
        sysSystemAnnouncementService.updateSystemAnnouncement(sysSystemAnnouncement);
        return success();
    }

    @AuthorityAnnotation("system:sysSystemAnnouncement:update:a23e30f789b44aa23e9306295b0da23e3")
    @OperationLog
    @Operation(method = "updateTopTimestamp",description="置顶")
    @PostMapping("/topTimestamp")
    public ApiResult<?> updateTopTimestamp(@RequestBody SysSystemAnnouncement sysSystemAnnouncement) {
        if (sysSystemAnnouncement.getSystemAnnouncementId() == null || sysSystemAnnouncement.getTopTimestamp() == null || !Arrays.asList(0L, 1L).contains(sysSystemAnnouncement.getTopTimestamp())) {
            return fail("参数不正确");
        }
        Long topTimestamp = sysSystemAnnouncement.getTopTimestamp();
        if(topTimestamp.equals(1L)){
            topTimestamp = System.currentTimeMillis();
        }
        if (sysSystemAnnouncementService.update(new LambdaUpdateWrapper<SysSystemAnnouncement>().eq(SysSystemAnnouncement::getSystemAnnouncementId, sysSystemAnnouncement.getSystemAnnouncementId()).set(SysSystemAnnouncement::getTopTimestamp, topTimestamp))) {
            return success("置顶成功");
        }
        return fail("置顶失败");
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysSystemAnnouncement:delete:a26e39b443a8a26e36295b0da26e3")
    public ApiResult<?> delete(@RequestBody  List<Integer> idList) {
        sysSystemAnnouncementService.deleteSystemAnnouncement(idList);
        return success();
    }

    @OperationLog
    @Operation(method = "read_page",description="分页查询用户")
    @GetMapping(value = "/read_page")
    @AuthorityAnnotation("system:sysSystemAnnouncement:list:253e130f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysSystemAnnouncementRead>> read_page(SysSystemAnnouncementReadParam param) {
        return success(sysSystemAnnouncementReadService.read_page(param));
    }
}