package com.struggle.common.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.exam.entity.TUserMajorCourseExam;
import com.struggle.common.exam.param.TUserMajorCourseExamParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: TUserMajorCourseExam
 * @Description: 用户专业考试记录-Mapper层
 * @author xsk
 */
@Mapper
public interface TUserMajorCourseExamMapper extends BaseMapper<TUserMajorCourseExam> {
    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<TUserMajorCourseExam>
     */
    List<TUserMajorCourseExam> selectPageRel(@Param("page") IPage<TUserMajorCourseExam> page,
                                    @Param("param") TUserMajorCourseExamParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<TUserMajorCourseExam>
     */
    List<TUserMajorCourseExam> selectListRel(@Param("param") TUserMajorCourseExamParam param);

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<TUserMajorCourseExam>
     */
    List<TUserMajorCourseExam> selectPageRelNotExam(@Param("page") IPage<TUserMajorCourseExam> page,
                                             @Param("param") TUserMajorCourseExamParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<TUserMajorCourseExam>
     */
    List<TUserMajorCourseExam> selectListRelNotExam(@Param("param") TUserMajorCourseExamParam param);

}