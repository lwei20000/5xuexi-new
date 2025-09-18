package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Group;
import com.struggle.common.system.mapper.GroupMapper;
import com.struggle.common.system.param.GroupParam;
import com.struggle.common.system.service.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组Service实现
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    @Override
    public PageResult<Group> pageRel(GroupParam param) {
        PageParam<Group, GroupParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        List<Group> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Group> listRel(GroupParam param) {
        PageParam<Group, GroupParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        List<Group> list = baseMapper.selectListRel(param,null,null);
        return page.sortRecords(list);
    }

    @Override
    public boolean saveGroup(Group group) {
        if (baseMapper.selectCount(new LambdaQueryWrapper<Group>().eq(Group::getGroupCode, group.getGroupCode()))> 0) {
            throw new BusinessException("分组CODE已存在");
        }
        if (baseMapper.selectCount(new LambdaQueryWrapper<Group>().eq(Group::getGroupName, group.getGroupName())) > 0) {
            throw new BusinessException("分组名称已存在");
        }
        return  baseMapper.insert(group)> 0;
    }

    @Override
    public boolean update(Group group) {

        if (baseMapper.selectCount(new LambdaQueryWrapper<Group>()
                .eq(Group::getGroupCode, group.getGroupCode())
                .ne(Group::getGroupId, group.getGroupId()))> 0) {
            throw new BusinessException("分组CODE已存在");
        }

        if (baseMapper.selectCount(new LambdaQueryWrapper<Group>()
                .eq(Group::getGroupName, group.getGroupName())
                .ne(Group::getGroupId, group.getGroupId())) > 0) {
            throw new BusinessException("分组名称已存在");
        }

        if(group.getParentId() !=null && group.getParentId() !=0){
            if(group.getGroupId().equals(group.getParentId())){
                throw new BusinessException("上级分组不能是当前分组");
            }

            List<Integer> childrenIds = new ArrayList<>();
            this.getGroupChildrenIds(group,childrenIds);
            if(childrenIds.contains(group.getParentId())){
                throw new BusinessException("上级分组不能是当前分组的子分组");
            }
        }
        group.setUpdateTime(null);
        return  baseMapper.update(group,Wrappers.<Group>lambdaUpdate()
                .set(Group::getComments,group.getComments())
                .set(Group::getParentId,group.getParentId())
                .eq(Group::getGroupId,group.getGroupId()))>0;
    }

    /**
     * 获取所以子节点Ids
     * @param group
     * @return
     */
    private void getGroupChildrenIds(Group group, List<Integer> childrenIds){
        List<Group> groups = baseMapper.selectList(Wrappers.<Group>lambdaQuery().select(Group::getGroupId).eq(Group::getParentId, group.getGroupId()));
        if(!CollectionUtils.isEmpty(groups)){
            for(Group _group : groups){
                childrenIds.add(_group.getGroupId());
                this.getGroupChildrenIds(_group,childrenIds);
            }
        }
    }
}
