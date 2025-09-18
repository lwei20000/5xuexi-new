package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:21
 */
@DS("system")
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 查询用户的菜单
     *
     * @param userId   用户id
     * @param menuType 菜单类型
     * @return List<Menu>
     */
    List<Menu> listMenuByUserId(@Param("userId") Integer userId, @Param("menuType") Integer menuType,@Param("appType") String appType);


    /**
     * 查询菜单
     *
     * @param roleIds   角色ids
     * @param menuType 菜单类型
     * @return List<Menu>
     */
    List<Menu> listMenuByRoleIds(@Param("roleIds") List<Integer> roleIds, @Param("menuType") Integer menuType,@Param("appType") String appType);
}
