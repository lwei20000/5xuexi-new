package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.mapper.SysAnnouncementMapper;
import com.struggle.common.system.mapper.SysAnnouncementReadMapper;
import com.struggle.common.system.mapper.SysAnnouncementReceiveMapper;
import com.struggle.common.system.param.SysAnnouncementParam;
import com.struggle.common.system.param.SysAnnouncementReadParam;
import com.struggle.common.system.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysAnnouncementService
 * @Description:  通知公告-ServiceImpl层
 * @author xsk
 */
@Service
public class SysAnnouncementServiceImpl extends ServiceImpl<SysAnnouncementMapper, SysAnnouncement> implements  ISysAnnouncementService {

    @Resource
    private SysAnnouncementReadMapper sysAnnouncementReadMapper;

    @Resource
    private SysAnnouncementReceiveMapper sysAnnouncementReceiveMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ConfigProperties configProperties;

    @Resource
    private OrgService orgService;

    @Resource
    private RoleService roleService;

    @Resource
    private GroupService groupService;

    @Resource
    private UserService userService;

    @Override
    public PageResult<SysAnnouncement> pageRel(SysAnnouncementParam param) {
        PageParam<SysAnnouncement, SysAnnouncementParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        QueryWrapper<SysAnnouncement> wrapper = page.getWrapper();
        wrapper.select("announcement_id","title","announcement_type","top_timestamp","create_time","update_time");
        IPage<SysAnnouncement> pageList = baseMapper.selectPage(page, wrapper);
        this.initAnnouncements(pageList.getRecords());
        return new PageResult<>(pageList.getRecords(), page.getTotal());
    }

    @Override
    public SysAnnouncement getDetailById(Integer id) {
        SysAnnouncement sysAnnouncement = baseMapper.selectById(id);
        this.initAnnouncement(sysAnnouncement);
        return sysAnnouncement;
    }

