package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.SysMessage;

/**
 *
 * @ClassName: SysMessage
 * @Description: 消息-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysMessageMapper extends BaseMapper<SysMessage> {

}