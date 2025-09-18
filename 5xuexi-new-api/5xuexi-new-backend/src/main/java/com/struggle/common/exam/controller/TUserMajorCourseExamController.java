package com.struggle.common.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.struggle.common.base.param.UserCourseParam;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.*;
import com.struggle.common.exam.param.TUserMajorCourseExamParam;
import com.struggle.common.exam.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;


/**
 *
 * @ClassName: TUserMajorCourseExamController
 * @Description: 用户专业考试记录-controller层
 * @author xsk
 */
@Tag(name= "用户专业考试记录", description = "TUserMajorCourseExamController")
@RestController
@RequestMapping(value = "/api/exam/tUserMajorCourseExam")
public class TUserMajorCourseExamController extends BaseController{

    @Resource
    private ITMajorCourseExamService tMajorCourseExamService;

    @Resource
    private ITUserMajorCourseExamService tUserMajorCourseExamService;

    @Resource
    private ITUserMajorCourseExamItemService tUserMajorCourseExamItemService;

    @Resource
    private ITPaperService tPaperService;

    @Resource
    private ITPaperQuestionService tPaperQuestionService;

    @OperationLog
    @Operation(method="page",description="分页查询")
    @GetMapping(value = "/page")
    @AuthorityAnnotation(value = "exam:tUserMajorCourseExam:list:4200f78920b44a820b06245b0d20",permission = true)
    public ApiResult<PageResult<TUserMajorCourseExam>> page(TUserMajorCourseExamParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        //增加机构范围
        param.setOrgIds(permission.getOrgIds());
        return success(tUserMajorCourseExamService.pageRel(param));
    }

