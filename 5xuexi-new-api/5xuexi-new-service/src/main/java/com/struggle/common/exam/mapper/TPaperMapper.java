package com.struggle.common.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.exam.entity.TPaper;
import com.struggle.common.exam.param.TPaperParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: TPaper
 * @Description: 试卷-Mapper层
 * @author xsk
 */
@Mapper
public interface TPaperMapper extends BaseMapper<TPaper> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<MajorCourse>
     */
    List<TPaper> pageCourseRel(@Param("page") IPage<TPaper> page,
                                    @Param("param") TPaperParam param);
}