package com.struggle.common.report.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.report.entity.OrgRemark;
import com.struggle.common.report.param.OrgRemarkParam;

/**
 *
 * @ClassName: OrgRemarkService
 * @Description: 站点付费备注-Service层
 * @author xsk
 */
public interface IOrgRemarkService extends IService<OrgRemark>{


    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<OrgRemark>
     */
    PageResult<OrgRemark> pageRel(OrgRemarkParam param);

    void generate(String year);
}