package com.struggle.common.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.param.CopyCoursewareParam;
import com.struggle.common.base.param.CoursewareParam;
import com.struggle.common.base.service.CoursewareService;
import com.struggle.common.base.service.UserLearningService;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 章节课时信息控制器
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Tag(name= "章节课时信息管理",description = "CoursewareController")
@RestController
@RequestMapping("/api/base/courseware")
public class CoursewareController extends BaseController {
    @Resource
    private CoursewareService coursewareService;
    @Resource
    private UserLearningService userLearningService;

    @AuthorityAnnotation("base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="page",description="分页查询章节课时信息")
    @GetMapping("/page")
    public ApiResult<PageResult<Courseware>> page(CoursewareParam param) {
        // 使用关联查询
        return success(coursewareService.pageRel(param));
    }

    @AuthorityAnnotation("base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="list",description="查询全部章节课时信息")
    @GetMapping()
    public ApiResult<List<Courseware>> list(CoursewareParam param) {
        // 使用关联查询
        return success(coursewareService.listRel(param));
    }

    @AuthorityAnnotation("base:course:list:b1e68d0d1f054c898306295dc71a617e")
    @OperationLog
    @Operation(method="get",description="根据id查询章节课时信息")
    @GetMapping("/{id}")
    public ApiResult<Courseware> get(@PathVariable("id") Integer id) {
        // 使用关联查询
        return success(coursewareService.getByIdRel(id));
    }

    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @OperationLog
    @Operation(method="save",description="添加章节课时信息")
    @PostMapping()
    public ApiResult<?> save(@RequestBody Courseware courseware) {
        if (coursewareService.save(courseware)) {
            return success("添加成功");
        }
        return fail("添加失败");
    }

    @AuthorityAnnotation("base:course:save:02efa9f1b1854a9f9dd9d89fcc1867c8")
    @OperationLog
    @Operation(method="copy",description="复制章节课时")
    @PostMapping("/copy")
    public ApiResult<?> copy(@RequestBody CopyCoursewareParam param) {
        if (coursewareService.copy(param)) {
            return success("复制成功");
        }
        return fail("复制失败");
    }

    @AuthorityAnnotation("base:course:update:9db31879a48e44c5a3c6cd802172418d")
    @OperationLog
    @Operation(method="update",description="修改章节课时信息")
    @PostMapping(value = "update")
    public ApiResult<?> update(@RequestBody Courseware courseware) {
        if(courseware.getParentId() !=null && courseware.getParentId() !=0){
            if(courseware.getCoursewareId().equals(courseware.getParentId())){
                throw new BusinessException("上级章节课时不能是当前章节课时");
            }
            List<Integer> childrenIds = new ArrayList<>();
            coursewareService.getCoursewareChildrenIds(courseware,childrenIds);
            if(childrenIds.contains(courseware.getParentId())){
                throw new BusinessException("上级章节课时不能是当前章节课时的子章节课时");
            }
        }
        courseware.setUpdateTime(null);
        Courseware byId = coursewareService.getById(courseware.getCoursewareId());
        if (coursewareService.update(courseware, Wrappers.<Courseware>lambdaUpdate().set(Courseware::getParentId,courseware.getParentId()).eq(Courseware::getCoursewareId,courseware.getCoursewareId()))) {
            //时长不一致，刷新进度
            if(courseware.getDuration() !=null && !byId.getDuration().equals(courseware.getDuration())){
                userLearningService.updateLearning(courseware);
            }
            return success("修改成功");
        }
        return fail("修改失败");
    }

    @AuthorityAnnotation("base:course:remove:a0a553c5733544d4b275d68506fe7b40")
    @OperationLog
    @Operation(method="remove",description="删除章节课时信息")
    @PostMapping("/{id}")
    public ApiResult<?> remove(@PathVariable("id") Integer id) {
        long count = coursewareService.count(new LambdaQueryWrapper<Courseware>().eq(Courseware::getParentId, id));
        if(count>0){
            return success("先删除子章节课时后再删除");
        }
        if (coursewareService.removeById(id)) {
            return success("删除成功");
        }
        return fail("删除失败");
    }
}
