package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.param.RoleParam;

import java.util.List;

/**
 * 角色Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:32
 */
public interface RoleService extends IService<Role> {

    void getRoleChildrenIds(Role role, List<Integer> childrenIds);

    /**
     * 关联查询全部据说
     *
     * @param param 查询参数
     * @return List<Role>
     */
    List<Role> listRel(RoleParam param);

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return boolean
     */
    boolean saveRole(Role role);
    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return boolean
     */
    boolean updateRole(Role role);
}
