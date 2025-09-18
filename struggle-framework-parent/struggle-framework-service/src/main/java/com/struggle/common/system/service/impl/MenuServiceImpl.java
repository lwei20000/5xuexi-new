package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.Menu;
import com.struggle.common.system.mapper.MenuMapper;
import com.struggle.common.system.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 菜单Service实现
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:10
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public void getMenuChildrenIds(Menu menu, List<Integer> childrenIds) {
        List<Menu> menus = baseMapper.selectList(Wrappers.<Menu>lambdaQuery().select(Menu::getMenuId).eq(Menu::getParentId, menu.getMenuId()));
        if(!CollectionUtils.isEmpty(menus)){
            for(Menu _menu : menus){
                childrenIds.add(_menu.getMenuId());
                this.getMenuChildrenIds(_menu,childrenIds);
            }
        }
    }
}
