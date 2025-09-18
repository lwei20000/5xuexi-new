package com.struggle.common.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.param.MajorParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 招生专业Mapper
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface MajorMapper extends BaseMapper<Major> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Major>
     */
    List<Major> selectPageRel(@Param("page") IPage<Major> page,
                             @Param("param") MajorParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<Major> selectListRel(@Param("param") MajorParam param);

}
