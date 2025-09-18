package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.param.SysSystemMessageParam;

import java.util.List;

/**
 *
 * @ClassName: SysMessageService
 * @Description: 消息-Service层
 * @author xsk
 */
public interface ISysSystemMessageService extends IService<SysSystemMessage>{

    PageResult<SysSystemMessage>  pageRel(SysSystemMessageParam param);

    SysSystemMessage getDetailById(Integer id);

    void saveSystemMessage(SysSystemMessage sysSystemMessage);

    void updateSystemMessage(SysSystemMessage sysSystemMessage);

    void deleteSystemMessage( List<Integer> idList);

}