package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.param.SysMessageParam;

import java.util.List;

/**
 *
 * @ClassName: SysMessageService
 * @Description: 消息-Service层
 * @author xsk
 */
public interface ISysMessageService extends IService<SysMessage>{

    PageResult<SysMessage>  pageRel(SysMessageParam param);

    SysMessage getDetailById(Integer id);

    void saveMessage(SysMessage sysMessage);

    void updateMessage(SysMessage sysMessage);

    void deleteMessage( List<Integer> idList);

}