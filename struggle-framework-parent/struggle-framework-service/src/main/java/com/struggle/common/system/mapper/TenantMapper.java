package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.param.TenantParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 租户Mapper
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@DS("system")
public interface TenantMapper extends BaseMapper<Tenant> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Tenant>
     */
    List<Tenant> selectPageRel(@Param("page") IPage<Tenant> page, @Param("param") TenantParam param);

    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<Tenant> selectListRel(@Param("param") TenantParam param);
}
