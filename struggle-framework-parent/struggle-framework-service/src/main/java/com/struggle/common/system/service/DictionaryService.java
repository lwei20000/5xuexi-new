package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.param.DictionaryParam;

/**
 * 字典Service
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:03
 */
public interface DictionaryService extends IService<Dictionary> {


    /**
     * 关联分页查询
     *
     * @param param 查询参数
     * @return PageResult<DictionaryData>
     */
    PageResult<Dictionary> pageRel(DictionaryParam param);
}
