package com.struggle.common.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.base.mapper.UserMajorMapper;
import com.struggle.common.base.param.UserMajorParam;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.report.entity.OrgRemark;
import com.struggle.common.report.mapper.OrgRemarkMapper;
import com.struggle.common.report.param.OrgRemarkParam;
import com.struggle.common.report.service.IOrgRemarkService;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.service.OrgService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: OrgRemarkService
 * @Description:  站点付费备注-ServiceImpl层
 * @author xsk
 */
@Service
public class OrgRemarkServiceImpl extends ServiceImpl<OrgRemarkMapper, OrgRemark> implements  IOrgRemarkService {

    @Resource
    private OrgService orgService;

    @Resource
    private UserMajorMapper userMajorMapper;

    @Override
    public PageResult<OrgRemark> pageRel(OrgRemarkParam param) {
        PageParam<OrgRemark, OrgRemarkParam> page = new PageParam<>(param);
        PageParam<OrgRemark, OrgRemarkParam> selectPage = baseMapper.selectPage(page, page.getWrapper());
        List<OrgRemark> records = selectPage.getRecords();
        this.initOther(records);
        return new PageResult<>(records,selectPage.getTotal());
    }

    @Override
    public void generate(String year) {
        List<Org> orgs = orgService.list(new LambdaQueryWrapper<Org>().like(Org::getOrgType, "学习中心"));
        if(!CollectionUtils.isEmpty(orgs)){
            List<Integer> _orgIds = orgs.stream().map(Org::getOrgId).collect(Collectors.toList());
            List<OrgRemark> orgRemarks = baseMapper.selectList(new LambdaQueryWrapper<OrgRemark>()
                    .eq(OrgRemark::getYear, year)
                    .in(OrgRemark::getOrgId, _orgIds));
            Map<String,OrgRemark> orgRemarkMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(orgRemarks)){
                for(OrgRemark orgRemark:orgRemarks){
                    String key = year +"_"+orgRemark.getOrgId();
                    orgRemarkMap.put(key,orgRemark);
                }
            }

            List<OrgRemark> orgRemarkList = new ArrayList<>();
            for(Integer orgId:_orgIds){
                String key = year +"_"+orgId;
                OrgRemark orgRemark = orgRemarkMap.get(key);
                if(orgRemark == null){
                    orgRemark = new OrgRemark();
                    orgRemark.setYear(year);
                    orgRemark.setOrgId(orgId);
                    orgRemarkList.add(orgRemark);
                }
            }
            if(!CollectionUtils.isEmpty(orgRemarkList)){
                this.saveBatch(orgRemarkList);
            }
        }
    }

    private void initOther(List<OrgRemark> records ){
        if(!CollectionUtils.isEmpty(records)){
            Set<String> yearList = new HashSet<>();
            Set<Integer> orgList = new HashSet<>();
            for(OrgRemark orgRemark:records){
                yearList.add(orgRemark.getYear());
                orgList.add(orgRemark.getOrgId());
            }

            List<Org> orgs = orgService.listByIds(orgList);
            Map<Integer,Org> orgMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(orgs)){
                for(Org org:orgs){
                    orgMap.put(org.getOrgId(),org);
                }
            }

            UserMajorParam p = new UserMajorParam();
            p.setMajorYears(new ArrayList<>(yearList));
            p.setOrgIds(new ArrayList<>(orgList));
            List<Map> maps = userMajorMapper.selectCountRel(p);
            Map<String,Integer> orgRemarkMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(maps)){
                for(Map map:maps){
                    String key = map.get("major_year")+"_"+map.get("org_id");
                    orgRemarkMap.put(key,Integer.valueOf(map.get("num").toString()));
                }
            }

            for(OrgRemark orgRemark:records){
                String key = orgRemark.getYear() +"_"+orgRemark.getOrgId();
                Integer num = orgRemarkMap.get(key);
                if(num == null){
                    orgRemark.setStudentNum(0);
                }else{
                    orgRemark.setStudentNum(num);
                }
                orgRemark.setOrg(orgMap.get(orgRemark.getOrgId()));
            }
        }
    }
}