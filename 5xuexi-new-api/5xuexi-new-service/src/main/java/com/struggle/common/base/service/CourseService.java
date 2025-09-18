package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.core.web.PageResult;

import java.util.List;

/**
 * 课程信息Service
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface CourseService extends IService<Course> {

    /**
     * 分页关联查询（自己的和父级的）
     *
     * @param param 查询参数
     * @return PageResult<Course>
     */
    PageResult<Course> pageRel(CourseParam param);

    /**
     * 关联查询全部（自己的和父级的）
     *
     * @param param 查询参数
     * @return List<Course>
     */
    List<Course> listRel(CourseParam param);

    /**
     * 根据id查询（自己的和父级的）
     *
     * @param courseId 课程id
     * @return Course
     */
    Course getByIdRel(Integer courseId);

}
