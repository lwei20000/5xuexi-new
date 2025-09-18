package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.system.entity.DefaultMenu;
import com.struggle.common.system.mapper.DefaultMenuMapper;
import com.struggle.common.system.service.DefaultMenuService;
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
public class DefaultMenuServiceImpl extends ServiceImpl<DefaultMenuMapper, DefaultMenu> implements DefaultMenuService {

    @Override
    public void getMenuChildrenIds(DefaultMenu menu, List<Integer> childrenIds) {
        List<DefaultMenu> menus = baseMapper.selectList(Wrappers.<DefaultMenu>lambdaQuery().select(DefaultMenu::getMenuId).eq(DefaultMenu::getParentId, menu.getMenuId()));
        if(!CollectionUtils.isEmpty(menus)){
            for(DefaultMenu _menu : menus){
                childrenIds.add(_menu.getMenuId());
                this.getMenuChildrenIds(_menu,childrenIds);
            }
        }
    }
}
