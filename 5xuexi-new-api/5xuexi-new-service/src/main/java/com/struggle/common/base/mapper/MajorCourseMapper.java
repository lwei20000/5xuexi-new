package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.MajorCourse;
import com.struggle.common.base.param.MajorCourseParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 专业与课程关系Mapper
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface MajorCourseMapper extends BaseMapper<MajorCourse> {

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
     * @return List<User>
     */
    List<MajorCourse> selectListRel(@Param("param") MajorCourseParam param);

    /**
     * 查询专业的最后一个课程的
     * @param majorIds
     * @return
     */
    List<Map<String,Object>> selectLastSemester(@Param("majorIds") List<Integer> majorIds);
}
