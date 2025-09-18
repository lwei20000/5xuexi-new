package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysOpenAnnouncement;
import com.struggle.common.system.param.SysOpenAnnouncementParam;
import com.struggle.common.system.service.ISysOpenAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @ClassName: SysOpenAnnouncementController
 * @Description: 公开通知公告-controller层
 * @author xsk
 */
@Tag(name = "公开通知公告",description = "SysOpenAnnouncementController")
@RestController
@RequestMapping("/api/system/open-announcement")
public class SysOpenAnnouncementController extends BaseController{
    @Resource
    private ISysOpenAnnouncementService sysOpenAnnouncementService;

    @OperationLog
    @Operation(method = "admin_page",description="管理端_分页查询")
    @GetMapping("/admin/page")
    @AuthorityAnnotation("system:sysOpenAnnouncement:list:253e30f789b44a82b06295b0d253e3")
    public ApiResult<PageResult<SysOpenAnnouncement>> admin_page(SysOpenAnnouncementParam param) {
        return success(sysOpenAnnouncementService.pageRel(param));
    }

    @AuthorityAnnotation(value = "system:sysOpenAnnouncement:list:253e30f789b44a82b06295b0d253e3")
    @OperationLog
    @Operation(method = "get",description="管理端根据id查询")
    @GetMapping("/detail/{id}")
    public ApiResult<SysOpenAnnouncement> get(@PathVariable("id") Integer id) {

        return success(sysOpenAnnouncementService.getDetailById(id));
    }

    @OperationLog
    @Operation(method = "save",description="新增")
    @PostMapping("/save")
    @AuthorityAnnotation("system:sysOpenAnnouncement:save:a253e30f789b44a253e306295b0da253e3")
    public ApiResult<?> save(@RequestBody SysOpenAnnouncement sysOpenAnnouncement) {
        ValidatorUtil._validBean(sysOpenAnnouncement);
        sysOpenAnnouncementService.saveAnnouncement(sysOpenAnnouncement);
        return success();
    }

    @OperationLog
    @Operation(method = "update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("system:sysOpenAnnouncement:update:a23e30f789b44aa23e306295b0da23e3")
    public ApiResult<?> update(@RequestBody SysOpenAnnouncement sysOpenAnnouncement) {
        ValidatorUtil._validBean(sysOpenAnnouncement);
        sysOpenAnnouncementService.updateAnnouncement(sysOpenAnnouncement);
        return success();
    }

    @AuthorityAnnotation("system:sysOpenAnnouncement:update:a23e30f789b44aa23e306295b0da23e3")
    @OperationLog
    @Operation(method = "updateTopTimestamp",description="置顶")
    @PostMapping("/topTimestamp")
    public ApiResult<?> updateTopTimestamp(@RequestBody SysOpenAnnouncement sysOpenAnnouncement) {
        if (sysOpenAnnouncement.getOpenAnnouncementId() == null || sysOpenAnnouncement.getTopTimestamp() == null || !Arrays.asList(0L, 1L).contains(sysOpenAnnouncement.getTopTimestamp())) {
            return fail("参数不正确");
        }
        Long topTimestamp = sysOpenAnnouncement.getTopTimestamp();
        if(topTimestamp.equals(1L)){
            topTimestamp = System.currentTimeMillis();
        }
        if (sysOpenAnnouncementService.update(new LambdaUpdateWrapper<SysOpenAnnouncement>().eq(SysOpenAnnouncement::getOpenAnnouncementId, sysOpenAnnouncement.getOpenAnnouncementId()).set(SysOpenAnnouncement::getTopTimestamp, topTimestamp))) {
            return success("置顶成功");
        }
        return fail("置顶失败");
    }

    @OperationLog
    @Operation(method = "delete",description="删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("system:sysOpenAnnouncement:delete:a26e39b44a8a26e36295b0da26e3")
    public ApiResult<?> delete(@RequestBody  List<Integer> idList) {
        sysOpenAnnouncementService.deleteAnnouncement(idList);
        return success();
    }
}