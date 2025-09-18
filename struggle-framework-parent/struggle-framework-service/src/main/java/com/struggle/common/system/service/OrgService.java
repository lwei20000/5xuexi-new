package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.param.OrgParam;

import java.util.List;

/**
 * 组织机构Service
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
public interface OrgService extends IService<Org> {
    /**
     * 关联分页查询
     *
     * @param param 查询参数
     * @return PageResult<Org>
     */
    PageResult<Org> pageRel(OrgParam param);
    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Org>
     */
    List<Org> listRel(OrgParam param);
    /**
     * 添加机构
     *
     * @param org 机构信息
     * @return boolean
     */
    boolean saveOrg(Org org);

    /**
     * 修改机构
     *
     * @param org 机构信息
     * @return boolean
     */
    boolean update(Org org);

    /**
     * 获取子机构IDS,包含自己
     * @return
     */
    List<Integer> getChildrens(List<Integer> orgIds);
    /**
     * 获取父机构IDS,包含自己
     * @return
     */
    List<Integer> getParents(List<Integer> orgIds);
}


