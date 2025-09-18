package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.param.RoleParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:44
 */
@DS("system")
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Role>
     */
    List<Role> selectPageRel(@Param("page") IPage<Role> page, @Param("param") RoleParam param);
    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<Role>
     */
    List<Role> selectListRel(@Param("param") RoleParam param,@Param("tenantIds") List<Integer> tenantIds, @Param("tenantId")  Integer tenantId);
}