    private  void  initAnnouncement(SysAnnouncement item){
        if(item != null){
            List<SysAnnouncementReceive> sysAnnouncementReceiveList = sysAnnouncementReceiveMapper.selectList(new LambdaQueryWrapper<SysAnnouncementReceive>()
                    .select(SysAnnouncementReceive::getAddNewly,SysAnnouncementReceive::getReceiveId,SysAnnouncementReceive::getReceiveType)
                    .eq(SysAnnouncementReceive::getAnnouncementId, item.getAnnouncementId()));
            if(!CollectionUtils.isEmpty(sysAnnouncementReceiveList)){
                List<Integer> orgIds = new ArrayList<>();
                List<Integer> groupIds = new ArrayList<>();
                List<Integer> userIds = new ArrayList<>();
                List<Integer> roleIds = new ArrayList<>();
                for(SysAnnouncementReceive sysAnnouncementReceive:sysAnnouncementReceiveList){
                    if("2".equals(sysAnnouncementReceive.getReceiveType())){
                        orgIds.add(sysAnnouncementReceive.getReceiveId());
                    }else if("3".equals(sysAnnouncementReceive.getReceiveType())){
                        roleIds.add(sysAnnouncementReceive.getReceiveId());
                    }else if("4".equals(sysAnnouncementReceive.getReceiveType())){
                        userIds.add(sysAnnouncementReceive.getReceiveId());
                    }else if("5".equals(sysAnnouncementReceive.getReceiveType())){
                        groupIds.add(sysAnnouncementReceive.getReceiveId());
                    }
                }

                Map<Integer,Org> orgMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(orgIds)){
                    List<Org> orgs = orgService.list(new LambdaQueryWrapper<Org>()
                            .select(Org::getOrgId,Org::getOrgName,Org::getOrgFullName)
                            .in(Org::getOrgId,orgIds));
                    if(!CollectionUtils.isEmpty(orgs)){
                        for(Org org:orgs){
                            orgMap.put(org.getOrgId(),org);
                        }
                    }
                }

                Map<Integer,Group> groupMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(groupIds)){
                    List<Group> groups = groupService.list(new LambdaQueryWrapper<Group>()
                            .select(Group::getGroupId,Group::getGroupCode,Group::getGroupName)
                            .in(Group::getGroupId,groupIds));
                    if(!CollectionUtils.isEmpty(groups)){
                        for(Group group:groups){
                            groupMap.put(group.getGroupId(),group);
                        }
                    }
                }

                Map<Integer, Role> roleMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(roleIds)){
                    List<Role> roles = roleService.list(new LambdaQueryWrapper<Role>()
                            .select(Role::getRoleId,Role::getRoleCode,Role::getRoleName)
                            .in(Role::getRoleId,roleIds));
                    if(!CollectionUtils.isEmpty(roles)){
                        for(Role role:roles){
                            roleMap.put(role.getRoleId(),role);
                        }
                    }
                }
                Map<Integer, User> userMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(userIds)){
                    List<User> users = userService.list(new LambdaQueryWrapper<User>()
                            .select(User::getUserId,User::getRealname,User::getUsername)
                            .in(User::getUserId,userIds));
                    if(!CollectionUtils.isEmpty(users)){
                        for(User user:users){
                            userMap.put(user.getUserId(),user);
                        }
                    }
                }

                for(SysAnnouncementReceive sysAnnouncementReceive:sysAnnouncementReceiveList){
                    if("1".equals(sysAnnouncementReceive.getReceiveType())){
                        sysAnnouncementReceive.setReceiveName("全部");
                    }else if("2".equals(sysAnnouncementReceive.getReceiveType())){
                        Org org = orgMap.get(sysAnnouncementReceive.getReceiveId());
                        if(org != null){
                            sysAnnouncementReceive.setReceiveName(org.getOrgName());
                        }
                    }else if("3".equals(sysAnnouncementReceive.getReceiveType())){
                        Role role = roleMap.get(sysAnnouncementReceive.getReceiveId());
                        if(role != null){
                            sysAnnouncementReceive.setReceiveName(role.getRoleName());
                        }
                    }else if("4".equals(sysAnnouncementReceive.getReceiveType())){
                        User user = userMap.get(sysAnnouncementReceive.getReceiveId());
                        if(user !=null){
                            sysAnnouncementReceive.setReceiveName(CommonUtil.stringJoiner(user.getUsername(),user.getRealname()));
                        }
                    }else if("5".equals(sysAnnouncementReceive.getReceiveType())){
                        Group group = groupMap.get(sysAnnouncementReceive.getReceiveId());
                        if(group !=null){
                            sysAnnouncementReceive.setReceiveName(group.getGroupName());
                        }
                    }
                }
            }
            if(item.getTopTimestamp()>0){
                item.setTopTimestamp(1L);
            }
            item.setSysAnnouncementReceiveList(sysAnnouncementReceiveList);
        }
    }

    private  void  initAnnouncements(List<SysAnnouncement> list){
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> announcementIds = new ArrayList<>(list.size());
            for(SysAnnouncement sysAnnouncement:list){
                announcementIds.add(sysAnnouncement.getAnnouncementId());
            }

            SysAnnouncementReadParam p = new SysAnnouncementReadParam();
            p.setAnnouncementIds(announcementIds);
            List<Map<String, Object>> _integerIntegerMap = sysAnnouncementReadMapper._selectCount(p);
            Map<Integer,Integer> totalMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(_integerIntegerMap)){
                for(Map<String, Object> map:_integerIntegerMap){
                    totalMap.put(Integer.valueOf(map.get("announcementId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            p.setHasRead(1);
            List<Map<String, Object>> integerIntegerMap = sysAnnouncementReadMapper._selectCount(p);
            Map<Integer,Integer> readMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(integerIntegerMap)){
                for(Map<String, Object> map:integerIntegerMap){
                    readMap.put(Integer.valueOf(map.get("announcementId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }
            for(SysAnnouncement sysAnnouncement:list){
                if(sysAnnouncement.getTopTimestamp()>0){
                    sysAnnouncement.setTopTimestamp(1L);
                }
                Integer num = readMap.get(sysAnnouncement.getAnnouncementId());
                if(num == null){
                    num = 0;
                }
                Integer integer = totalMap.get(sysAnnouncement.getAnnouncementId());
                if(integer == null){
                    integer = 0;
                }
                sysAnnouncement.setReadTotal(num+" / "+integer);
            }
        }
    }

    @Override
    @Transactional
    public void saveAnnouncement(SysAnnouncement sysAnnouncement) {
        if(CollectionUtils.isEmpty(sysAnnouncement.getSysAnnouncementReceiveList())){
            throw new BusinessException("发布范围不能为空");
        }
        int insert = baseMapper.insert(sysAnnouncement);
        if(insert>0){
            for(SysAnnouncementReceive sysAnnouncementReceive:sysAnnouncement.getSysAnnouncementReceiveList()){
                sysAnnouncementReceive.setAnnouncementId(sysAnnouncement.getAnnouncementId());
                sysAnnouncementReceiveMapper.insert(sysAnnouncementReceive);
            }
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void updateAnnouncement(SysAnnouncement sysAnnouncement) {
        if(sysAnnouncement.getAnnouncementId() == null){
            throw new BusinessException("主键ID不能为空");
        }
        sysAnnouncement.setTopTimestamp(null);
        int update = baseMapper.update(sysAnnouncement, new LambdaUpdateWrapper<SysAnnouncement>()
                .set(SysAnnouncement::getAnnouncementAttachment, sysAnnouncement.getAnnouncementAttachment())
                .set(SysAnnouncement::getAnnouncementType, sysAnnouncement.getAnnouncementType())
                .set(SysAnnouncement::getAnnouncementPicture, sysAnnouncement.getAnnouncementPicture())
                .eq(SysAnnouncement::getAnnouncementId, sysAnnouncement.getAnnouncementId()));
        if(update>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    @Transactional
    public void deleteAnnouncement(List<Integer> idList) {

        sysAnnouncementReadMapper.delete(new LambdaQueryWrapper<SysAnnouncementRead>().in(SysAnnouncementRead::getAnnouncementId,idList));

        sysAnnouncementReceiveMapper.delete(new LambdaQueryWrapper<SysAnnouncementReceive>().in(SysAnnouncementReceive::getAnnouncementId,idList));

        int num = baseMapper.deleteBatchIds(idList);
        if(num>0){
            //删除缓存
            this.delRedis();
        }
    }

    private void delRedis(){
        redisUtil.dels(CommonUtil.stringJoiner(configProperties.getAnnouncementPrefix(),ThreadLocalContextHolder.getTenant().toString()),false);
    }
}