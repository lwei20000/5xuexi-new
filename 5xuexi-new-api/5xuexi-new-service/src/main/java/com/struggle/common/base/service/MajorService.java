package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.Major;
import com.struggle.common.base.param.CopyMajorParam;
import com.struggle.common.base.param.MajorParam;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 招生专业Service
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface MajorService extends IService<Major> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<Major>
     */
    PageResult<Major> pageRel(MajorParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Major>
     */
    List<Major> listRel(MajorParam param);

    /**
     * 根据id查询
     *
     * @param majorId 专业id
     * @return Major
     */
    Major getByIdRel(Integer majorId);

    /**
     * 复制专业
     *
     * @return boolean
     */
    boolean copy(CopyMajorParam major);

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
}
