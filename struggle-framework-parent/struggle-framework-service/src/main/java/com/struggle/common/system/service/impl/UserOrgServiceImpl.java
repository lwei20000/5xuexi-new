package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.UserOrg;
import com.struggle.common.system.mapper.UserOrgMapper;
import com.struggle.common.system.service.UserOrgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:36
 */
@Service
public class UserOrgServiceImpl extends ServiceImpl<UserOrgMapper, UserOrg> implements UserOrgService {


    @Override
    public int saveBatch(Integer userId, List<Integer> orgIds) {
        return baseMapper.insertBatch(userId, orgIds);
    }

    @Override
    public List<Org> listByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<Org> listByUserIds(List<Integer> userIds) {
        return baseMapper.selectByUserIds(userIds);
    }

}
