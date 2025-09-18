package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.*;
import com.struggle.common.system.param.SysAnnouncementReceiveParam;
import com.struggle.common.system.param.SysMessageReceiveParam;
import com.struggle.common.system.param.SysSystemAnnouncementReceiveParam;
import com.struggle.common.system.param.SysSystemMessageReceiveParam;
import com.struggle.common.system.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * @ClassName: SysAnnouncementController
 * @Description: 通知公告-controller层
 * @author xsk
 */
@Tag(name = "COM内部通知公告",description = "ComAnnouncementController")
@RestController
@RequestMapping("/api/system/announcement")
public class ComAnnouncementController extends BaseController{
    @Resource
    private ISysAnnouncementReceiveService sysAnnouncementReceiveService;
    @Resource
    private ISysAnnouncementReadService sysAnnouncementReadService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private ISysMessageReceiveService sysMessageReceiveService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private ISysSystemAnnouncementReceiveService sysSystemAnnouncementReceiveService;
    @Resource
    private ISysSystemMessageReceiveService sysSystemMessageReceiveService;

    @OperationLog
    @Operation(method="page",description= "分页查询公告")
    @GetMapping("/page")
    public ApiResult<PageResult<SysAnnouncement>> page(SysAnnouncementReceiveParam param) {
        User user = ThreadLocalContextHolder.getUser();
        param.setUserId(user.getUserId());
        param.setUserTenantCreateTime(user.getUserTenantCreateTime());
        param.setDetail(true);
        return success(sysAnnouncementReceiveService.pageRel(param));
    }

    @OperationLog
    @Operation(method="detail",description= "查看公告")
    @GetMapping("/{id}")
    public ApiResult<SysAnnouncement> detail(@PathVariable("id") Integer id) {
        User user = ThreadLocalContextHolder.getUser();
        SysAnnouncementReceiveParam param = new SysAnnouncementReceiveParam();
        param.setAnnouncementId(id);
        param.setUserId(user.getUserId());
        param.setUserTenantCreateTime(user.getUserTenantCreateTime());
        SysAnnouncement sysAnnouncement = sysAnnouncementReceiveService.get(param);
        if(sysAnnouncement != null){
            this.read(sysAnnouncement.getAnnouncementId(), user,ThreadLocalContextHolder.getTenant());
        }
        return success(sysAnnouncement);
    }

