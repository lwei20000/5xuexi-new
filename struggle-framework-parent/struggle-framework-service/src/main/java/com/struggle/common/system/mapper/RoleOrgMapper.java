package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.RoleOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色指定机构Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@DS("system")
public interface RoleOrgMapper extends BaseMapper<RoleOrg> {

    /**
     * 批量添加角色指定机构
     *
     * @param roleId  角色id
     * @param orgIds 机构id集合
     * @return int
     */
    int insertBatch(@Param("roleId") Integer roleId, @Param("orgIds") List<Integer> orgIds);

    /**
     * 根据角色id查询机构
     *
     * @param roleId 角色id
     * @return List<Org>
     */
    List<Org> selectByRoleId(@Param("roleId") Integer roleId);

    /**
     * 批量根据角色id查询机构
     *
     * @param roleIds 角色id集合
     * @return List<RoleResult>
     */
    List<Org> selectByRoleIds(@Param("roleIds") List<Integer> roleIds);

}
