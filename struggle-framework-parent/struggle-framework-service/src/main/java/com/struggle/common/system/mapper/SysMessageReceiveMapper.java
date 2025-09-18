package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.entity.SysMessageReceive;
import com.struggle.common.system.param.SysMessageReceiveParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: SysMessageReceive
 * @Description: 通知公告接收对象-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysMessageReceiveMapper extends BaseMapper<SysMessageReceive> {

    List<SysMessage> pageRel(@Param("page") IPage<SysMessage> page,
                                  @Param("param") SysMessageReceiveParam param);

    SysMessage get(@Param("param")  SysMessageReceiveParam sysMessageReceiveParam);
}