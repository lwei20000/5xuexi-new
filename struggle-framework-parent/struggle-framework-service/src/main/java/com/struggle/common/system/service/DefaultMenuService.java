package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.DefaultMenu;

import java.util.List;

/**
 * 菜单Service
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:31
 */
public interface DefaultMenuService extends IService<DefaultMenu> {


    void getMenuChildrenIds(DefaultMenu defaultmenu, List<Integer> childrenIds);
}
