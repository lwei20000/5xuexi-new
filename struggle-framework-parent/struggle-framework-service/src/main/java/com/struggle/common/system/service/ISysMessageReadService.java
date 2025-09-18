package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.param.SysMessageReadParam;

/**
 *
 * @ClassName: SysMessageReadService
 * @Description: 消息已读记录-Service层
 * @author xsk
 */
public interface ISysMessageReadService extends IService<SysMessageRead>{

    PageResult<SysMessageRead> read_page(SysMessageReadParam param);
}