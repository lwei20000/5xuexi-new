package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.param.SysAnnouncementParam;
import com.struggle.common.system.param.TenantParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 * @ClassName: SysAnnouncement
 * @Description: 通知公告-Mapper层
 * @author xsk
 */
@DS("system")
public interface SysAnnouncementMapper extends BaseMapper<SysAnnouncement> {

}