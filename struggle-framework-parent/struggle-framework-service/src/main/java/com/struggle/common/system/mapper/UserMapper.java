package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.core.web.DataPermissionParam;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.UserParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:14
 */
@DS("system")
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<User>
     */
    List<User> selectPageRel(@Param("page") IPage<User> page, @Param("param") UserParam param,@Param("tenantIds") List<Integer> tenantIds,@Param("tenantId") Integer tenantId);
    /**
     * 查询全部
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<User> selectListRel(@Param("param") UserParam param,@Param("tenantIds") List<Integer> tenantIds,@Param("tenantId") Integer tenantId);

    /**
     * 根据账号查询不区分租户
     *
     * @param username 账号
     * @return User
     */
    @InterceptorIgnore(tenantLine = "true")
    User selectByUsername(@Param("username") String username);
}
