package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.entity.UserLearning;
import com.struggle.common.base.param.CopyMajorCourseParam;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.base.service.MajorCourseService;
import com.struggle.common.base.service.UserCourseService;
import com.struggle.common.base.service.UserLearningService;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 专业与课程关系控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "专业与课程关系管理",description = "MajorCourseController")
@RestController
@RequestMapping("/api/base/major-course")
public class MajorCourseController extends BaseController {
    @Resource
    private MajorCourseService majorCourseService;
    @Resource
    private UserCourseService userCourseService;
    @Resource
    private UserLearningService userLearningService;

    @AuthorityAnnotation("base:majorCourse:list:0240c21534f14c1dac3c5883d7ef21b6")
    @OperationLog
    @Operation(method="page",description="分页查询专业与课程关系")
    @GetMapping("/page")
    public ApiResult<PageResult<MajorCourse>> page(MajorCourseParam param) {
        PageParam<MajorCourse, MajorCourseParam> page = new PageParam<>(param);
        // 使用关联查询
        return success(majorCourseService.pageRel(param));
    }

    @AuthorityAnnotation("base:majorCourse:list:0240c21534f14c1dac3c5883d7ef21b6")
    @OperationLog
    @Operation(method="list",description="查询全部专业与课程关系")
    @GetMapping()
    public ApiResult<List<MajorCourse>> list(MajorCourseParam param) {
        // 使用关联查询
        return success(majorCourseService.listRel(param));
    }

    @AuthorityAnnotation("base:majorCourse:list:0240c21534f14c1dac3c5883d7ef21b6")
    @OperationLog
    @Operation(method="get",description="根据id查询专业与课程关系")
    @GetMapping("/{id}")
    public ApiResult<MajorCourse> get(@PathVariable("id") Integer id) {
        // 使用关联查询
        return success(majorCourseService.getByIdRel(id));
    }

    @AuthorityAnnotation("base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1")
    @OperationLog
    @Operation(method="save",description="添加专业与课程关系")
    @PostMapping()
    public ApiResult<?> save(@RequestBody MajorCourse majorCourse) {

        if (majorCourseService.saveMajorCourse(majorCourse)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1")
    @OperationLog
    @Operation(method="copy",description="复制专业与课程")
    @PostMapping("/copy")
    public ApiResult<?> copy(@RequestBody CopyMajorCourseParam majorCourse) {
        if (majorCourseService.copy(majorCourse)) {
            return success("复制成功");
        }
        return fail("复制失败");
    }

    @AuthorityAnnotation("base:majorCourse:update:773ed130545045bdb8dd0ab55cc2e141")
    @OperationLog
    @Operation(method="update",description="修改专业与课程关系")
    @PostMapping(value = "update")
    public ApiResult<?> update(@RequestBody MajorCourse majorCourse) {
        if (majorCourseService.updateMajorCourse(majorCourse)) {
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("base:majorCourse:remove:deb745cf0f484e159bf932bdf14c4dcf")
    @OperationLog
    @Operation(method="remove",description="删除专业与课程关系")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        MajorCourse byId = majorCourseService.getById(id);
        if(byId == null){
            return fail("删除成功");
        }

        if (majorCourseService.removeById(id)) {
            //删除用户课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().eq(UserCourse::getMajorId,byId.getMajorId()).eq(UserCourse::getCourseId,byId.getCourseId()));
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().eq(UserLearning::getMajorId, byId.getMajorId()).eq(UserLearning::getCourseId,byId.getCourseId()));

            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("base:majorCourse:remove:deb745cf0f484e159bf932bdf14c4dcf")
    @OperationLog
    @Operation(method="removeBatch",description="批量删除专业与课程关系")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        List<MajorCourse> byIds = majorCourseService.listByIds(ids);
        if(CollectionUtils.isEmpty(byIds)){
            return fail("删除成功");
        }
        List<Integer> courseIds = new ArrayList<>();
        List<Integer> majorIds = new ArrayList<>();
        for(MajorCourse majorCourse:byIds){
            courseIds.add(majorCourse.getCourseId());
            majorIds.add(majorCourse.getMajorId());
        }

        if (majorCourseService.removeByIds(ids)) {
            //删除用户课程
            userCourseService.remove(new LambdaQueryWrapper<UserCourse>().in(UserCourse::getMajorId,majorIds).in(UserCourse::getCourseId,courseIds));
            //删除用户学习记录
            userLearningService.remove(new LambdaQueryWrapper<UserLearning>().in(UserLearning::getMajorId, majorIds).in(UserLearning::getCourseId,courseIds));

            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1")
    @Operation(method="importBatch",description="当前租户_导入")
    @PostMapping("/import")
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        return majorCourseService.importBatch(file,request,response);
    }

    @Operation(method="templateExport",description="模板导出")
    @AuthorityAnnotation("base:majorCourse:save:0d00f441d8a941f396ed93c064e31eb1")
    @RequestMapping(method = RequestMethod.GET, value = "/templateExport")
    public ApiResult<?> templateExport(HttpServletRequest request, HttpServletResponse response) {
        majorCourseService.templateExport(response);
        return null;
    }

}
