package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.OrgLeader;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.User;

import java.util.List;

/**
 * 用户机构Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:35
 */
public interface OrgLeaderService extends IService<OrgLeader> {

    /**
     * 批量添加用户机构
     *
     * @param orgId  机构Id
     * @param userIds 用户id集合
     * @return int
     */
    int saveBatch(Integer orgId, List<Integer> userIds);

    /**
     * 根据用户id查询机构
     *
     * @param userId 用户id
     * @return List<Role>
     */
    List<Org> listByUserId(Integer userId);

    /**
     * 批量根据机构id查询用户
     *
     * @param orgIds 机构id集合
     * @return List<RoleResult>
     */
    List<User> listByOrgIds(List<Integer> orgIds);

}
