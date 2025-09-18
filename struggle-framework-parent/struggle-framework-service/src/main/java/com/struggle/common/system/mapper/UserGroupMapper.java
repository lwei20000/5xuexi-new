package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.entity.UserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户机构Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@DS("system")
public interface UserGroupMapper extends BaseMapper<UserGroup> {

    /**
     * 批量添加用户机构
     *
     * @param userId  用户id
     * @param groupIds 机构id集合
     * @return int
     */
    int insertBatch(@Param("userId") Integer userId, @Param("groupIds") List<Integer> groupIds);

    /**
     * 根据用户id查询机构
     *
     * @param userId 用户id
     * @return List<Group>
     */
    List<Group> selectByUserId(@Param("userId") Integer userId);

    /**
     * 批量根据用户id查询机构
     *
     * @param userIds 用户id集合
     * @return List<RoleResult>
     */
    List<Group> selectByUserIds(@Param("userIds") List<Integer> userIds);

}
