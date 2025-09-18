package com.struggle.common.security.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.security.entity.SysUserProblem;
/**
 *
 * @ClassName: TSysUserProblem
 * @Description: 安全问题-Mapper层
 * @author dxf
 */
@DS("system")
public interface SysUserProblemMapper extends BaseMapper<SysUserProblem> {

}