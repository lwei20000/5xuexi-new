package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.exception.BusinessException;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.mapper.SysSystemAnnouncementMapper;
import com.struggle.common.system.mapper.SysSystemAnnouncementReadMapper;
import com.struggle.common.system.mapper.SysSystemAnnouncementReceiveMapper;
import com.struggle.common.system.param.SysSystemAnnouncementParam;
import com.struggle.common.system.param.SysSystemAnnouncementReadParam;
import com.struggle.common.system.service.DefaultRoleService;
import com.struggle.common.system.service.ISysSystemAnnouncementService;
import com.struggle.common.system.service.TenantService;
import com.struggle.common.system.service.UserService;
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
 * @ClassName: SysSystemAnnouncementServiceImpl
 * @Description:  系统通知公告-ServiceImpl层
 * @author xsk
 */
@Service
public class SysSystemAnnouncementServiceImpl extends ServiceImpl<SysSystemAnnouncementMapper, SysSystemAnnouncement> implements  ISysSystemAnnouncementService {
    @Resource
    private SysSystemAnnouncementReadMapper sysSystemAnnouncementReadMapper;
    @Resource
    private SysSystemAnnouncementReceiveMapper sysSystemAnnouncementReceiveMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private TenantService tenantService;
    @Resource
    private DefaultRoleService defaultRoleService;
    @Resource
    private UserService userService;

    @Override
    public PageResult<SysSystemAnnouncement> pageRel(SysSystemAnnouncementParam param) {
        PageParam<SysSystemAnnouncement, SysSystemAnnouncementParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        QueryWrapper<SysSystemAnnouncement> wrapper = page.getWrapper();
        wrapper.select("system_announcement_id","title","system_announcement_type","top_timestamp","create_time","update_time");
        IPage<SysSystemAnnouncement> pageList = baseMapper.selectPage(page, wrapper);
        this.initSystemAnnouncements(pageList.getRecords());
        return new PageResult<>(pageList.getRecords(), page.getTotal());
    }

    @Override
    public SysSystemAnnouncement getDetailById(Integer id) {
        SysSystemAnnouncement sysSystemAnnouncement = baseMapper.selectById(id);
        this.initSystemAnnouncement(sysSystemAnnouncement);
        return sysSystemAnnouncement;
    }

