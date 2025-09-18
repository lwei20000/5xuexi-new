package com.struggle.common.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.security.entity.SysUserProblem;

import java.util.List;

/**
 * @author dxf
 * @ClassName: TSysUserProblemService
 * @Description: 安全问题-Service层
 */
public interface SysUserProblemService extends IService<SysUserProblem> {

    /**
     * 新增或编辑
     *
     * @return
     */
    void saveOrUpdateProblem(List<SysUserProblem> list);

    /**
     * 解绑
     *
     * @return
     */
    int unbinding();
}