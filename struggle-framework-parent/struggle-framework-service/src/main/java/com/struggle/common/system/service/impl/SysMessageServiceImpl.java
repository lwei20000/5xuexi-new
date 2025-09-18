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
import com.struggle.common.system.mapper.SysMessageMapper;
import com.struggle.common.system.mapper.SysMessageReadMapper;
import com.struggle.common.system.mapper.SysMessageReceiveMapper;
import com.struggle.common.system.param.SysMessageParam;
import com.struggle.common.system.param.SysMessageReadParam;
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
 * @ClassName: SysMessageService
 * @Description:  通知公告-ServiceImpl层
 * @author xsk
 */
@Service
public class SysMessageServiceImpl extends ServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    @Resource
    private SysMessageReadMapper sysMessageReadMapper;
    @Resource
    private SysMessageReceiveMapper sysMessageReceiveMapper;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private OrgService orgService;
    @Resource
    private GroupService groupService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserService userService;

    @Override
    public PageResult<SysMessage> pageRel(SysMessageParam param) {
        PageParam<SysMessage, SysMessageParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        QueryWrapper<SysMessage> wrapper = page.getWrapper();
        wrapper.select("message_id","title","create_time","update_time");
        IPage<SysMessage> pageList = baseMapper.selectPage(page, wrapper);
        this.initMessages(pageList.getRecords());
        return new PageResult<>(pageList.getRecords(), page.getTotal());
    }

    @Override
    public SysMessage getDetailById(Integer id) {
        SysMessage sysMessage = baseMapper.selectById(id);
        this.initMessage(sysMessage);
        return sysMessage;
    }

    private  void  initMessage(SysMessage item){
        if(item != null){
            List<SysMessageReceive> sysMessageReceiveList = sysMessageReceiveMapper.selectList(new LambdaQueryWrapper<SysMessageReceive>()
                    .select(SysMessageReceive::getAddNewly,SysMessageReceive::getReceiveId,SysMessageReceive::getReceiveType)
                    .eq(SysMessageReceive::getMessageId, item.getMessageId()));
            if(!CollectionUtils.isEmpty(sysMessageReceiveList)){
                List<Integer> orgIds = new ArrayList<>();
                List<Integer> groupIds = new ArrayList<>();
                List<Integer> userIds = new ArrayList<>();
                List<Integer> roleIds = new ArrayList<>();
                for(SysMessageReceive sysMessageReceive:sysMessageReceiveList){
                    if("2".equals(sysMessageReceive.getReceiveType())){
                        orgIds.add(sysMessageReceive.getReceiveId());
                    }else if("3".equals(sysMessageReceive.getReceiveType())){
                        roleIds.add(sysMessageReceive.getReceiveId());
                    }else if("4".equals(sysMessageReceive.getReceiveType())){
                        userIds.add(sysMessageReceive.getReceiveId());
                    }else if("5".equals(sysMessageReceive.getReceiveType())){
                        groupIds.add(sysMessageReceive.getReceiveId());
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

                for(SysMessageReceive sysMessageReceive:sysMessageReceiveList){
                    if("1".equals(sysMessageReceive.getReceiveType())){
                        sysMessageReceive.setReceiveName("全部");
                    }else if("2".equals(sysMessageReceive.getReceiveType())){
                        Org org = orgMap.get(sysMessageReceive.getReceiveId());
                        if(org != null){
                            sysMessageReceive.setReceiveName(org.getOrgName());
                        }
                    }else if("3".equals(sysMessageReceive.getReceiveType())){
                        Role role = roleMap.get(sysMessageReceive.getReceiveId());
                        if(role != null){
                            sysMessageReceive.setReceiveName(role.getRoleName());
                        }
                    }else if("4".equals(sysMessageReceive.getReceiveType())){
                        User user = userMap.get(sysMessageReceive.getReceiveId());
                        if(user !=null){
                            sysMessageReceive.setReceiveName(CommonUtil.stringJoiner(user.getUsername(),user.getRealname()));
                        }
                    }else if("5".equals(sysMessageReceive.getReceiveType())){
                        Group group = groupMap.get(sysMessageReceive.getReceiveId());
                        if(group !=null){
                            sysMessageReceive.setReceiveName(group.getGroupName());
                        }
                    }
                }
            }

            item.setSysMessageReceiveList(sysMessageReceiveList);
        }
    }

    private  void  initMessages(List<SysMessage> list){
        if(!CollectionUtils.isEmpty(list)){
            List<Integer> messageIds = new ArrayList<>(list.size());
            for(SysMessage sysMessage:list){
                messageIds.add(sysMessage.getMessageId());
            }

            SysMessageReadParam p = new SysMessageReadParam();
            p.setMessageIds(messageIds);
            List<Map<String, Object>> _integerIntegerMap = sysMessageReadMapper._selectCount(p);
            Map<Integer,Integer> totalMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(_integerIntegerMap)){
                for(Map<String, Object> map:_integerIntegerMap){
                    totalMap.put(Integer.valueOf(map.get("messageId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            p.setHasRead(1);
            List<Map<String, Object>> integerIntegerMap = sysMessageReadMapper._selectCount(p);
            Map<Integer,Integer> readMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(integerIntegerMap)){
                for(Map<String, Object> map:integerIntegerMap){
                    readMap.put(Integer.valueOf(map.get("messageId").toString()),Integer.valueOf(map.get("num").toString()));
                }
            }

            for(SysMessage sysMessage:list){
                Integer num = readMap.get(sysMessage.getMessageId());
                if(num == null){
                    num = 0;
                }
                Integer integer = totalMap.get(sysMessage.getMessageId());
                if(integer == null){
                    integer = 0;
                }
                sysMessage.setReadTotal(num+" / "+integer);
            }
        }
    }

    @Override
    @Transactional
    public void saveMessage(SysMessage sysMessage) {
        if(CollectionUtils.isEmpty(sysMessage.getSysMessageReceiveList())){
            throw new BusinessException("发布范围不能为空");
        }
        int insert = baseMapper.insert(sysMessage);
        if(insert>0){
            for(SysMessageReceive sysMessageReceive:sysMessage.getSysMessageReceiveList()){
                sysMessageReceive.setMessageId(sysMessage.getMessageId());
                sysMessageReceiveMapper.insert(sysMessageReceive);
            }
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void updateMessage(SysMessage sysMessage) {
        if(sysMessage.getMessageId() == null){
            throw new BusinessException("主键ID不能为空");
        }
        int update = baseMapper.update(sysMessage, new LambdaUpdateWrapper<SysMessage>()
                .set(SysMessage::getTargetUrl,sysMessage.getTargetUrl())
                .set(SysMessage::getMessagePicture, sysMessage.getMessagePicture())
                .eq(SysMessage::getMessageId, sysMessage.getMessageId()));
        if(update>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    @Transactional
    public void deleteMessage(List<Integer> idList) {

        sysMessageReadMapper.delete(new LambdaQueryWrapper<SysMessageRead>().in(SysMessageRead::getMessageId,idList));

        sysMessageReceiveMapper.delete(new LambdaQueryWrapper<SysMessageReceive>().in(SysMessageReceive::getMessageId,idList));

        int num = baseMapper.deleteBatchIds(idList);
        if(num>0){
            //删除缓存
            this.delRedis();
        }
    }

    private void delRedis(){
        redisUtil.dels(CommonUtil.stringJoiner(configProperties.getMessagePrefix(),ThreadLocalContextHolder.getTenant().toString()),false);
    }
}