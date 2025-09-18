package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Course;
import com.struggle.common.base.mapper.CourseMapper;
import com.struggle.common.base.param.CourseParam;
import com.struggle.common.base.service.CourseService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.service.TenantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程信息Service实现
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Resource
    private TenantService tenantService;

    @Override
    public PageResult<Course> pageRel(CourseParam param) {
        PageParam<Course, CourseParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        param.setTenantIdAlias(ThreadLocalContextHolder.getTenant());
        List<Course> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Course> listRel(CourseParam param) {
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        param.setTenantIdAlias(ThreadLocalContextHolder.getTenant());
        List<Course> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<Course, CourseParam> page = new PageParam<>();
        page.setDefaultOrder("update_time desc");
        return page.sortRecords(list);
    }

    @Override
    public Course getByIdRel(Integer courseId) {
        CourseParam param = new CourseParam();
        param.setCourseId(courseId);
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        param.setTenantIdAlias(ThreadLocalContextHolder.getTenant());
        return param.getOne(baseMapper.selectListRel(param));
    }
}
