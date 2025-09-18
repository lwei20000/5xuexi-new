package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.entity.UserGroup;
import com.struggle.common.system.mapper.UserGroupMapper;
import com.struggle.common.system.service.UserGroupService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:36
 */
@Service
public class UserGroupServiceImpl extends ServiceImpl<UserGroupMapper, UserGroup> implements UserGroupService {


    @Override
    public int saveBatch(Integer userId, List<Integer> groupIds) {
        return baseMapper.insertBatch(userId, groupIds);
    }

    @Override
    public List<Group> listByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<Group> listByUserIds(List<Integer> userIds) {
        return baseMapper.selectByUserIds(userIds);
    }

}
