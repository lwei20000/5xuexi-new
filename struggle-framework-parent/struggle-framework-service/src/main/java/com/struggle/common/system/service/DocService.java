package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.Doc;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.param.DocParam;
import com.struggle.common.system.param.RoleParam;

import java.util.List;

/**
 * 角色Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:32
 */
public interface DocService extends IService<Doc> {
    /**
     * 关联查询全部据说
     *
     * @param param 查询参数
     * @return List<User>
     */
    List<Doc> listRel(DocParam param);

    /**
     * 添加角色
     *
     * @param doc 角色信息
     * @return boolean
     */
    boolean saveDoc(Doc doc);
    /**
     * 修改角色
     *
     * @param doc 角色信息
     * @return boolean
     */
    boolean updateDoc(Doc doc);
}
