package com.struggle.common.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.service.ITMajorCourseExamService;
import com.struggle.common.exam.service.ITPaperQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @ClassName: TPaperQuestionController
 * @Description: 课程试卷题目-controller层
 * @author xsk
 */
@Tag(name= "课程试卷题目", description = "TPaperQuestionController")
@RestController
@RequestMapping(value = "/api/exam/tPaperQuestion")
public class TPaperQuestionController extends BaseController{

    @Resource
    private ITPaperQuestionService tPaperQuestionService;

    @Resource
    private ITMajorCourseExamService majorCourseExamService;

    /**
     * excel导入
     */
    @AuthorityAnnotation("exam:tPaper:save:60f47869b446062965b40d6")
    @Operation(method="importBatch",description="导入")
    @PostMapping("/import")
    public ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse response,Integer paperId) {
        long count = majorCourseExamService.count(new LambdaQueryWrapper<TMajorCourseExam>().eq(TMajorCourseExam::getPaperId, paperId));
        if(count>0){
            return fail("已有专业课程用到该试卷，不能导入答题卡");
        }
        return tPaperQuestionService.importBatch(file,request,response,paperId);
    }

    @Operation(method="templateExport",description="模板导出")
    @AuthorityAnnotation("exam:tPaper:save:60f47869b446062965b40d6")
    @RequestMapping(method = RequestMethod.GET, value = "/templateExport")
    public ApiResult<?> templateExport(HttpServletRequest request, HttpServletResponse response) {
        tPaperQuestionService.templateExport(response);
        return null;
    }
}