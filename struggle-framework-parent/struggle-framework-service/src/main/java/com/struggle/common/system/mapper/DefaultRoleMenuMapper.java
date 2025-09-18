package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.DefaultRoleMenu;

/**
 * 默认角色菜单Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:21
 */
@DS("system")
public interface DefaultRoleMenuMapper extends BaseMapper<DefaultRoleMenu> {

}
