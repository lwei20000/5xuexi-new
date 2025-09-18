package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.DefaultRole;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.param.DefaultRoleParam;
import com.struggle.common.system.param.RoleParam;

import java.util.List;

/**
 * 角色Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:32
 */
public interface DefaultRoleService extends IService<DefaultRole> {

    void getRoleChildrenIds(DefaultRole role, List<Integer> childrenIds);

    /**
     * 关联查询全部据说
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<DefaultRole> listRel(DefaultRoleParam param);

    /**
     * 添加角色
     *
     * @param role 角色信息
     * @return boolean
     */
    boolean saveRole(DefaultRole role);
    /**
     * 修改角色
     *
     * @param role 角色信息
     * @return boolean
     */
    boolean updateRole(DefaultRole role);

    /**
     * 初始化租户资源
     *
     */
    void initTenant(List<Tenant> tenantList);

    /**
     * 同步角色租户资源
     *
     * @param tenantType 租户类型
     */
    void updateTenant(String tenantType);
}
