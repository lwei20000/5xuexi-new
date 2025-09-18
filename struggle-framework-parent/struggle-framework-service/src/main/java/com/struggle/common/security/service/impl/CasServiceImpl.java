package com.struggle.common.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.security.entity.Cas;
import com.struggle.common.security.mapper.CasMapper;
import com.struggle.common.security.param.CasParam;
import com.struggle.common.security.service.CasService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 租户Service实现
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Service
public class CasServiceImpl extends ServiceImpl<CasMapper, Cas> implements CasService {

    @Override
    public PageResult<Cas> pageRel(CasParam param) {
        PageParam<Cas, CasParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        List<Cas> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Cas> listRel(CasParam param) {
        List<Cas> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<Cas, CasParam> page = new PageParam<>();
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    @Override
    public Cas getByIdRel(Integer casId) {
        CasParam param = new CasParam();
        param.setId(casId);
        return param.getOne(baseMapper.selectListRel(param));
    }


    @Override
    public boolean saveCas(Cas cas) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Cas>()
                .eq(Cas::getAppKey, cas.getAppKey())) > 0) {
            throw new BusinessException("服务Key已存在");
        }
        if (baseMapper.selectCount(new LambdaQueryWrapper<Cas>()
                .eq(Cas::getServiceId, cas.getServiceId())) > 0) {
            throw new BusinessException("服务标识已存在");
        }
        return baseMapper.insert(cas)> 0;
    }

    @Override
    public boolean updateCas(Cas cas) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Cas>()
                .eq(Cas::getAppKey, cas.getAppKey())
                .ne(Cas::getId, cas.getId())) > 0) {
            throw new BusinessException("服务Key已存在");
        }
        if (baseMapper.selectCount(new LambdaQueryWrapper<Cas>()
                .eq(Cas::getServiceId, cas.getServiceId())
                .ne(Cas::getId, cas.getId())) > 0) {
            throw new BusinessException("服务标识已存在");
        }

        boolean result = baseMapper.update(cas, Wrappers.<Cas>lambdaUpdate()
                .set(Cas::getDescription,cas.getDescription())
                .set(Cas::getAllowReturnAttr,cas.getAllowReturnAttr())
                .eq(Cas::getId,cas.getId()))>0;

        return result;
    }
}
