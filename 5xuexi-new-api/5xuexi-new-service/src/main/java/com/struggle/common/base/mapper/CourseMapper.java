package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.param.CourseParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程信息Mapper
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface CourseMapper extends BaseMapper<Course> {

    /**
     * 分页查询 - 自己的和上级租户的
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Course>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Course> selectPageRel(@Param("page") IPage<Course> page,
                             @Param("param") CourseParam param);

    /**
     * 查询全部 - 自己的和上级租户的
     *
     * @param param 查询参数
     * @return List<User>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Course> selectListRel(@Param("param") CourseParam param);

}
