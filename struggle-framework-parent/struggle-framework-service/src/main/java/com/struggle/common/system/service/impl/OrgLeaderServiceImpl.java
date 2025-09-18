package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.OrgLeader;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.mapper.OrgLeaderMapper;
import com.struggle.common.system.service.OrgLeaderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:36
 */
@Service
public class OrgLeaderServiceImpl extends ServiceImpl<OrgLeaderMapper, OrgLeader> implements OrgLeaderService {


    @Override
    public int saveBatch(Integer orgId, List<Integer> userIds) {
        return baseMapper.insertBatch(orgId, userIds);
    }

    @Override
    public List<Org> listByUserId(Integer userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<User> listByOrgIds(List<Integer> orgIds) {
            return baseMapper.selectByOrgIds(orgIds);
    }
}
