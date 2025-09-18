package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.param.GroupParam;

import java.util.List;

/**
 * 组织机构Service
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
public interface GroupService extends IService<Group> {
    /**
     * 关联分页查询
     *
     * @param param 查询参数
     * @return PageResult<Group>
     */
    PageResult<Group> pageRel(GroupParam param);
    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<Group>
     */
    List<Group> listRel(GroupParam param);

    /**
     * 添加机构
     *
     * @param group 机构信息
     * @return boolean
     */
    boolean saveGroup(Group group);

    /**
     * 修改机构
     *
     * @param group 机构信息
     * @return boolean
     */
    boolean update(Group group);

}


