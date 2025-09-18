package com.struggle.common.security.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.security.entity.Cas;
import com.struggle.common.security.param.CasParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户Mapper
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@DS("system")
public interface CasMapper extends BaseMapper<Cas> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Tenant>
     */
    List<Cas> selectPageRel(@Param("page") IPage<Cas> page,
                             @Param("param") CasParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<Cas> selectListRel(@Param("param") CasParam param);

}
