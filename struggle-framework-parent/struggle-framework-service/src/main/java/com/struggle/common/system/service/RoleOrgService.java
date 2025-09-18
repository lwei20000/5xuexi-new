package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.RoleOrg;

import java.util.List;

/**
 * 角色指定机构Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:35
 */
public interface RoleOrgService extends IService<RoleOrg> {

    /**
     * 批量添加角色指定机构
     *
     * @param roleId  角色id
     * @param orgIds 机构id集合
     * @return int
     */
    int saveBatch(Integer roleId, List<Integer> orgIds);

    /**
     * 根据角色id查询机构
     *
     * @param roleId 角色id
     * @return List<Role>
     */
    List<Org> listByRoleId(Integer roleId);

    /**
     * 批量根据角色ids查询机构
     *
     * @param roleIds 角色id集合
     * @return List<RoleResult>
     */
    List<Org> listByRoleIds(List<Integer> roleIds);

}
