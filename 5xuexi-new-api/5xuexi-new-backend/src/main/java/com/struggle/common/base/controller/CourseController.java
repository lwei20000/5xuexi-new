package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.entity.CourseAlias;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.service.CourseAliasService;
import com.struggle.common.base.service.CourseService;
import com.struggle.common.base.service.CoursewareService;
import com.struggle.common.base.service.MajorCourseService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程信息控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "课程信息管理",description = "CourseController")
@RestController
@RequestMapping("/api/base/course")
public class CourseController extends BaseController {
    @Resource
    private CourseService courseService;
    @Resource
    private CourseAliasService courseAliasService;
    @Resource
    private CoursewareService coursewareService;
    @Resource
    private MajorCourseService majorCourseService;

    @AuthorityAnnotation("base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="page",description="分页查询课程信息")
    @GetMapping("/page")
    public ApiResult<PageResult<Course>> page(CourseParam param) {
        if(param.isBySelf()){
            param.setTenantId(ThreadLocalContextHolder.getTenant());
        }
        // 使用关联查询
        return success(courseService.pageRel(param));
    }

    @AuthorityAnnotation("base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="list",description="查询全部课程信息")
    @GetMapping()
    public ApiResult<List<Course>> list(CourseParam param) {
        if(param.isBySelf()){
            param.setTenantId(ThreadLocalContextHolder.getTenant());
        }
        // 使用关联查询
        return success(courseService.listRel(param));
    }

    @AuthorityAnnotation(value = "base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="get",description="根据id查询课程信息")
    @GetMapping("/{id}")
    public ApiResult<Course> get(@PathVariable("id") Integer id) {
        // 使用关联查询
        return success(courseService.getByIdRel(id));
    }

    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @OperationLog
    @Operation(method="save",description="添加课程信息")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Course course) {
        if (courseService.count(new LambdaQueryWrapper<Course>().eq(Course::getCourseCode, course.getCourseCode()).eq(Course::getCourseName, course.getCourseName())) > 0) {
            return fail("课程代码、课程名称唯一，已存在");
        }
        if (courseService.save(course)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("base:course:update:9db31879a48e44c5a3c6cd802172418d")
    @OperationLog
    @Operation(method="updateAlias",description="修改课程别名信息")
    @PostMapping(value = "updateAlias")
    public ApiResult<?> updateAlias(@RequestBody CourseAlias courseAlias) {
        // 如果存在记录，则更新
        courseAliasService.saveOrUpdate(courseAlias, Wrappers.<CourseAlias>lambdaUpdate()
                .set(CourseAlias::getCourseCodeAlias,courseAlias.getCourseCodeAlias())
                .set(CourseAlias::getCourseNameAlias,courseAlias.getCourseNameAlias())
                .eq(CourseAlias::getTenantId,ThreadLocalContextHolder.getTenant())
                .eq(CourseAlias::getCourseId,courseAlias.getCourseId()));

        return success("修改成功");
    }

    @AuthorityAnnotation("base:course:update:9db31879a48e44c5a3c6cd802172418d")
    @OperationLog
    @Operation(method="update",description="修改课程信息")
    @PostMapping(value = "update")
    public ApiResult<?> update(@RequestBody Course course) {

        if (courseService.count(new LambdaQueryWrapper<Course>()
                .eq(Course::getCourseCode, course.getCourseCode())
                .eq(Course::getCourseName, course.getCourseName())
                .ne(Course::getCourseId, course.getCourseId())) > 0) {
            return fail("课程代码、课程名称唯一，已存在");
        }

        courseService.update(course, Wrappers.<Course>lambdaUpdate()
                .set(Course::getCoursePicture,course.getCoursePicture())
                .set(Course::getComments,course.getComments())
                .eq(Course::getCourseId,course.getCourseId()));

        return success("修改成功");
    }

    @AuthorityAnnotation("base:course:remove:a0a553c5733544d4b275d68506fe7b40")
    @OperationLog
    @Operation(method="remove",description="删除课程信息")
    @PostMapping("/{id}")
    @Transactional
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        ThreadLocalContextHolder.setCloseTenant(true);
        long count = majorCourseService.count(new LambdaQueryWrapper<MajorCourse>().eq(MajorCourse::getCourseId, id));
        ThreadLocalContextHolder.removeCloseTenant();
        if(count > 0){
            return fail("有专业已用到课程，不能删除");
        }
        if (courseService.removeById(id)) {
            //删除章节
            coursewareService.remove(new LambdaQueryWrapper<Courseware>().eq(Courseware::getCourseId, id));
            //删除别名表数据
            ThreadLocalContextHolder.setCloseTenant(true);
            courseAliasService.remove(new LambdaQueryWrapper<CourseAlias>().eq(CourseAlias::getCourseId, id));
            ThreadLocalContextHolder.removeCloseTenant();
            return success("删除成功");
        }
        return fail("删除失败");
    }

    @AuthorityAnnotation("base:course:remove:a0a553c5733544d4b275d68506fe7b40")
    @OperationLog
    @Operation(method="removeBatch",description="批量删除课程信息")
    @PostMapping("/batch")
    @Transactional
    public ApiResult<?> removeBatch(@RequestBody List<Integer> ids) {
        ThreadLocalContextHolder.setCloseTenant(true);
        long count = majorCourseService.count(new LambdaQueryWrapper<MajorCourse>().in(MajorCourse::getCourseId, ids));
        ThreadLocalContextHolder.removeCloseTenant();
        if(count > 0){
            return fail("有专业已用到课程，不能删除");
        }
        if (courseService.removeByIds(ids)) {
            //删除章节
            coursewareService.remove(new LambdaQueryWrapper<Courseware>().in(Courseware::getCourseId, ids));
            //删除别名表数据
            ThreadLocalContextHolder.setCloseTenant(true);
            courseAliasService.remove(new LambdaQueryWrapper<CourseAlias>().in(CourseAlias::getCourseId, ids));
            ThreadLocalContextHolder.removeCloseTenant();
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
