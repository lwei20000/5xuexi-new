package com.struggle.common.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.security.entity.Cas;
import com.struggle.common.security.param.CasParam;

import java.util.List;

/**
 * CasService
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
public interface CasService extends IService<Cas> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<Cas>
     */
    PageResult<Cas> pageRel(CasParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Cas>
     */
    List<Cas> listRel(CasParam param);

    /**
     * 根据id查询
     *
     * @param casId casId
     * @return Cas
     */
    Cas getByIdRel(Integer casId);

    /**
     * 添加Cas
     *
     * @param cas Cas信息
     * @return boolean
     */
    boolean saveCas(Cas cas);

    /**
     * 修改Cas
     *
     * @param cas Cas信息
     * @return boolean
     */
    boolean updateCas(Cas cas);
}
