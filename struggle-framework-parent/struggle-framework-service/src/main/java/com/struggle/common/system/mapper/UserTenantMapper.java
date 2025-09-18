package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.entity.UserTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户租户Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:02
 */
@DS("system")
public interface UserTenantMapper extends BaseMapper<UserTenant> {
    
    /**
     * 根据用户id查询租户
     *
     * @param userId 用户id
     * @return List<Org>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<Tenant> selectByUserId(@Param("userId") Integer userId);
    /**
     * 根据用户ids查询租户
     *
     * @param userIds 用户ids
     * @return List<Org>
     */
    List<Tenant> selectByUserIds(@Param("userIds") List<Integer> userIds,@Param("tenantIds") List<Integer> tenantIds);
}
