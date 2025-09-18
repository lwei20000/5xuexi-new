package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.param.TenantParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户Service
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
public interface TenantService extends IService<Tenant> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<Tenant>
     */
    PageResult<Tenant> pageRel(TenantParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Tenant>
     */
    List<Tenant> listRel(TenantParam param);
    /**
     * 添加租户
     *
     * @param tenant 租户信息
     * @return boolean
     */
    boolean saveTenant(Tenant tenant);
    /**
     * 修改租户
     *
     * @param tenant 租户信息
     * @return boolean
     */
    boolean updateTenant(Tenant tenant);
    /**
     * 导入
     * @param file
     * @return
     */
    ApiResult<?> importBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res);

    /**
     * 导出Excel模板
     */
    void templateExport(HttpServletResponse response);
    /**
     * 获取子租户IDS,包含自己
     * @return
     */
    List<Integer> getChildrens(Integer tenantId);
    /**
     * 获取子租户,包含自己
     * @return
     */
    List<Tenant>  getChildrenTenants(Integer tenantId);
    /**
     * 获取父租户IDS,包含自己
     * @return
     */
    List<Integer> getParents(Integer tenantId);
    /**
     * 获取父租户,包含自己
     * @return
     */
    List<Tenant>  getParentTenants(Integer tenantId);
}
