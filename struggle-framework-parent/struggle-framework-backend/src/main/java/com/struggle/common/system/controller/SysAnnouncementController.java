package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysAnnouncementRead;
import com.struggle.common.system.param.SysAnnouncementParam;
import com.struggle.common.system.param.SysAnnouncementReadParam;
import com.struggle.common.system.service.ISysAnnouncementReadService;
import com.struggle.common.system.service.ISysAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @ClassName: SysAnnouncementController
 * @Description: 通知公告-controller层
 * @author xsk
 */
@Tag(name = "通知公告",description = "SysAnnouncementController")
@RestController
@RequestMapping("/api/system/announcement")
public class SysAnnouncementController extends BaseController{
    @Resource
    private ISysAnnouncementService sysAnnouncementService;
    @Resource
    private ISysAnnouncementReadService sysAnnouncementReadService;

    @OperationLog
    @Operation(method = "admin_page",description="管理端_分页查询")
    @GetMapping("/admin/page")
    @AuthorityAnnotation("system:sysAnnouncement:list:253e30f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysAnnouncement>> admin_page(SysAnnouncementParam param) {
        return success(sysAnnouncementService.pageRel(param));
    }

    @AuthorityAnnotation(value = "system:sysAnnouncement:list:253e30f789b44a82b06295b0d253e3")
    @OperationLog
    @Operation(method = "get",description="管理端根据id查询")
    @GetMapping("/detail/{id}")
    public ApiResult<SysAnnouncement> get(@PathVariable("id") Integer id) {

        return success(sysAnnouncementService.getDetailById(id));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysAnnouncement:save:a253e30f789b44a253e306295b0da253e3")
    public ApiResult<?> save(@RequestBody SysAnnouncement sysAnnouncement) {
        ValidatorUtil._validBean(sysAnnouncement);
        sysAnnouncementService.saveAnnouncement(sysAnnouncement);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysAnnouncement:update:a23e30f789b44aa23e306295b0da23e3")
    public ApiResult<?> update(@RequestBody SysAnnouncement sysAnnouncement) {
        ValidatorUtil._validBean(sysAnnouncement);
        sysAnnouncementService.updateAnnouncement(sysAnnouncement);
        return success();
    }

    @AuthorityAnnotation("system:sysAnnouncement:update:a23e30f789b44aa23e306295b0da23e3")
    @OperationLog
    @Operation(method = "updateTopTimestamp",description="置顶")
    @PostMapping("/topTimestamp")
    public ApiResult<?> updateTopTimestamp(@RequestBody SysAnnouncement sysAnnouncement) {
        if (sysAnnouncement.getAnnouncementId() == null || sysAnnouncement.getTopTimestamp() == null || !Arrays.asList(0L, 1L).contains(sysAnnouncement.getTopTimestamp())) {
            return fail("参数不正确");
        }
        Long topTimestamp = sysAnnouncement.getTopTimestamp();
        if(topTimestamp.equals(1L)){
            topTimestamp = System.currentTimeMillis();
        }
        if (sysAnnouncementService.update(new LambdaUpdateWrapper<SysAnnouncement>().eq(SysAnnouncement::getAnnouncementId, sysAnnouncement.getAnnouncementId()).set(SysAnnouncement::getTopTimestamp, topTimestamp))) {
            return success("置顶成功");
        }
        return fail("置顶失败");
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysAnnouncement:delete:a26e39b44a8a26e36295b0da26e3")
    public ApiResult<?> delete(@RequestBody  List<Integer> idList) {
        sysAnnouncementService.deleteAnnouncement(idList);
        return success();
    }

    @OperationLog
    @Operation(method = "read_page",description="分页查询用户")
    @GetMapping(value = "/read_page")
    @AuthorityAnnotation("system:sysAnnouncement:list:253e30f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysAnnouncementRead>> read_page(SysAnnouncementReadParam param) {
        return success(sysAnnouncementReadService.read_page(param));
    }
}