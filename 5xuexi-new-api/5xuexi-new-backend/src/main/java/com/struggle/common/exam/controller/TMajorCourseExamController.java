package com.struggle.common.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.entity.TUserMajorCourseExam;
import com.struggle.common.exam.param.TMajorCourseExamParam;
import com.struggle.common.exam.service.ITMajorCourseExamService;
import com.struggle.common.exam.service.ITUserMajorCourseExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 * @ClassName: TMajorCourseExamController
 * @Description: 专业课程考试-controller层
 * @author xsk
 */
@Tag(name= "专业课程考试", description = "TMajorCourseExamController")
@RestController
@RequestMapping(value = "/api/exam/tMajorCourseExam")
public class TMajorCourseExamController extends BaseController{

    @Resource
    private ITMajorCourseExamService tMajorCourseExamService;

    @Resource
    private ITUserMajorCourseExamService tUserMajorCourseExamService;

    @AuthorityAnnotation("exam:tMajorCourseExam:list:4160f78916b44a816b06245b0d16")
    @OperationLog
    @Operation(method="page",description="分页查询专业与课程关系")
    @GetMapping("/major_course_page")
    public ApiResult<PageResult<MajorCourse>> page(MajorCourseParam param) {
        // 使用关联查询
        return success(tMajorCourseExamService.pageRel(param));
    }

    @OperationLog
    @Operation(method="batch_save",description="批量发布")
    @PostMapping(value = "/batch_save")
    @AuthorityAnnotation("exam:tMajorCourseExam:save:160f478169b44160629165b40d16")
    public ApiResult<?> batch_save(@RequestBody  MajorCourseParam param, HttpServletRequest request,HttpServletResponse response) {
        if(!StringUtils.hasText(param.getAcademicYear())){
            throw new BusinessException("批量发布时，学年必需选择一个");
        }
        tMajorCourseExamService.batch_save(param);
        return success();
    }

    @OperationLog
    @Operation(method="pageRel",description="分页查询")
    @GetMapping(value = "/page")
    @AuthorityAnnotation("exam:tMajorCourseExam:list:4160f78916b44a816b06245b0d16")
    public ApiResult<PageResult<TMajorCourseExam>> pageRel(TMajorCourseExamParam param) {

        return success(tMajorCourseExamService.pageRel(param));
    }

    @OperationLog
    @Operation(method="save",description="新增")
    @PostMapping(value = "/save")
    @AuthorityAnnotation("exam:tMajorCourseExam:save:160f478169b44160629165b40d16")
    public ApiResult<?> save(@RequestBody TMajorCourseExam tMajorCourseExam, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(tMajorCourseExam);
        int i = tMajorCourseExamService.selectTotal(tMajorCourseExam);
        if(i>0){
            return fail("考试时间与其他发布有冲突");
        }
        tMajorCourseExamService.save(tMajorCourseExam);
        return success();
    }

    @OperationLog
    @Operation(method="update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("exam:tMajorCourseExam:update:160f7849b44a160624495b40d16")
    public ApiResult<?> update(@RequestBody TMajorCourseExam tMajorCourseExam, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(tMajorCourseExam);
        TMajorCourseExam byId = tMajorCourseExamService.getById(tMajorCourseExam.getMajorCourseExamId());
        if(byId == null){
            return fail("数据已被删除");
        }
        //如果修改了试卷，需要判断是否有学生已经作答
        if(!byId.getPaperId().equals(tMajorCourseExam.getPaperId())){
            long count = tUserMajorCourseExamService.count(new LambdaQueryWrapper<TUserMajorCourseExam>()
                    .eq(TUserMajorCourseExam::getMajorCourseExamId, tMajorCourseExam.getMajorCourseExamId()));
            if(count>0){
                return fail("已有学生已考试，不能编辑");
            }
        }
        int i = tMajorCourseExamService.selectTotal(tMajorCourseExam);
        if(i>0){
            return fail("考试时间与其他发布有冲突");
        }
        tMajorCourseExamService.updateById(tMajorCourseExam);
        return success();
    }

    @OperationLog
    @Operation(method="delete",description = "删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("exam:tMajorCourseExam:remove:49b4164a8462169165b0d4")
    public ApiResult<?> delete(@RequestBody List<Integer> idList) {

        long count = tUserMajorCourseExamService.count(new LambdaQueryWrapper<TUserMajorCourseExam>()
                .in(TUserMajorCourseExam::getMajorCourseExamId, idList));
        if(count>0){
            return fail("已有学生已考试，不能删除");
        }
        return success(tMajorCourseExamService.removeBatchByIds(idList));
    }

    @OperationLog
    @Operation(method="detail",description= "查看")
    @GetMapping(value = "/{id}")
    @AuthorityAnnotation("exam:tMajorCourseExam:list:4160f78916b44a816b06245b0d16")
    public ApiResult<TMajorCourseExam> detail(HttpServletRequest request, HttpServletResponse response,@PathVariable("id") Integer id) {
        return success( tMajorCourseExamService.getById(id));
    }

    @OperationLog
    @Operation(method="list",description= "获取集合")
    @PostMapping(value = "/list")
    @AuthorityAnnotation("exam:tMajorCourseExam:list:4160f78916b44a816b06245b0d16")
    public ApiResult<List<TMajorCourseExam>> list(TMajorCourseExamParam param,HttpServletRequest request, HttpServletResponse response) {
        PageParam<TMajorCourseExam, TMajorCourseExamParam> page = new PageParam<>(param);
        return success( tMajorCourseExamService.list(page.getWrapper()));
    }
}