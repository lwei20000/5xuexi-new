package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.entity.RoleMenu;
import com.struggle.common.system.mapper.RoleMenuMapper;
import com.struggle.common.system.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色菜单Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:12
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    public List<Menu> listMenuByUserId(Integer userId, Integer menuType,String appType) {

        return baseMapper.listMenuByUserId(userId, menuType,appType);
    }

    @Override
    public List<Menu> listMenuByRoleIds(List<Integer> roleIds, Integer menuType,String appType) {

        return baseMapper.listMenuByRoleIds(roleIds, menuType,appType);
    }
}
