package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.entity.UserMajor;
import com.struggle.common.base.param.CopyMajorParam;
import com.struggle.common.base.param.MajorParam;
import com.struggle.common.base.service.MajorService;
import com.struggle.common.base.service.UserMajorService;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 招生专业控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "招生专业管理",description = "MajorController")
@RestController
@RequestMapping("/api/base/major")
public class MajorController extends BaseController {
    @Resource
    private MajorService majorService;
    @Resource
    private UserMajorService userMajorService;

    @AuthorityAnnotation("base:major:list:00724f16d2b645059d30b28b6235fb67")
    @OperationLog
    @Operation(method="page",description="分页查询招生专业")
    @GetMapping("/page")
    public ApiResult<PageResult<Major>> page(MajorParam param) {
        // 使用关联查询
        return success(majorService.pageRel(param));
    }

    @AuthorityAnnotation("base:major:list:00724f16d2b645059d30b28b6235fb67")
    @OperationLog
    @Operation(method="list",description="查询全部招生专业")
    @GetMapping()
    public ApiResult<List<Major>> list(MajorParam param) {
        PageParam<Major, MajorParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return success(majorService.list(page.getOrderWrapper()));
    }

    @AuthorityAnnotation("base:major:list:00724f16d2b645059d30b28b6235fb67")
    @OperationLog
    @Operation(method="get",description="根据id查询招生专业")
    @GetMapping("/{id}")
    public ApiResult<Major> get(@PathVariable("id") Integer id) {
        return success(majorService.getById(id));
    }

    @AuthorityAnnotation("base:major:save:d6f326b8cbe24752a91e1f73e6cf1057")
    @OperationLog
    @Operation(method="save",description="添加招生专业")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Major major) {
        if (majorService.count(new LambdaQueryWrapper<Major>()
                .eq(Major::getMajorYear, major.getMajorYear())
                .eq(Major::getMajorCode, major.getMajorCode())
                .eq(Major::getMajorName, major.getMajorName())
                .eq(Major::getMajorType, major.getMajorType())
                .eq(Major::getMajorGradation, major.getMajorGradation())
                .eq(Major::getMajorForms, major.getMajorForms())
                .eq(Major::getMajorLength, major.getMajorLength())) > 0) {
            return fail("年份、代码、名称、类型、层次、学习形式、学制唯一，已存在");
        }
        if (majorService.save(major)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("base:major:save:d6f326b8cbe24752a91e1f73e6cf1057")
    @OperationLog
    @Operation(method="copy",description="复制招生专业")
    @PostMapping("/copy")
    public ApiResult<?> copy(@RequestBody CopyMajorParam major) {
        if (majorService.copy(major)) {
            return success("复制成功");
        }
        return fail("复制失败");
    }

    @AuthorityAnnotation("base:major:update:3139f76d-6294441b9bb3b9d771721ccc")
    @OperationLog
    @Operation(method="update",description="修改招生专业")
    @PostMapping(value = "update")
    public ApiResult<?> update(@RequestBody Major major) {

        long count = userMajorService.count(new LambdaQueryWrapper<UserMajor>().eq(UserMajor::getMajorId, major.getMajorId()));
        if(count>0){
            return fail("有用户已用到专业，不能修改，");
        }
        if (majorService.count(new LambdaQueryWrapper<Major>()
                .eq(Major::getMajorYear, major.getMajorYear())
                .eq(Major::getMajorCode, major.getMajorCode())
                .eq(Major::getMajorName, major.getMajorName())
                .eq(Major::getMajorType, major.getMajorType())
                .eq(Major::getMajorGradation, major.getMajorGradation())
                .eq(Major::getMajorForms, major.getMajorForms())
                .eq(Major::getMajorLength, major.getMajorLength())
                .ne(Major::getMajorId, major.getMajorId())) > 0) {
            return fail("年份、代码、名称、类型、层次、学习形式、学制唯一，已存在");
        }

        major.setUpdateTime(null);
        if (majorService.update(major, Wrappers.<Major>lambdaUpdate()
                .set(Major::getMajorPicture,major.getMajorPicture())
                .set(Major::getComments,major.getComments())
                .eq(Major::getMajorId,major.getMajorId()))) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("base:major:remove:889a7080e0ab45fbb35847cde5d7ac53")
    @OperationLog
    @Operation(method="remove",description="删除招生专业")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        long count = userMajorService.count(new LambdaQueryWrapper<UserMajor>().eq(UserMajor::getMajorId, id));
        if(count>0){
            return fail("有用户已用到专业，不能删除，");
        }
        if (majorService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("base:major:remove:889a7080e0ab45fbb35847cde5d7ac53")
    @OperationLog
    @Operation(method="removeBatch",description="批量删除招生专业")
    @PostMapping("/batch")
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {

        long count = userMajorService.count(new LambdaQueryWrapper<UserMajor>().in(UserMajor::getMajorId, ids));
        if(count>0){
            return fail("有用户已用到专业，不能删除，");
        }

        if (majorService.removeByIds(ids)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("base:major:save:d6f326b8cbe24752a91e1f73e6cf1057")
    @Operation(method="importBatch",description="当前租户_导入")
    @PostMapping("/import")
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {

        return majorService.importBatch(file,request,response);
    }

    @Operation(method="templateExport",description="模板导出")
    @AuthorityAnnotation("base:major:save:d6f326b8cbe24752a91e1f73e6cf1057")
    @RequestMapping(method = RequestMethod.GET, value = "/templateExport")
    public ApiResult<?> templateExport(HttpServletRequest request, HttpServletResponse response) {
        majorService.templateExport(response);
        return null;
    }

}
