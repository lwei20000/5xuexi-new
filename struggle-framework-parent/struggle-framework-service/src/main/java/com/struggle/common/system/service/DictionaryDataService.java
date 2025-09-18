package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.param.DictionaryDataParam;

import java.util.List;

/**
 * 字典数据Service
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
public interface DictionaryDataService extends IService<DictionaryData> {

    /**
     * 关联分页查询
     *
     * @param param 查询参数
     * @return PageResult<DictionaryData>
     */
    PageResult<DictionaryData> pageRel(DictionaryDataParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<DictionaryData>
     */
    List<DictionaryData> listRel(DictionaryDataParam param);
}
