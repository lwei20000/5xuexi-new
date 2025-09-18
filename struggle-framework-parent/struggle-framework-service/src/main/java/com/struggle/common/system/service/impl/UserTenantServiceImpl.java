package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.entity.UserTenant;
import com.struggle.common.system.mapper.UserTenantMapper;
import com.struggle.common.system.service.UserTenantService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:36
 */
@Service
public class UserTenantServiceImpl extends ServiceImpl<UserTenantMapper, UserTenant> implements UserTenantService {

    @Override
    public List<Tenant> listByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<Tenant> listByUserIds(List<Integer> userIds,List<Integer> tenantIds) {
        return baseMapper.selectByUserIds(userIds,tenantIds);
    }
}
