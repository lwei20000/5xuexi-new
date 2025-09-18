package com.struggle.common.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.entity.Courseware;
import com.struggle.common.base.mapper.CoursewareMapper;
import com.struggle.common.base.param.CopyCoursewareParam;
import com.struggle.common.base.param.CoursewareParam;
import com.struggle.common.base.service.CoursewareService;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 章节课时信息Service实现
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Service
public class CoursewareServiceImpl extends ServiceImpl<CoursewareMapper, Courseware> implements CoursewareService {

    @Resource
    private TenantService tenantService;

    @Override
    public PageResult<Courseware> pageRel(CoursewareParam param) {
        PageParam<Courseware, CoursewareParam> page = new PageParam<>(param);
        page.setDefaultOrder("parentId,sort_number");
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        List<Courseware> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Courseware> listRel(CoursewareParam param) {
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        List<Courseware> list = baseMapper.selectListRel(param);
        // 排序
        PageParam<Courseware, CoursewareParam> page = new PageParam<>();
        page.setDefaultOrder("parentId,sort_number");
        return page.sortRecords(list);
    }

    @Override
    @Transactional
    public boolean copy(CopyCoursewareParam param) {
        if(param.getPcourseId() == null){
            throw new BusinessException("来源课程ID，不能为空");
        }
        if(param.getCourseId() == null){
            throw new BusinessException("目标课程ID，不能为空");
        }

        List<Courseware> coursewares = baseMapper.selectList(new LambdaQueryWrapper<Courseware>().eq(Courseware::getCourseId, param.getPcourseId()));
        if(CollectionUtils.isEmpty(coursewares)){
            throw new BusinessException("来源课程，没有章节课时");
        }

        List<Courseware> coursewares1 = baseMapper.selectList(new LambdaQueryWrapper<Courseware>().eq(Courseware::getCourseId, param.getCourseId()));
        if(!CollectionUtils.isEmpty(coursewares1)){
            throw new BusinessException("目标课程，已存在章节课时");
        }

        Map<Integer,List<Courseware>> coursewareMap = new HashMap<>();
        for(Courseware courseware:coursewares){
            List<Courseware> coursewares2 = coursewareMap.get(courseware.getParentId());
            if(!CollectionUtils.isEmpty(coursewares2)){
                coursewares2.add(courseware);
            }else{
                coursewares2 = new ArrayList<>();
                coursewares2.add(courseware);
                coursewareMap.put(courseware.getParentId(),coursewares2);
            }
        }
        this.courseListMap(0,param.getCourseId(),new HashMap<>(),coursewareMap);

        return true;
    }

    private void courseListMap(Integer parentId,Integer courseId, Map<Integer,Integer> coursewaresIdMap,Map<Integer,List<Courseware>> coursewareMap){
        List<Courseware> coursewares = coursewareMap.get(parentId);
        if(!CollectionUtils.isEmpty(coursewares)){
            for(Courseware courseware:coursewares){
                Integer coursewaresId = courseware.getCoursewareId();
                courseware.setCoursewareId(null);
                courseware.setCourseId(courseId);
                courseware.setCreateTime(null);
                courseware.setUpdateTime(null);
                if(parentId != 0){
                    courseware.setParentId(coursewaresIdMap.get(parentId));
                }
                baseMapper.insert(courseware);
                coursewaresIdMap.put(coursewaresId,courseware.getCoursewareId());

                this.courseListMap(coursewaresId,courseId,coursewaresIdMap,coursewareMap);
            }
        }
    }

    @Override
    public Courseware getByIdRel(Integer coursewareId) {
        CoursewareParam param = new CoursewareParam();
        param.setCoursewareId(coursewareId);
        List<Integer> tenantIds = tenantService.getParents(ThreadLocalContextHolder.getTenant());
        param.setTenantIds(tenantIds);
        return param.getOne(baseMapper.selectListRel(param));
    }

    @Override
    public void getCoursewareChildrenIds(Courseware courseware, List<Integer> childrenIds) {
        List<Courseware> coursewares = baseMapper.selectList(Wrappers.<Courseware>lambdaQuery().eq(Courseware::getParentId, courseware.getCoursewareId()));
        if(!CollectionUtils.isEmpty(coursewares)){
            for(Courseware _courseware : coursewares){
                childrenIds.add(_courseware.getCoursewareId());
                this.getCoursewareChildrenIds(_courseware,childrenIds);
            }
        }
    }

}
