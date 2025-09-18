package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.param.CopyCoursewareParam;
import com.struggle.common.base.param.CoursewareParam;
import com.struggle.common.core.web.PageResult;

import java.util.List;

/**
 * 章节课时信息Service
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface CoursewareService extends IService<Courseware> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<Courseware>
     */
    PageResult<Courseware> pageRel(CoursewareParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Courseware>
     */
    List<Courseware> listRel(CoursewareParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Courseware>
     */
    boolean copy(CopyCoursewareParam param);

    /**
     * 根据id查询
     *
     * @param coursewareId 章节课时id
     * @return Courseware
     */
    Courseware getByIdRel(Integer coursewareId);

    /**
     * 查询子集
     * @param courseware
     * @param childrenIds
     */
    void getCoursewareChildrenIds(Courseware courseware, List<Integer> childrenIds);

}