    @OperationLog
    @Operation(method="save",description="批改")
    @PostMapping(value = "/save")
    @AuthorityAnnotation(value ="exam:tUserMajorCourseExam:save:200f478209b44200629205b40d20",permission = true)
    public ApiResult<?> save(@RequestBody TUserMajorCourseExam tUserMajorCourseExam) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        tUserMajorCourseExamService.correctPaper(tUserMajorCourseExam,permission.getOrgIds());
        return success();
    }

    @OperationLog
    @Operation(method="back",description="退回重答")
    @GetMapping(value = "/back/{id}")
    @AuthorityAnnotation(value ="exam:tUserMajorCourseExam:save:200f478209b44200629205b40d20",permission = true)
    public ApiResult<?> back(@PathVariable("id") Integer id) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        TUserMajorCourseExamParam param = new TUserMajorCourseExamParam();
        //增加机构范围
        param.setOrgIds(permission.getOrgIds());
        param.setId(id);
        List<TUserMajorCourseExam> tUserMajorCourseExams = tUserMajorCourseExamService.listRel(param,false);
        if(CollectionUtils.isEmpty(tUserMajorCourseExams)){
            return fail("无权限退回这份试卷作答",null);
        }

        TUserMajorCourseExam tUserMajorCourseExam = tUserMajorCourseExams.get(0);

        TMajorCourseExam majorCourseExam = tMajorCourseExamService.getOne(new LambdaQueryWrapper<TMajorCourseExam>()
                .select(TMajorCourseExam::getMajorCourseExamId,TMajorCourseExam::getStartTime,TMajorCourseExam::getEndTime)
                .eq(TMajorCourseExam::getMajorCourseExamId,tUserMajorCourseExam.getMajorCourseExamId()));
        if(majorCourseExam == null){
            return fail("专业课程考试已经不存在了",null);
        }

        if(majorCourseExam.getEndTime().before(new Date())){
            return fail("专业课程考试已经结束，不能退回重答",null);
        }

        //状态退回到未提交
        tUserMajorCourseExamService.update(null,new LambdaUpdateWrapper<TUserMajorCourseExam>()
                        .set(TUserMajorCourseExam::getStatus,0)
                .eq(TUserMajorCourseExam::getId,id));
        return success();
    }

    @OperationLog
    @Operation(method="detail",description="查看")
    @GetMapping(value = "/{id}")
    @AuthorityAnnotation(value ="exam:tUserMajorCourseExam:list:4200f78920b44a820b06245b0d20",permission = true)
    public ApiResult<TPaper> detail(@PathVariable("id") Integer id) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        TUserMajorCourseExamParam param = new TUserMajorCourseExamParam();
        //增加机构范围
        param.setOrgIds(permission.getOrgIds());
        param.setId(id);
        List<TUserMajorCourseExam> tUserMajorCourseExams = tUserMajorCourseExamService.listRel(param,false);
        if(CollectionUtils.isEmpty(tUserMajorCourseExams)){
            return fail("无权限查看这份试卷作答",null);
        }
        TUserMajorCourseExam tUserMajorCourseExam = tUserMajorCourseExams.get(0);

        TMajorCourseExam majorCourseExam = tMajorCourseExamService.getOne(new LambdaQueryWrapper<TMajorCourseExam>()
                        .select(TMajorCourseExam::getMajorCourseExamId,TMajorCourseExam::getStartTime,TMajorCourseExam::getEndTime)
                .eq(TMajorCourseExam::getMajorCourseExamId,tUserMajorCourseExam.getMajorCourseExamId()));
        if(majorCourseExam == null){
            return fail("专业课程考试已经不存在了",null);
        }

        //如果再考试时间内就可以退回重填
        if(majorCourseExam.getEndTime().after(new Date())){
            tUserMajorCourseExam.setBack(true);
        }

        List<TUserMajorCourseExamItem> list = tUserMajorCourseExamItemService.list(new LambdaQueryWrapper<TUserMajorCourseExamItem>()
                .select(TUserMajorCourseExamItem::getId,TUserMajorCourseExamItem::getPaperQuestionId,TUserMajorCourseExamItem::getAnswer,TUserMajorCourseExamItem::getScore)
                .eq(TUserMajorCourseExamItem::getMajorCourseExamId, tUserMajorCourseExam.getMajorCourseExamId())
                .eq(TUserMajorCourseExamItem::getUserId,tUserMajorCourseExam.getUserId()));

        tUserMajorCourseExam.setItemList(list);

         TPaper byId = tPaperService.getOne(new LambdaQueryWrapper<TPaper>().select(TPaper::getPaperName,TPaper::getPaperFile).eq(TPaper::getPaperId,tUserMajorCourseExam.getPaperId()));
        if(byId != null) {
            List<TPaperQuestion> qlist = tPaperQuestionService.list(new LambdaQueryWrapper<TPaperQuestion>()
                    .select(TPaperQuestion::getQuestionAnswer,TPaperQuestion::getQuestionAnalysis,TPaperQuestion::getPaperQuestionId, TPaperQuestion::getQuestionGroup, TPaperQuestion::getQuestionType, TPaperQuestion::getQuestionTitle, TPaperQuestion::getQuestionOptions, TPaperQuestion::getQuestionScore, TPaperQuestion::getQuestionSort)
                    .eq(TPaperQuestion::getPaperId, tUserMajorCourseExam.getPaperId()));
            byId.setPaperQuestionList(qlist);
            byId.setUserMajorCourseExam(tUserMajorCourseExam);
        }

        return success(byId);
    }

    @Operation(method="dataExport",description="数据导出")
    @AuthorityAnnotation(value="exam:tUserMajorCourseExam:list:4200f78920b44a820b06245b0d20",permission = true)
    @RequestMapping(method = RequestMethod.GET, value = "/dataExport")
    public ApiResult<?> dataExport(HttpServletRequest request, HttpServletResponse response, TUserMajorCourseExamParam param) {
        DataPermissionParam permission = ThreadLocalContextHolder.getPermission();
        param.setOrgIds(permission.getOrgIds());
        tUserMajorCourseExamService.dataExport(response,param);
        return null;
    }
}