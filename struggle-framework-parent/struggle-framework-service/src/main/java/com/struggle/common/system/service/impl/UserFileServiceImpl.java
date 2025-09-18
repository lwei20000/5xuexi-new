package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.mapper.UserFileMapper;
import com.struggle.common.system.param.UserFileParam;
import com.struggle.common.system.service.UserFileService;
import com.struggle.common.system.entity.UserFile;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 用户文件Service实现
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    @Override
    public PageResult<UserFile> pageRel(UserFileParam param) {
        PageParam<UserFile, UserFileParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return new PageResult<>(baseMapper.selectPageRel(page, param), page.getTotal());
    }

    @Override
    public List<UserFile> listRel(UserFileParam param) {
        PageParam<UserFile, UserFileParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(baseMapper.selectListRel(param));
    }

    @Override
    public void getUserFileChildrenIds(UserFile userFile, List<Integer> childrenIds) {
        List<UserFile> userFiles = baseMapper.selectList(Wrappers.<UserFile>lambdaQuery().eq(UserFile::getParentId, userFile.getId()).eq(UserFile::getIsDirectory,1));
        if(!CollectionUtils.isEmpty(userFiles)){
            for(UserFile _userFile : userFiles){
                childrenIds.add(_userFile.getId());
                this.getUserFileChildrenIds(_userFile,childrenIds);
            }
        }
    }
}
