package com.struggle.common.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.ScoreRule;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.entity.TUserMajorCourseExam;
import com.struggle.common.exam.param.TUserMajorCourseExamParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @ClassName: TUserMajorCourseExamService
 * @Description: 用户专业考试记录-Service层
 * @author xsk
 */
public interface ITUserMajorCourseExamService extends IService<TUserMajorCourseExam>{

    /**
     * 保存考试记录
     * @param param
     */
    void submitPaper(TUserMajorCourseExam param);
    /**
     * 批改试卷
     * @param param
     */
    void correctPaper(TUserMajorCourseExam param,List<Integer> orgs);
    /**
     * 分页查询
     * @param param
     * @return
     */
    PageResult<TUserMajorCourseExam>  pageRel(TUserMajorCourseExamParam param);

    /**
     * 查询list
     * @param param
     * @return
     */
    List<TUserMajorCourseExam> listRel(TUserMajorCourseExamParam param,boolean details);

    /**
     * 定时任务 提交试卷
     * @param records
     */
    void batchSubmitPaper(List<TMajorCourseExam> records, ScoreRule scoreRule);

    /**
     * 导出Excel数据
     */
    void dataExport(HttpServletResponse response, TUserMajorCourseExamParam param);
}