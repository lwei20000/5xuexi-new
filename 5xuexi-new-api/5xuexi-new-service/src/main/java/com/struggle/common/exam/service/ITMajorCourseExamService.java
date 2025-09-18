package com.struggle.common.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.exam.entity.TMajorCourseExam;
import com.struggle.common.exam.param.TMajorCourseExamParam;

/**
 *
 * @ClassName: TMajorCourseExamService
 * @Description: 专业课程考试-Service层
 * @author xsk
 */
public interface ITMajorCourseExamService extends IService<TMajorCourseExam>{

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<MajorCourse>
     */
    PageResult<MajorCourse> pageRel(MajorCourseParam param);

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<TMajorCourseExam>
     */
    PageResult<TMajorCourseExam> pageRel(TMajorCourseExamParam param);

    void batch_save(MajorCourseParam param);

    int selectTotal(TMajorCourseExam tMajorCourseExam);
}