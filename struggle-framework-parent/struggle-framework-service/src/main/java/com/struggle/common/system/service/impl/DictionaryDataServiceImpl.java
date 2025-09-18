package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.DictionaryData;
import com.struggle.common.system.mapper.DictionaryDataMapper;
import com.struggle.common.system.param.DictionaryDataParam;
import com.struggle.common.system.service.DictionaryDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据Service实现
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Service
public class DictionaryDataServiceImpl extends ServiceImpl<DictionaryDataMapper, DictionaryData>
        implements DictionaryDataService {

    @Override
    public PageResult<DictionaryData> pageRel(DictionaryDataParam param) {
        PageParam<DictionaryData, DictionaryDataParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return new PageResult<>(baseMapper.selectPageRel(page, param), page.getTotal());
    }

    @Override
    public List<DictionaryData> listRel(DictionaryDataParam param) {
        PageParam<DictionaryData, DictionaryDataParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return page.sortRecords(baseMapper.selectListRel(param));
    }
}
