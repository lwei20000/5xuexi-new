package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.UserParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:52
 */
public interface UserService extends IService<User> {

    /**
     * 关联分页查询用户
     *
     * @param param 查询参数
     * @return PageResult<User>
     */
    PageResult<User> pageRel(UserParam param,boolean detail);
    /**
     * 关联查询全部用户
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<User> listRel(UserParam param,boolean detail);

    /**
     * 登录用 - - 详细信息【角色、机构】
     *
     * @param userId 用户id
     * @return User
     */
    User getByIdRel(Integer userId);

    /**
     * 根据账号查询用户
     *
     * @param username 账号
     * @return User
     */
    User getByUsername(String username);

    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return boolean
     */
    boolean saveUser(User user);

    /**
     * 修改用户
     *
     * @param user 用户信息
     * @return boolean
     */
    boolean updateUser(User user);

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
