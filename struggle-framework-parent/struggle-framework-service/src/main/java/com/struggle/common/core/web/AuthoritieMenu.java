package com.struggle.common.core.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.system.entity.Menu;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限菜单
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthoritieMenu implements Serializable {

    List<Menu> menus;

    Map<String, List<Integer>> authoritieMap;

    public AuthoritieMenu(){

    }
    public AuthoritieMenu(List<Menu> menus){
        if(!CollectionUtils.isEmpty(menus)){
            int size = menus.size();
            List<Menu> menuList = new ArrayList<>(size);
            Map<String,List<Integer>> map = new HashMap<>(size);
            Map<Integer,Menu> _map = new HashMap<>();
            for(Menu menu:menus){
                if(StringUtils.hasText(menu.getAuthority())){
                    List<Integer> _roleIds = map.get(menu.getAuthority());
                    if(!CollectionUtils.isEmpty(_roleIds)){
                        _roleIds.add(menu.getRoleId());
                    }else{
                        _roleIds = new ArrayList<>();
                        _roleIds.add(menu.getRoleId());
                        map.put(menu.getAuthority(),_roleIds);
                    }
                }

                //去重
                Menu _menu = _map.get(menu.getMenuId());
                if(_menu == null){
                    menu.setRoleId(null);
                    if(menu.getMenuType().equals(2)){ //按钮只保存MenuType、authority
                        _menu = new Menu();
                        _menu.setAuthority(menu.getAuthority());
                        _menu.setMenuType(menu.getMenuType());
                        _menu.setSortNumber(menu.getSortNumber());
                        menuList.add(_menu);
                    }else{
                        menuList.add(menu);
                    }
                    _map.put(menu.getMenuId(),menu);
                }
            }
            menuList.sort(
                    (o1,o2) ->{
                        if (o1.getSortNumber() > o2.getSortNumber()) {
                            return 1;
                        } else if (o1.getSortNumber() < o2.getSortNumber()) {
                            return -1;
                        } else {
                            return 0;
                        }
                    });
           this.authoritieMap = map;
           this.menus = menuList;
        }
    }
}