    private  void  initSystemAnnouncements(List<SysSystemAnnouncement> list){
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> systemAnnouncementIds = new ArrayList<>(list.size());
            for(SysSystemAnnouncement sysSystemAnnouncement:list){
                systemAnnouncementIds.add(sysSystemAnnouncement.getSystemAnnouncementId());
            }

            SysSystemAnnouncementReadParam p = new SysSystemAnnouncementReadParam();
            p.setSystemAnnouncementIds(systemAnnouncementIds);
            List<Map<String, Object>> _integerIntegerMap = sysSystemAnnouncementReadMapper._selectCount(p);
            Map<Integer,Integer> totalMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(_integerIntegerMap)){
                for(Map<String, Object> map:_integerIntegerMap){
                    totalMap.put(Integer.valueOf(map.get("systemAnnouncementId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            p.setHasRead(1);
            List<Map<String, Object>> integerIntegerMap = sysSystemAnnouncementReadMapper._selectCount(p);
            Map<Integer,Integer> readMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(integerIntegerMap)){
                for(Map<String, Object> map:integerIntegerMap){
                    readMap.put(Integer.valueOf(map.get("systemAnnouncementId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            for(SysSystemAnnouncement sysSystemAnnouncement:list){
                if(sysSystemAnnouncement.getTopTimestamp()>0){
                    sysSystemAnnouncement.setTopTimestamp(1L);
                }
                Integer num = readMap.get(sysSystemAnnouncement.getSystemAnnouncementId());
                if(num == null){
                    num = 0;
                }
                Integer integer = totalMap.get(sysSystemAnnouncement.getSystemAnnouncementId());
                if(integer == null){
                    integer = 0;
                }
                sysSystemAnnouncement.setReadTotal(num+" / "+integer);
            }
        }
    }

    private  void  initSystemAnnouncement(SysSystemAnnouncement item){
        if(item !=null){
            List<SysSystemAnnouncementReceive> sysSystemAnnouncementReceiveList = sysSystemAnnouncementReceiveMapper.selectList(new LambdaQueryWrapper<SysSystemAnnouncementReceive>()
                            .select(SysSystemAnnouncementReceive::getAddNewly,SysSystemAnnouncementReceive::getReceiveId,SysSystemAnnouncementReceive::getReceiveType)
                            .eq(SysSystemAnnouncementReceive::getSystemAnnouncementId, item.getSystemAnnouncementId()));
            if(!CollectionUtils.isEmpty(sysSystemAnnouncementReceiveList)){
                List<Integer> tenantIds = new ArrayList<>();
                List<Integer> userIds = new ArrayList<>();
                List<Integer> defaultRoleIds = new ArrayList<>();
                for(SysSystemAnnouncementReceive sysSystemAnnouncementReceive:sysSystemAnnouncementReceiveList){
                    if("2".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        tenantIds.add(sysSystemAnnouncementReceive.getReceiveId());
                    }else if("3".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        defaultRoleIds.add(sysSystemAnnouncementReceive.getReceiveId());
                    }else if("4".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        userIds.add(sysSystemAnnouncementReceive.getReceiveId());
                    }
                }

                Map<Integer,Tenant> tenantMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(tenantIds)){
                    List<Tenant> tenants = tenantService.list(new LambdaQueryWrapper<Tenant>()
                                    .select(Tenant::getTenantId,Tenant::getTenantName,Tenant::getTenantFullName)
                            .in(Tenant::getTenantId,tenantIds));
                    if(!CollectionUtils.isEmpty(tenants)){
                        for(Tenant tenant:tenants){
                            tenantMap.put(tenant.getTenantId(),tenant);
                        }
                    }
                }

                Map<Integer, DefaultRole> defaultRoleMap = new HashMap<>();
                if(!CollectionUtils.isEmpty(defaultRoleIds)){
                    List<DefaultRole> defaultRoles = defaultRoleService.list(
                            new LambdaQueryWrapper<DefaultRole>()
                                    .select(DefaultRole::getRoleId,DefaultRole::getRoleName,DefaultRole::getTenantType)
                                    .in(DefaultRole::getRoleId, defaultRoleIds));
                    if(!CollectionUtils.isEmpty(defaultRoles)){
                        for(DefaultRole role:defaultRoles){
                            defaultRoleMap.put(role.getRoleId(),role);
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

                for(SysSystemAnnouncementReceive sysSystemAnnouncementReceive:sysSystemAnnouncementReceiveList){
                    if("1".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        sysSystemAnnouncementReceive.setReceiveName("全部");
                    }else if("2".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        Tenant tenant = tenantMap.get(sysSystemAnnouncementReceive.getReceiveId());
                        if(tenant != null){
                            sysSystemAnnouncementReceive.setReceiveName(tenant.getTenantFullName());
                        }
                    }else if("3".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        DefaultRole role = defaultRoleMap.get(sysSystemAnnouncementReceive.getReceiveId());
                        if(role != null){
                            sysSystemAnnouncementReceive.setReceiveName(CommonUtil.stringJoiner(role.getTenantType(),role.getRoleName()));
                        }
                    }else if("4".equals(sysSystemAnnouncementReceive.getReceiveType())){
                        User user = userMap.get(sysSystemAnnouncementReceive.getReceiveId());
                        if(user !=null){
                            sysSystemAnnouncementReceive.setReceiveName(CommonUtil.stringJoiner(user.getUsername(),user.getRealname()));
                        }
                    }
                }
            }
            if(item.getTopTimestamp()>0){
                item.setTopTimestamp(1L);
            }
            item.setSysSystemAnnouncementReceiveList(sysSystemAnnouncementReceiveList);
        }
    }

    @Override
    @Transactional
    public void saveSystemAnnouncement(SysSystemAnnouncement sysSystemAnnouncement) {
        if(CollectionUtils.isEmpty(sysSystemAnnouncement.getSysSystemAnnouncementReceiveList())){
            throw new BusinessException("发布范围不能为空");
        }
        int insert = baseMapper.insert(sysSystemAnnouncement);
        if(insert>0){
            for(SysSystemAnnouncementReceive sysSystemAnnouncementReceive:sysSystemAnnouncement.getSysSystemAnnouncementReceiveList()){
                sysSystemAnnouncementReceive.setSystemAnnouncementId(sysSystemAnnouncement.getSystemAnnouncementId());
                sysSystemAnnouncementReceiveMapper.insert(sysSystemAnnouncementReceive);
            }
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void updateSystemAnnouncement(SysSystemAnnouncement sysSystemAnnouncement) {
        if(sysSystemAnnouncement.getSystemAnnouncementId() == null){
            throw new BusinessException("主键ID不能为空");
        }
        sysSystemAnnouncement.setTopTimestamp(null);
        int update = baseMapper.update(sysSystemAnnouncement, new LambdaUpdateWrapper<SysSystemAnnouncement>()
                .set(SysSystemAnnouncement::getSystemAnnouncementAttachment, sysSystemAnnouncement.getSystemAnnouncementAttachment())
                .set(SysSystemAnnouncement::getSystemAnnouncementType, sysSystemAnnouncement.getSystemAnnouncementType())
                .set(SysSystemAnnouncement::getSystemAnnouncementPicture, sysSystemAnnouncement.getSystemAnnouncementPicture())
                .eq(SysSystemAnnouncement::getSystemAnnouncementId, sysSystemAnnouncement.getSystemAnnouncementId()));
        if(update>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    @Transactional
    public void deleteSystemAnnouncement(List<Integer> idList) {

        sysSystemAnnouncementReadMapper.delete(new LambdaQueryWrapper<SysSystemAnnouncementRead>().in(SysSystemAnnouncementRead::getSystemAnnouncementId,idList));

        sysSystemAnnouncementReceiveMapper.delete(new LambdaQueryWrapper<SysSystemAnnouncementReceive>().in(SysSystemAnnouncementReceive::getSystemAnnouncementId,idList));

        int num = baseMapper.deleteBatchIds(idList);
        if(num>0){
            //删除缓存
            this.delRedis();
        }
    }

    private void delRedis(){
        redisUtil.dels(configProperties.getSystemAnnouncementPrefix(),false);
    }
}