package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.OrgLeader;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.mapper.OrgMapper;
import com.struggle.common.system.param.OrgParam;
import com.struggle.common.system.service.OrgLeaderService;
import com.struggle.common.system.service.OrgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 组织机构Service实现
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Service
public class OrgServiceImpl extends ServiceImpl<OrgMapper, Org> implements OrgService {

    @Resource
    private OrgLeaderService orgLeaderService;

    @Override
    public PageResult<Org> pageRel(OrgParam param) {
        PageParam<Org, OrgParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        List<Org> list = baseMapper.selectPageRel(page, param);
        return new PageResult<>(list, page.getTotal());
    }

    @Override
    public List<Org> listRel(OrgParam param) {
        PageParam<Org, OrgParam> page = new PageParam<>(param);
        page.setDefaultOrder("sort_number");
        List<Org> list = baseMapper.selectListRel(param,null,null);
        // 查询机构的主管
        if(param.isDetail()){
            this.selectOrgLeaders(list);
        }
        return page.sortRecords(list);
    }

    /**
     * 递归更新fullName,code
     * @param org
     */
    private void  updateOrgFull(Org org){
        String orgCodeStr = this.getOrgFullCodeStr(org);
        String orgNameStr = this.getOrgFullNameStr(org);
        baseMapper.update(null, Wrappers.<Org>lambdaUpdate().eq(Org::getOrgId,org.getOrgId()).set(Org::getOrgFullCode,orgCodeStr).set(Org::getOrgFullName,orgNameStr));
        List<Org> orgs = baseMapper.selectList(Wrappers.<Org>lambdaQuery().select(Org::getOrgId,Org::getOrgName,Org::getParentId).eq(Org::getParentId, org.getOrgId()));
        if(!CollectionUtils.isEmpty(orgs)){
            for(Org _org : orgs){
                this.updateOrgFull(_org);
            }
        }
    }

    @Override
    @Transactional
    public boolean saveOrg(Org org) {

        if(org.getParentId() == 0){
            if (baseMapper.selectCount(new LambdaQueryWrapper<Org>()
                    .eq(Org::getParentId, 0))> 0) {
                throw new BusinessException("只能存在一个主机构");
            }
        }

        if (baseMapper.selectCount(new LambdaQueryWrapper<Org>()
                .eq(Org::getOrgCode, org.getOrgCode()))> 0) {
            throw new BusinessException("机构CODE已存在");
        }

        if (baseMapper.selectCount(new LambdaQueryWrapper<Org>()
                .eq(Org::getOrgName, org.getOrgName())
                .eq(Org::getParentId, org.getParentId())) > 0) {
            throw new BusinessException("机构名称已存在");
        }

        boolean result = baseMapper.insert(org)> 0;

        if(result){
            this.updateOrgFull(org);
        }

        if (result && org.getLeaders() != null && org.getLeaders().size() > 0) {
            List<Integer> userIds = org.getLeaders().stream().map(User::getUserId).collect(Collectors.toList());
            if (orgLeaderService.saveBatch(org.getOrgId(), userIds) < userIds.size()) {
                throw new BusinessException("机构主管添加失败");
            }
        }

        return result;
    }

    @Override
    @Transactional
    public boolean update(Org org) {

        if(org.getParentId() == 0){
            if (baseMapper.selectCount(new LambdaQueryWrapper<Org>().eq(Org::getParentId, 0).ne(Org::getOrgId, org.getOrgId()))> 0) {
                throw new BusinessException("只能存在一个主机构");
            }
        }

        if (baseMapper.selectCount(new LambdaQueryWrapper<Org>().eq(Org::getOrgCode, org.getOrgCode()).ne(Org::getOrgId, org.getOrgId()))> 0) {
            throw new BusinessException("机构CODE已存在");
        }

        if (baseMapper.selectCount(new LambdaQueryWrapper<Org>().eq(Org::getOrgName, org.getOrgName()).eq(Org::getParentId, org.getParentId()).ne(Org::getOrgId, org.getOrgId())) > 0) {
            throw new BusinessException("机构名称已存在");
        }

        if(org.getParentId() !=null && org.getParentId() !=0){
            if(org.getOrgId().equals(org.getParentId())){
                throw new BusinessException("上级机构不能是当前机构");
            }

            List<Integer> childrenIds = new ArrayList<>();
            this.getOrgChildrenIds(org,childrenIds);
            if(childrenIds.contains(org.getParentId())){
                throw new BusinessException("上级机构不能是当前机构的子机构");
            }
        }

        Org _org = baseMapper.selectById(org.getOrgId());
        org.setUpdateTime(null);
        boolean result =  baseMapper.update(org,Wrappers.<Org>lambdaUpdate()
                .set(Org::getComments,org.getComments())
                .set(Org::getParentId,org.getParentId())
                .eq(Org::getOrgId,org.getOrgId()))>0;
        if(result){
            //层级变化或者名称变化
            if(!_org.getParentId().equals(org.getParentId()) || !_org.getOrgName().equals(org.getOrgName())){
                this.updateOrgFull(org);
            }
            orgLeaderService.remove(new LambdaUpdateWrapper<OrgLeader>().eq(OrgLeader::getOrgId, org.getOrgId()));
            if (org.getLeaders() != null && org.getLeaders().size() > 0) {
                List<Integer> userIds = org.getLeaders().stream().map(User::getUserId).collect(Collectors.toList());
                if (orgLeaderService.saveBatch(org.getOrgId(), userIds) < userIds.size()) {
                    throw new BusinessException("机构主管添加失败");
                }
            }
        }
        return result;
    }

