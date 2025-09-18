package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.Menu;

/**
 * 菜单Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:32
 */
@DS("system")
public interface MenuMapper extends BaseMapper<Menu> {

}
