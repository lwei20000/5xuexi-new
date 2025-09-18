package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.UserCourse;
import com.struggle.common.base.param.UserCourseParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCourseMapper extends BaseMapper<UserCourse> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<StudentMajor>
     */
    List<UserCourse> selectPageRel(@Param("page") IPage<UserCourse> page,
                                  @Param("param") UserCourseParam param);


    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<UserCourse> selectListRel(@Param("param") UserCourseParam param);

}
