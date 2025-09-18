package com.struggle.common.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.entity.TPaper;
import com.struggle.common.exam.entity.TPaperQuestion;
import com.struggle.common.exam.param.TPaperParam;
import com.struggle.common.exam.service.ITMajorCourseExamService;
import com.struggle.common.exam.service.ITPaperQuestionService;
import com.struggle.common.exam.service.ITPaperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 *
 * @ClassName: TPaperController
 * @Description: 试卷-controller层
 * @author xsk
 */
@Tag(name= "试卷", description = "TPaperController")
@RestController
@RequestMapping(value = "/api/exam/tPaper")
public class TPaperController extends BaseController{

    @Resource
    private ITPaperService tPaperService;

    @Resource
    private ITMajorCourseExamService majorCourseExamService;

    @Resource
    private ITPaperQuestionService tPaperQuestionService;

    @OperationLog
    @Operation(method="course_page",description="分页查询专业课程")
    @GetMapping(value = "/course_page")
    @AuthorityAnnotation("exam:tPaper:list:460f7896b44a86b06245b0d6")
    public ApiResult<PageResult<TPaper>> course_page(HttpServletRequest request, HttpServletResponse response,TPaperParam param) {

        return success(tPaperService.pageCourseRel(param));
    }

    @OperationLog
    @Operation(method="page",description="分页查询")
    @GetMapping(value = "/page")
    @AuthorityAnnotation("exam:tPaper:list:460f7896b44a86b06245b0d6")
    public ApiResult<PageResult<TPaper>> page(HttpServletRequest request, HttpServletResponse response,TPaperParam param) {
        PageParam<TPaper, TPaperParam> page = new PageParam<>(param);
        return success(tPaperService.page(page, page.getWrapper()));
    }

    @OperationLog
    @Operation(method="save",description="新增")
    @PostMapping(value = "/save")
    @AuthorityAnnotation("exam:tPaper:save:60f47869b446062965b40d6")
    @Transactional
    public ApiResult<?> save(@RequestBody TPaper tPaper, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(tPaper);
        if(tPaper.getPaperUsage().equals(1)){
            tPaperService.update(new LambdaUpdateWrapper<TPaper>()
                    .eq(TPaper::getCourseId,tPaper.getCourseId())
                    .set(TPaper::getPaperUsage,0));
        }
        tPaperService.save(tPaper);
        return success();
    }

    @OperationLog
    @Operation(method="update",description="编辑")
    @PostMapping( "/update")
    @AuthorityAnnotation("exam:tPaper:update:60f7849b44a60624495b40d6")
    @Transactional
    public ApiResult<?> update(@RequestBody TPaper tPaper, HttpServletRequest request,HttpServletResponse response) {
        ValidatorUtil._validBean(tPaper);
        TPaper byId = tPaperService.getById(tPaper.getPaperId());
        if(byId == null){
            return fail("数据已经被删除了");
        }
        if((StringUtils.hasText(byId.getPaperFile()) && !byId.getPaperFile().equals(tPaper.getPaperFile())) ||(!StringUtils.hasText(byId.getPaperFile()) && StringUtils.hasText(tPaper.getPaperFile()))){
            long count = majorCourseExamService.count(new LambdaQueryWrapper<TMajorCourseExam>().eq(TMajorCourseExam::getPaperId, tPaper.getPaperId()));
            if(count>0){
                return fail("已有专业课程用到该试卷，不能修改试卷文件");
            }
        }

        if(tPaper.getPaperUsage().equals(1)){
            tPaperService.update(new LambdaUpdateWrapper<TPaper>()
                    .eq(TPaper::getCourseId,tPaper.getCourseId())
                    .set(TPaper::getPaperUsage,0));
        }
        tPaperService.update(tPaper,new LambdaUpdateWrapper<TPaper>()
                .set(TPaper::getPaperFile,tPaper.getPaperFile())
                .eq(TPaper::getPaperId,tPaper.getPaperId()));

        return success();
    }

    @OperationLog
    @Operation(method="delete",description= "删除")
    @PostMapping("/batch")
    @AuthorityAnnotation("exam:tPaper:remove:49b464a84626965b0d4")
    public ApiResult<?> delete(@RequestBody List<Integer> idList) {
        long count = majorCourseExamService.count(new LambdaQueryWrapper<TMajorCourseExam>().in(TMajorCourseExam::getPaperId, idList));
        if(count>0){
            return fail("已有专业课程用到该试卷");
        }
        //删除试卷
        tPaperService.removeBatchByIds(idList);
        //删除试卷题目
        tPaperQuestionService.remove(new LambdaQueryWrapper<TPaperQuestion>().in(TPaperQuestion::getPaperId,idList));
        return success();
    }

    @OperationLog
    @Operation(method="detail",description= "预览试卷")
    @GetMapping(value = "/{id}")
    @AuthorityAnnotation("exam:tPaper:list:460f7896b44a86b06245b0d6")
    public ApiResult<TPaper> detail(HttpServletRequest request, HttpServletResponse response,@PathVariable("id") Integer id) {
        TPaper byId = tPaperService.getById(id);
        if(byId != null){
            List<TPaperQuestion> list = tPaperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>().eq(TPaperQuestion::getPaperId, byId.getPaperId()));
            byId.setPaperQuestionList(list);
        }
        return success(byId);
    }

    @OperationLog
    @Operation(method="list",description= "获取集合")
    @PostMapping(value = "/list")
    @AuthorityAnnotation("exam:tPaper:list:460f7896b44a86b06245b0d6")
    public ApiResult<List<TPaper>> list(TPaperParam param,HttpServletRequest request, HttpServletResponse response) {
        PageParam<TPaper, TPaperParam> page = new PageParam<>(param);
        return success( tPaperService.list(page.getWrapper()));
    }
}