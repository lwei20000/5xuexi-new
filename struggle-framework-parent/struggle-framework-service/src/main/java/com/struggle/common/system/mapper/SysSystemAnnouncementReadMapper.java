package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysSystemAnnouncementRead;
import com.struggle.common.system.param.SysSystemAnnouncementReadParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysSystemAnnouncementReadMapper
 * @Description: 系统通知公告已读记录-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysSystemAnnouncementReadMapper extends BaseMapper<SysSystemAnnouncementRead> {
    /**
     * 分页查询
     *
     * @param page  分页对象
     * @param param 查询参数
     * @return List<Dictionary>
     */
    @InterceptorIgnore(tenantLine = "true")
    List<SysSystemAnnouncementRead> selectPageRel(@Param("page") IPage<SysSystemAnnouncementRead> page,@Param("param") SysSystemAnnouncementReadParam param);

    @InterceptorIgnore(tenantLine = "true")
    List<Map<String,Object>> _selectCount(@Param("param") SysSystemAnnouncementReadParam param);
}