    @Override
    public List<Integer> getChildrens(List<Integer> orgIds) {
        List<Integer> _orgs = new ArrayList<>();
        List<Org> orgs = baseMapper.selectList(new LambdaQueryWrapper<Org>().select(Org::getOrgId,Org::getOrgCode,Org::getOrgFullCode).in(Org::getOrgId,orgIds));
        if(orgs !=null){
            LambdaQueryWrapper<Org> orgLambdaQueryWrapper = new LambdaQueryWrapper<>();
            for(Org org: orgs){
                orgLambdaQueryWrapper.or().likeRight(Org::getOrgFullCode,org.getOrgFullCode());
            }
            orgLambdaQueryWrapper.select(Org::getOrgId);
            List<Org> orgs_1 = baseMapper.selectList(orgLambdaQueryWrapper);
            if(!CollectionUtils.isEmpty(orgs_1)){
                _orgs = orgs_1.stream().map(Org::getOrgId).collect(Collectors.toList());
            }
        }

        return _orgs;
    }

    @Override
    public List<Integer> getParents(List<Integer> orgIds) {
        List<Integer> _orgs = new ArrayList<>();
        List<Org> orgs = baseMapper.selectList(new LambdaQueryWrapper<Org>().select(Org::getOrgId,Org::getOrgCode,Org::getOrgFullCode).in(Org::getOrgId,orgIds));
        if(orgs !=null){
            Set<String> orgFullCodes = new HashSet<>();
            for(Org org: orgs){
                orgFullCodes.addAll(CommonUtil.parentCode(org.getOrgFullCode()));
            }
            List<Org> orgs_1 = baseMapper.selectList(new LambdaQueryWrapper<Org>().select(Org::getOrgId).in(Org::getOrgFullCode,orgFullCodes));
            if(!CollectionUtils.isEmpty(orgs_1)){
                _orgs = orgs_1.stream().map(Org::getOrgId).collect(Collectors.toList());
            }
        }

        return _orgs;
    }

    private String getOrgFullCodeStr(Org org){
        String _org = "<"+org.getOrgId()+">";
        if(org.getParentId() !=null && org.getParentId()!=0){
            Org org1 = baseMapper.selectOne(new LambdaQueryWrapper<Org>().select(Org::getOrgId,Org::getParentId).eq(Org::getOrgId,org.getParentId()));
            if(org1 != null){
                _org =CommonUtil.stringJoiner(this.getOrgFullCodeStr(org1),_org);
            }
        }
        return _org;
    }

    private String getOrgFullNameStr(Org org){
        String _org = org.getOrgName();
        if(org.getParentId() !=null && org.getParentId()!=0){
            Org org1 = baseMapper.selectOne(new LambdaQueryWrapper<Org>().select(Org::getOrgId,Org::getParentId,Org::getOrgName).eq(Org::getOrgId,org.getParentId()));
            if(org1 != null){
                _org = CommonUtil.stringJoiner(this.getOrgFullNameStr(org1),_org);
            }
        }
        return _org;
    }

    /**
     * 获取所以子节点Ids
     * @param org
     * @return
     */
    private void getOrgChildrenIds(Org org, List<Integer> childrenIds){
        List<Org> orgs = baseMapper.selectList(Wrappers.<Org>lambdaQuery().select(Org::getOrgId).eq(Org::getParentId, org.getOrgId()));
        if(!CollectionUtils.isEmpty(orgs)){
            for(Org _org : orgs){
                childrenIds.add(_org.getOrgId());
                this.getOrgChildrenIds(_org,childrenIds);
            }
        }
    }

    /**
     * 批量查询机构的主管
     *
     * @param orgs 机构集合
     */
    private void selectOrgLeaders(List<Org> orgs) {
        if (orgs != null && orgs.size() > 0) {
            List<Integer> orgIds = orgs.stream().map(Org::getOrgId).collect(Collectors.toList());
            List<User> users = orgLeaderService.listByOrgIds(orgIds);
            if (!CollectionUtils.isEmpty(users)){
                Map<Integer,List<User>> result = users.stream().collect(Collectors.groupingBy(it -> it.getOrgId()));
                for (Org org : orgs) {
                    org.setLeaders(result.get(org.getOrgId()));
                }
            }
        }
    }
}
