package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysMessage;
import com.struggle.common.system.entity.SysMessageRead;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.SysMessageReceiveParam;
import com.struggle.common.system.service.ISysMessageReadService;
import com.struggle.common.system.service.ISysMessageReceiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 *
 * @ClassName: SysMessageController
 * @Description: 消息-controller层
 * @author xsk
 */
@Tag(name = "COM消息",description = "ComMessageController")
@RestController
@RequestMapping("/api/system/message")
public class ComMessageController extends BaseController{
    @Resource
    private ISysMessageReceiveService sysMessageReceiveService;
    @Resource
    private ISysMessageReadService sysMessageReadService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedissonClient redissonClient;

    @OperationLog
    @Operation(method="page",description="分页查询公告")
    @GetMapping("/page")
    public ApiResult<PageResult<SysMessage>> page(SysMessageReceiveParam param) {
        User user = ThreadLocalContextHolder.getUser();
        param.setUserId(user.getUserId());
        param.setUserTenantCreateTime(user.getUserTenantCreateTime());
        param.setDetail(true);
        return success(sysMessageReceiveService.pageRel(param));
    }

    @OperationLog
    @Operation(method="detail",description= "查看消息")
    @GetMapping("/{id}")
    public ApiResult<SysMessage> detail(@PathVariable("id") Integer id) {
        User user = ThreadLocalContextHolder.getUser();
        SysMessageReceiveParam param = new SysMessageReceiveParam();
        param.setMessageId(id);
        param.setUserId(user.getUserId());
        param.setUserTenantCreateTime(user.getUserTenantCreateTime());
        SysMessage sysMessage = sysMessageReceiveService.get(param);
        if(sysMessage != null){
            this.read(sysMessage.getMessageId(), user,ThreadLocalContextHolder.getTenant());
        }
        return success(sysMessage);
    }

    private void read(Integer messageId, User user, Integer tenantId) {
        Integer userId = user.getUserId();
        String _key = CommonUtil.stringJoiner("message",messageId.toString(),tenantId.toString(),userId.toString());
        RLock lock = redissonClient.getLock(_key);
        try {
            // 拿锁失败时会不停的重试
            // 没有Watch Dog ，获取锁后10s自动释放,防止死锁
            lock.lock(10, TimeUnit.SECONDS);
            Long aLong = sysMessageReadService.count(new LambdaQueryWrapper<SysMessageRead>()
                    .eq(SysMessageRead::getMessageId, messageId)
                    .eq(SysMessageRead::getUserId, userId));
            if(aLong == 0){
                SysMessageRead sysMessageRead = new SysMessageRead();
                sysMessageRead.setMessageId(messageId);
                sysMessageRead.setUserId(userId);
                sysMessageReadService.save(sysMessageRead);
                //删除缓存
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getMessagePrefix(),tenantId.toString(),userId.toString()));
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
}