package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.entity.SysSystemMessageRead;
import com.struggle.common.system.param.SysMessageReadParam;
import com.struggle.common.system.param.SysSystemMessageReadParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysSystemMessageReadMapper
 * @Description: 系统消息已读记录-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysSystemMessageReadMapper extends BaseMapper<SysSystemMessageRead> {
    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysSystemMessageRead> selectPageRel(@Param("page") IPage<SysSystemMessageRead> page, @Param("param") SysSystemMessageReadParam param);

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String,Object>> _selectCount(@Param("param") SysSystemMessageReadParam param);
}