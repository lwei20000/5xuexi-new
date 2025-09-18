package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.Role;
import com.struggle.common.system.entity.RoleOrg;
import com.struggle.common.system.mapper.RoleMapper;
import com.struggle.common.system.param.RoleParam;
import com.struggle.common.system.service.RoleOrgService;
import com.struggle.common.system.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:11
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleOrgService roleOrgService;

    @Override
    public void getRoleChildrenIds(Role role, List<Integer> childrenIds) {
        List<Role> roles = baseMapper.selectList(Wrappers.<Role>lambdaQuery().select(Role::getRoleId).eq(Role::getParentId, role.getRoleId()));
        if(!CollectionUtils.isEmpty(roles)){
            for(Role _role : roles){
                childrenIds.add(_role.getRoleId());
                this.getRoleChildrenIds(_role,childrenIds);
            }
        }
    }

    @Override
    public List<Role> listRel(RoleParam param) {
        PageParam<Role, RoleParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        List<Role> roles = baseMapper.selectList(page.getOrderWrapper());
        this.selectRoleOrgs(roles);
        return roles;
    }

    @Override
    @Transactional
    public boolean saveRole(Role role) {

        role.setSystemDefault(0);
        if (baseMapper.selectCount(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, role.getRoleCode())) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        if (baseMapper.selectCount(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, role.getRoleName())) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        boolean result = baseMapper.insert(role) > 0;
        if (result && role.getOrgs() != null && role.getOrgs().size() > 0) {
            List<Integer> orgIds = role.getOrgs().stream().map(Org::getOrgId).collect(Collectors.toList());
            if (roleOrgService.saveBatch(role.getRoleId(), orgIds) < orgIds.size()) {
                throw new BusinessException("角色指定机构添加失败");
            }
        }
        return result;
    }

    @Override
    @Transactional
    public boolean updateRole(Role role) {

        if(this.count(Wrappers.<Role>lambdaQuery()
                .eq(Role::getRoleId, role.getRoleId()).gt(Role::getSystemDefault,0))>0){
            throw new BusinessException("全局的角色，不能修改");
        }

        if (role.getRoleCode() != null && baseMapper.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, role.getRoleCode())
                .ne(Role::getRoleId, role.getRoleId())) > 0) {
            throw new BusinessException("角色标识已存在");
        }
        if (role.getRoleName() != null && baseMapper.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName, role.getRoleName())
                .ne(Role::getRoleId, role.getRoleId())) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        if(role.getParentId() !=null && role.getParentId() !=0){
            if(role.getRoleId().equals(role.getParentId())){
                throw new BusinessException("上级角色不能是当前角色");
            }

            List<Integer> childrenIds = new ArrayList<>();
            this.getRoleChildrenIds(role,childrenIds);
            if(childrenIds.contains(role.getParentId())){
                throw new BusinessException("上级角色不能是当前角色的子角色");
            }
        }

        boolean result =baseMapper.update(role,Wrappers.<Role>lambdaUpdate()
                .set(Role::getComments,role.getComments())
                .set(Role::getParentId,role.getParentId())
                .eq(Role::getRoleId,role.getRoleId()))>0;
        roleOrgService.remove(new LambdaUpdateWrapper<RoleOrg>().eq(RoleOrg::getRoleId, role.getRoleId()));
        if (role.getOrgs() != null && role.getOrgs().size() > 0) {
            List<Integer> orgIds = role.getOrgs().stream().map(Org::getOrgId).collect(Collectors.toList());
            if (roleOrgService.saveBatch(role.getRoleId(), orgIds) < orgIds.size()) {
                throw new BusinessException("角色指定机构添加失败");
            }
        }

        return result;
    }

    /**
     * 批量查询角色指定的机构
     *
     * @param roles 据说集合
     */
    private void selectRoleOrgs(List<Role> roles) {
        if (roles != null && roles.size() > 0) {
            List<Integer> roleIds = roles.stream().map(Role::getRoleId).collect(Collectors.toList());
            List<Org> roleOrgs = roleOrgService.listByRoleIds(roleIds);
            if (!CollectionUtils.isEmpty(roleOrgs)) {
                Map<Integer,List<Org>> result = roleOrgs.stream().collect(Collectors.groupingBy(it -> it.getRoleId()));
                for (Role role : roles) {
                    List<Org> orgs = result.get(role.getRoleId());
                    role.setOrgs(orgs);
                }
            }
        }
    }
}
