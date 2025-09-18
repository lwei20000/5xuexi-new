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
import com.struggle.common.system.mapper.SysSystemMessageMapper;
import com.struggle.common.system.mapper.SysSystemMessageReadMapper;
import com.struggle.common.system.mapper.SysSystemMessageReceiveMapper;
import com.struggle.common.system.param.SysSystemMessageParam;
import com.struggle.common.system.param.SysSystemMessageReadParam;
import com.struggle.common.system.service.DefaultRoleService;
import com.struggle.common.system.service.ISysSystemMessageService;
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
 * @ClassName: SysMessageService
 * @Description:  通知公告-ServiceImpl层
 * @author xsk
 */
@Service
public class SysSystemMessageServiceImpl extends ServiceImpl<SysSystemMessageMapper, SysSystemMessage> implements ISysSystemMessageService {

    @Resource
    private SysSystemMessageReadMapper sysSystemMessageReadMapper;

    @Resource
    private SysSystemMessageReceiveMapper sysSystemMessageReceiveMapper;

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
    public PageResult<SysSystemMessage> pageRel(SysSystemMessageParam param) {
        PageParam<SysSystemMessage, SysSystemMessageParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        QueryWrapper<SysSystemMessage> wrapper = page.getWrapper();
        wrapper.select("system_message_id","title","create_time","update_time");
        IPage<SysSystemMessage> pageList = baseMapper.selectPage(page, wrapper);
        this.initSystemMessages(pageList.getRecords());
        return new PageResult<>(pageList.getRecords(), page.getTotal());
    }

    @Override
    public SysSystemMessage getDetailById(Integer id) {
        SysSystemMessage sysSystemMessage = baseMapper.selectById(id);
        this.initSystemMessage(sysSystemMessage);
        return sysSystemMessage;
    }

