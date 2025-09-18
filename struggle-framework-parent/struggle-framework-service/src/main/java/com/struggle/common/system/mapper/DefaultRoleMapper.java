package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.DefaultRole;

/**
 * 默认角色Mapper
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:44
 */
@DS("system")
public interface DefaultRoleMapper extends BaseMapper<DefaultRole> {

}
