package com.struggle.common.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.security.entity.SysUserProblem;
import com.struggle.common.security.mapper.SysUserProblemMapper;
import com.struggle.common.security.service.SysUserProblemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dxf
 * @ClassName: TSysUserProblemService
 * @Description: 安全问题-ServiceImpl层
 */
@Service
public class SysUserProblemServiceImpl extends ServiceImpl<SysUserProblemMapper, SysUserProblem> implements SysUserProblemService {

    @Resource
    private SysUserProblemMapper sysUserProblemMapper;


    @Override
    @Transactional
    public void saveOrUpdateProblem(List<SysUserProblem> list) {
        sysUserProblemMapper.delete(Wrappers.<SysUserProblem>lambdaUpdate().eq(SysUserProblem::getUsername,ThreadLocalContextHolder.getUser().getUsername()));
        if(!CollectionUtils.isEmpty(list)){
            for (int i=0;i<list.size();i++) {
                SysUserProblem sysUserProblem = list.get(i);
                String loginName = ThreadLocalContextHolder.getUser().getUsername();
                sysUserProblem.setUsername(loginName);
                sysUserProblem.setSortNumber(i);
                sysUserProblemMapper.insert(sysUserProblem);
            }
        }
    }

    @Override
    public int unbinding() {
        return sysUserProblemMapper.delete(new LambdaQueryWrapper<SysUserProblem>().eq(SysUserProblem::getUsername, ThreadLocalContextHolder.getUser().getUsername()));
    }
}