package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.UserFile;
import com.struggle.common.system.param.UserFileParam;

import java.util.List;

/**
 * 用户文件Service
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
public interface UserFileService extends IService<UserFile> {


    /**
     * 关联分页查询
     *
     * @param param 查询参数
     * @return PageResult<DictionaryData>
     */
    PageResult<UserFile> pageRel(UserFileParam param);

    /**
     * 关联查询全部
     *
     * @param param 查询参数
     * @return List<LoginRecord>
     */
    List<UserFile> listRel(UserFileParam param);
    /**
     * 查找子集文件夹
     * @param userFile
     * @param childrenIds
     */
    void getUserFileChildrenIds(UserFile userFile,List<Integer> childrenIds);
}
