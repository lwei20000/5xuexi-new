package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.RoleOrg;
import com.struggle.common.system.mapper.RoleOrgMapper;
import com.struggle.common.system.service.RoleOrgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:36
 */
@Service
public class RoleOrgServiceImpl extends ServiceImpl<RoleOrgMapper, RoleOrg> implements RoleOrgService {


    @Override
    public int saveBatch(Integer roleId, List<Integer> orgIds) {
        return baseMapper.insertBatch(roleId, orgIds);
    }

    @Override
    public List<Org> listByRoleId(Integer userId) {
        return baseMapper.selectByRoleId(userId);
    }

    @Override
    public List<Org> listByRoleIds(List<Integer> userIds) {
        return baseMapper.selectByRoleIds(userIds);
    }

}
