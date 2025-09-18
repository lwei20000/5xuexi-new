package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.param.CoursewareParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 章节课时信息Mapper
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface CoursewareMapper extends BaseMapper<Courseware> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Courseware>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Courseware> selectPageRel(@Param("page") IPage<Courseware> page,
                             @Param("param") CoursewareParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Courseware> selectListRel(@Param("param") CoursewareParam param);

}
