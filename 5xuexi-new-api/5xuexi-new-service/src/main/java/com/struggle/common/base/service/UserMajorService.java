package com.struggle.common.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.base.entity.UserMajor;
import com.struggle.common.base.param.UserMajorParam;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.core.world.WordOperation;
import com.struggle.common.system.entity.User;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 学生与专业关系Service
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
public interface UserMajorService extends IService<UserMajor> {

    /**
     * 分页关联查询
     *
     * @param param 查询参数
     * @return PageResult<StudentMajor>
     */
    PageResult<UserMajor> pageRel(UserMajorParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<StudentMajor>
     */
    List<UserMajor> listRel(UserMajorParam param);

    /**
     * 新增
     * @param studentMajor
     * @return
     */
    boolean saveUserMajor(UserMajor studentMajor);

    /**
     * 修改
     * @param studentMajor
     * @return
     */
    boolean updateUserMajor(UserMajor studentMajor);
    /**
     * 根据id查询
     *
     * @param id 学生与专业关系id
     * @return StudentMajor
     */
    UserMajor getByIdRel(Integer id);

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
     * 导入
     * @param file
     * @return
     */
    ApiResult<?> thesisImportBatch(MultipartFile file, HttpServletRequest request, HttpServletResponse res);

    /**
     * 导出Excel模板
     */
    void thesisTemplateExport(HttpServletResponse response);

    /**
     * 生成登记表
     * @param majorId
     * @param loginUser
     * @return
     * @throws IOException
     */
    public WordOperation generateSingleRegistrationFile(Integer  majorId, User loginUser) throws IOException;

    /**
     * 生成登记表
     * @param majorId
     * @param loginUser
     * @return
     * @throws IOException
     */
    public WordOperation generateSingleEnrollmentFile(Integer  majorId, User loginUser) throws IOException;
}
