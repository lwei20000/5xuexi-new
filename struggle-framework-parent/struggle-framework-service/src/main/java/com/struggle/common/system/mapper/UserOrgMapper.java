package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.UserOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户机构Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@DS("system")
public interface UserOrgMapper extends BaseMapper<UserOrg> {

    /**
     * 批量添加用户机构
     *
     * @param userId  用户id
     * @param orgIds 机构id集合
     * @return int
     */
    int insertBatch(@Param("userId") Integer userId, @Param("orgIds") List<Integer> orgIds);

    /**
     * 根据用户id查询机构
     *
     * @param userId 用户id
     * @return List<Org>
     */
    List<Org> selectByUserId(@Param("userId") Integer userId);

    /**
     * 批量根据用户id查询机构
     *
     * @param userIds 用户id集合
     * @return List<RoleResult>
     */
    List<Org> selectByUserIds(@Param("userIds") List<Integer> userIds);

}
