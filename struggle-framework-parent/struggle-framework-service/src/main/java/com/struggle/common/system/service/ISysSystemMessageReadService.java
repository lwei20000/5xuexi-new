package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemMessageRead;
import com.struggle.common.system.param.SysSystemMessageReadParam;

/**
 *
 * @ClassName: SysMessageReadService
 * @Description: 消息已读记录-Service层
 * @author xsk
 */
public interface ISysSystemMessageReadService extends IService<SysSystemMessageRead>{

    PageResult<SysSystemMessageRead> read_page(SysSystemMessageReadParam param);
}