package com.struggle.common.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.MajorCourseParam;
import com.struggle.common.exam.entity.TMajorCourseExam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: TMajorCourseExam
 * @Description: 专业课程考试-Mapper层
 * @author xsk
 */
@Mapper
public interface TMajorCourseExamMapper extends BaseMapper<TMajorCourseExam> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<MajorCourse>
     */
    List<MajorCourse> selectPageRel(@Param("page") IPage<MajorCourse> page,
                                    @Param("param") MajorCourseParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<MajorCourse>
     */
    List<MajorCourse> selectListRel(@Param("param") MajorCourseParam param);

    /**
     * 校验时间重复
     * @return
     */
    int selectTotal(@Param("param")TMajorCourseExam majorCourseExam);
}