    private  void  initSystemMessages(List<SysSystemMessage> list){
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> systemMessageIds = new ArrayList<>(list.size());
            for(SysSystemMessage sysSystemMessage:list){
                systemMessageIds.add(sysSystemMessage.getSystemMessageId());
            }

            SysSystemMessageReadParam p = new SysSystemMessageReadParam();
            p.setSystemMessageIds(systemMessageIds);
            List<Map<String, Object>> _integerIntegerMap = sysSystemMessageReadMapper._selectCount(p);
            Map<Integer,Integer> totalMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(_integerIntegerMap)){
                for(Map<String, Object> map:_integerIntegerMap){
                    totalMap.put(Integer.valueOf(map.get("systemMessageId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            p.setHasRead(1);
            List<Map<String, Object>> integerIntegerMap = sysSystemMessageReadMapper._selectCount(p);
            Map<Integer,Integer> readMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(integerIntegerMap)){
                for(Map<String, Object> map:integerIntegerMap){
                    readMap.put(Integer.valueOf(map.get("systemMessageId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            for(SysSystemMessage sysSystemMessage:list){
                Integer num = readMap.get(sysSystemMessage.getSystemMessageId());
                if(num == null){
                    num = 0;
                }
                Integer integer = totalMap.get(sysSystemMessage.getSystemMessageId());
                if(integer == null){
                    integer = 0;
                }
                sysSystemMessage.setReadTotal(num+" / "+integer);
            }
        }
    }

    private  void  initSystemMessage(SysSystemMessage item){
        if(item !=  null){
            List<SysSystemMessageReceive> sysSystemMessageReceiveList = sysSystemMessageReceiveMapper.selectList(new LambdaQueryWrapper<SysSystemMessageReceive>()
                    .select(SysSystemMessageReceive::getAddNewly,SysSystemMessageReceive::getReceiveId,SysSystemMessageReceive::getReceiveType)
                    .eq(SysSystemMessageReceive::getSystemMessageId, item.getSystemMessageId()));
            if(!CollectionUtils.isEmpty(sysSystemMessageReceiveList)){
                List<Integer> tenantIds = new ArrayList<>();
                List<Integer> userIds = new ArrayList<>();
                List<Integer> defaultRoleIds = new ArrayList<>();
                for(SysSystemMessageReceive sysSystemMessageReceive:sysSystemMessageReceiveList){
                    if("2".equals(sysSystemMessageReceive.getReceiveType())){
                        tenantIds.add(sysSystemMessageReceive.getReceiveId());
                    }else if("3".equals(sysSystemMessageReceive.getReceiveType())){
                        defaultRoleIds.add(sysSystemMessageReceive.getReceiveId());
                    }else if("4".equals(sysSystemMessageReceive.getReceiveType())){
                        userIds.add(sysSystemMessageReceive.getReceiveId());
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

                for(SysSystemMessageReceive sysSystemMessageReceive:sysSystemMessageReceiveList){
                    if("1".equals(sysSystemMessageReceive.getReceiveType())){
                        sysSystemMessageReceive.setReceiveName("全部");
                    }else if("2".equals(sysSystemMessageReceive.getReceiveType())){
                        Tenant tenant = tenantMap.get(sysSystemMessageReceive.getReceiveId());
                        if(tenant != null){
                            sysSystemMessageReceive.setReceiveName(tenant.getTenantFullName());
                        }
                    }else if("3".equals(sysSystemMessageReceive.getReceiveType())){
                        DefaultRole role = defaultRoleMap.get(sysSystemMessageReceive.getReceiveId());
                        if(role != null){
                            sysSystemMessageReceive.setReceiveName(CommonUtil.stringJoiner(role.getTenantType(),role.getRoleName()));
                        }
                    }else if("4".equals(sysSystemMessageReceive.getReceiveType())){
                        User user = userMap.get(sysSystemMessageReceive.getReceiveId());
                        if(user !=null){
                            sysSystemMessageReceive.setReceiveName(CommonUtil.stringJoiner(user.getUsername(),user.getRealname()));
                        }
                    }
                }
            }
            item.setSysSystemMessageReceiveList(sysSystemMessageReceiveList);
        }
    }

    @Override
    @Transactional
    public void saveSystemMessage(SysSystemMessage sysSystemMessage) {
        if(CollectionUtils.isEmpty(sysSystemMessage.getSysSystemMessageReceiveList())){
            throw new BusinessException("发布范围不能为空");
        }
        int insert = baseMapper.insert(sysSystemMessage);
        if(insert>0){
            for(SysSystemMessageReceive sysSystemMessageReceive:sysSystemMessage.getSysSystemMessageReceiveList()){
                sysSystemMessageReceive.setSystemMessageId(sysSystemMessage.getSystemMessageId());
                sysSystemMessageReceiveMapper.insert(sysSystemMessageReceive);
            }
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void updateSystemMessage(SysSystemMessage sysSystemMessage) {
        if(sysSystemMessage.getSystemMessageId() == null){
            throw new BusinessException("主键ID不能为空");
        }
        int update = baseMapper.update(sysSystemMessage, new LambdaUpdateWrapper<SysSystemMessage>()
                .set(SysSystemMessage::getTargetUrl,sysSystemMessage.getTargetUrl())
                .set(SysSystemMessage::getSystemMessagePicture, sysSystemMessage.getSystemMessagePicture())
                .eq(SysSystemMessage::getSystemMessageId, sysSystemMessage.getSystemMessageId()));
        if(update>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    @Transactional
    public void deleteSystemMessage(List<Integer> idList) {

        sysSystemMessageReadMapper.delete(new LambdaQueryWrapper<SysSystemMessageRead>().in(SysSystemMessageRead::getSystemMessageId,idList));

        sysSystemMessageReceiveMapper.delete(new LambdaQueryWrapper<SysSystemMessageReceive>().in(SysSystemMessageReceive::getSystemMessageId,idList));

        int num = baseMapper.deleteBatchIds(idList);
        if(num>0){
            //删除缓存
            this.delRedis();
        }
    }

    private void delRedis(){
        redisUtil.dels(configProperties.getSystemMessagePrefix(),false);
    }
}