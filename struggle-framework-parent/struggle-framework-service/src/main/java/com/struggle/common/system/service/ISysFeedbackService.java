package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysFeedback;
import com.struggle.common.system.param.SysFeedbackParam;

/**
 *
 * @ClassName: SysFeedbackService
 * @Description: 意见反馈-Service层
 * @author xsk
 */
public interface ISysFeedbackService extends IService<SysFeedback>{

    PageResult<SysFeedback> pageRel(SysFeedbackParam param,boolean detail);
}