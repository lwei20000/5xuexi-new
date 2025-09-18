package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.entity.UserLearning;
import com.struggle.common.base.mapper.UserLearningMapper;
import com.struggle.common.base.service.UserLearningService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.system.entity.Tenant;
import com.struggle.common.system.service.TenantService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserLearningServiceImpl extends ServiceImpl<UserLearningMapper, UserLearning>  implements UserLearningService {

    @Resource
    private TenantService tenantService;

    @Override
    @Async
    public void updateLearning(Courseware courseware) {
        List<Tenant> list = tenantService.list(new LambdaQueryWrapper<Tenant>().ne(Tenant::getTenantId, 1));
        for(Tenant tenant:list){
            try {
                ThreadLocalContextHolder.setTenant(tenant.getTenantId());
                baseMapper.update(null,new LambdaUpdateWrapper<UserLearning>()
                        .set(UserLearning::getPlatProgress,100)
                        .set(UserLearning::getCurrentPlayTime,0)
                        .eq(UserLearning::getCoursewareId,courseware.getCoursewareId())
                        .ge(UserLearning::getTotalPalyTime,courseware.getDuration()-20));
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                ThreadLocalContextHolder.removeTenant();
            }
        }
    }
}