    private void read(Integer announcementId, User user, Integer tenantId) {
        Integer userId = user.getUserId();
        String _key = CommonUtil.stringJoiner("announcement",announcementId.toString(),tenantId.toString(),userId.toString());
        RLock lock = redissonClient.getLock(_key);
        try {
            // 拿锁失败时会不停的重试
            // 没有Watch Dog ，获取锁后10s自动释放,防止死锁
            lock.lock(10, TimeUnit.SECONDS);
            Long aLong = sysAnnouncementReadService.count(new LambdaQueryWrapper<SysAnnouncementRead>().eq(SysAnnouncementRead::getAnnouncementId, announcementId).eq(SysAnnouncementRead::getUserId, userId));
            if(aLong == 0){
                SysAnnouncementRead sysAnnouncementRead = new SysAnnouncementRead();
                sysAnnouncementRead.setAnnouncementId(announcementId);
                sysAnnouncementRead.setUserId(userId);
                sysAnnouncementReadService.save(sysAnnouncementRead);
                //删除缓存
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getAnnouncementPrefix(),tenantId.toString(),userId.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //lock.isHeldByCurrentThread() 判断当前线程是否持有锁
            if(null != lock && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    @Operation(method="getUnreadNotice",description="获取未读通知、消息、待办")
    @GetMapping("/getUnreadNotice")
    public ApiResult<?> getUnreadNotice() {
        Map<String,Object> map = new HashMap<>();
        User user = ThreadLocalContextHolder.getUser();
        if(user != null){
            //租户通知
            String notice_key = CommonUtil.stringJoiner(configProperties.getAnnouncementPrefix(),ThreadLocalContextHolder.getTenant().toString(),user.getUserId().toString());
            String notice_msg = redisUtil.get(notice_key);
            if(StringUtils.hasText(notice_msg)){
                map.put("notice",notice_msg);
            }else{
                SysAnnouncementReceiveParam param = new SysAnnouncementReceiveParam();
                param.setUserId(user.getUserId());
                param.setUserTenantCreateTime(user.getUserTenantCreateTime());
                param.setLimit(5L);
                param.setHasRead(0);
                PageResult<SysAnnouncement> sysAnnouncementPageResult = sysAnnouncementReceiveService.pageRel(param);
                notice_msg = JSONUtil.toJSONString(sysAnnouncementPageResult);
                //加入缓存
                redisUtil.set(notice_key,notice_msg,configProperties.getTokenExpireTime());
                map.put("notice",notice_msg);
            }

            //系统通知
            String system_notice_key = CommonUtil.stringJoiner(configProperties.getSystemAnnouncementPrefix(),user.getUserId().toString());
            String system_notice_msg = redisUtil.get(system_notice_key);
            if(StringUtils.hasText(system_notice_msg)){
                map.put("system_notice",system_notice_msg);
            }else{
                SysSystemAnnouncementReceiveParam param = new SysSystemAnnouncementReceiveParam();
                param.setUserId(user.getUserId());
                param.setUserCreateTime(user.getCreateTime());
                param.setLimit(5L);
                param.setHasRead(0);
                PageResult<SysSystemAnnouncement> sysSystemAnnouncementPageResult = sysSystemAnnouncementReceiveService.pageRel(param);
                system_notice_msg = JSONUtil.toJSONString(sysSystemAnnouncementPageResult);
                //加入缓存
                redisUtil.set(system_notice_key,system_notice_msg,configProperties.getTokenExpireTime());
                map.put("system_notice",system_notice_msg);
            }

            //租户消息
            String message_key = CommonUtil.stringJoiner(configProperties.getMessagePrefix(),ThreadLocalContextHolder.getTenant().toString(),user.getUserId().toString());
            String message_msg = redisUtil.get(message_key);
            if(StringUtils.hasText(message_msg)){
                map.put("message",message_msg);
            }else{
                SysMessageReceiveParam param = new SysMessageReceiveParam();
                param.setUserId(user.getUserId());
                param.setUserTenantCreateTime(user.getUserTenantCreateTime());
                param.setLimit(5L);
                param.setHasRead(0);
                PageResult<SysMessage> sysMessagePageResult = sysMessageReceiveService.pageRel(param);
                message_msg = JSONUtil.toJSONString(sysMessagePageResult);
                //加入缓存
                redisUtil.set(message_key,message_msg,configProperties.getTokenExpireTime());
                map.put("message",message_msg);
            }

            //系统消息
            String system_message_key = CommonUtil.stringJoiner(configProperties.getSystemMessagePrefix(),user.getUserId().toString());
            String system_message_msg = redisUtil.get(system_message_key);
            if(StringUtils.hasText(system_message_msg)){
                map.put("system_message",system_message_msg);
            }else{
                SysSystemMessageReceiveParam param = new SysSystemMessageReceiveParam();
                param.setUserId(user.getUserId());
                param.setUserCreateTime(user.getCreateTime());
                param.setLimit(5L);
                param.setHasRead(0);
                PageResult<SysSystemMessage> sysSystemMessagePageResult = sysSystemMessageReceiveService.pageRel(param);
                system_message_msg = JSONUtil.toJSONString(sysSystemMessagePageResult);
                //加入缓存
                redisUtil.set(system_message_key,system_message_msg,configProperties.getTokenExpireTime());
                map.put("system_message",system_message_msg);
            }
        }
        return success("成功",map);
    }
}