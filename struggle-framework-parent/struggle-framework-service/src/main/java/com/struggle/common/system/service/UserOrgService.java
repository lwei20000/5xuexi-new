package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.UserOrg;

import java.util.List;

/**
 * 用户机构Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:35
 */
public interface UserOrgService extends IService<UserOrg> {

    /**
     * 批量添加用户机构
     *
     * @param userId  用户id
     * @param orgIds 机构id集合
     * @return int
     */
    int saveBatch(Integer userId, List<Integer> orgIds);

    /**
     * 根据用户id查询机构
     *
     * @param userId 用户id
     * @return List<Role>
     */
    List<Org> listByUserId(Integer userId);

    /**
     * 批量根据用户id查询机构
     *
     * @param userIds 用户id集合
     * @return List<RoleResult>
     */
    List<Org> listByUserIds(List<Integer> userIds);

}
