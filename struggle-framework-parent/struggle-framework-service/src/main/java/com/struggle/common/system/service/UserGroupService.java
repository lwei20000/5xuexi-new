package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.entity.UserGroup;

import java.util.List;

/**
 * 用户分组Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:35
 */
public interface UserGroupService extends IService<UserGroup> {

    /**
     * 批量添加用户分组
     *
     * @param userId  用户id
     * @param groupIds 分组id集合
     * @return int
     */
    int saveBatch(Integer userId, List<Integer> groupIds);

    /**
     * 根据用户id查询分组
     *
     * @param userId 用户id
     * @return List<Role>
     */
    List<Group> listByUserId(Integer userId);

    /**
     * 批量根据用户id查询分组
     *
     * @param userIds 用户id集合
     * @return List<RoleResult>
     */
    List<Group> listByUserIds(List<Integer> userIds);

}
