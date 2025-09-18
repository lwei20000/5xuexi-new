package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Dictionary;
import com.struggle.common.system.mapper.DictionaryMapper;
import com.struggle.common.system.param.DictionaryParam;
import com.struggle.common.system.service.DictionaryService;
import org.springframework.stereotype.Service;

/**
 * 字典Service实现
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:03
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {
    @Override
    public PageResult<Dictionary> pageRel(DictionaryParam param) {
        PageParam<Dictionary, DictionaryParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        return new PageResult<>(baseMapper.selectPageRel(page, param), page.getTotal());
    }
}
