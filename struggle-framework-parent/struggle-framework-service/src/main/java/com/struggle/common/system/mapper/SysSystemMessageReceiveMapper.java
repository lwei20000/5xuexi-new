package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.entity.SysSystemMessageReceive;
import com.struggle.common.system.param.SysSystemMessageReceiveParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: SysSystemMessageReceiveMapper
 * @Description: 系统消息接收对象-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysSystemMessageReceiveMapper extends BaseMapper<SysSystemMessageReceive> {

    @InterceptorIgnore(tenantLine = "true")
    List<SysSystemMessage> pageRel(@Param("page") IPage<SysSystemMessage> page, @Param("param") SysSystemMessageReceiveParam param);

    @InterceptorIgnore(tenantLine = "true")
    SysSystemMessage get(@Param("param")  SysSystemMessageReceiveParam sysSystemMessageReceiveParam);
}