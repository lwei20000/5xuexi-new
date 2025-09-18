package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.entity.UserTenant;

import java.util.List;

/**
 * 用户租户Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:35
 */
public interface UserTenantService extends IService<UserTenant> {

    /**
     * 根据用户id查询租户
     *
     * @param userId 用户id
     * @return List<Role>
     */
    List<Tenant> listByUserId(Integer userId);

    /**
     * 根据用户ids查询租户
     *
     * @param userIds 用户ids
     * @return List<Role>
     */
    List<Tenant> listByUserIds(List<Integer> userIds,List<Integer> tenantIds);
}
