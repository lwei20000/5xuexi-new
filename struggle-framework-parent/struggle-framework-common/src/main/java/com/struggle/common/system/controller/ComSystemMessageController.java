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
import com.struggle.common.system.entity.SysSystemMessage;
import com.struggle.common.system.entity.SysSystemMessageRead;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.SysSystemMessageReceiveParam;
import com.struggle.common.system.service.ISysSystemMessageReadService;
import com.struggle.common.system.service.ISysSystemMessageReceiveService;
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
 * @ClassName: ComSystemMessageController
 * @Description: 系统消息-controller层
 * @author xsk
 */
@Tag(name = "COM系统消息",description = "ComSystemMessageController")
@RestController
@RequestMapping("/api/system/system_message")
public class ComSystemMessageController extends BaseController{
    @Resource
    private ISysSystemMessageReceiveService sysSystemMessageReceiveService;
    @Resource
    private ISysSystemMessageReadService sysSystemMessageReadService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedissonClient redissonClient;

    @OperationLog
    @Operation(method="page",description="分页查询公告")
    @GetMapping("/page")
    public ApiResult<PageResult<SysSystemMessage>> page(SysSystemMessageReceiveParam param) {
        User user = ThreadLocalContextHolder.getUser();
        param.setUserId(user.getUserId());
        param.setUserCreateTime(user.getCreateTime());
        param.setDetail(true);
        return success(sysSystemMessageReceiveService.pageRel(param));
    }

    @OperationLog
    @Operation(method="detail",description= "查看消息")
    @GetMapping("/{id}")
    public ApiResult<SysSystemMessage> detail(@PathVariable("id") Integer id) {
        User user = ThreadLocalContextHolder.getUser();
        SysSystemMessageReceiveParam param = new SysSystemMessageReceiveParam();
        param.setSystemMessageId(id);
        param.setUserId(user.getUserId());
        param.setUserCreateTime(user.getCreateTime());
        SysSystemMessage sysSystemMessage = sysSystemMessageReceiveService.get(param);
        if(sysSystemMessage != null){
            this.read(sysSystemMessage.getSystemMessageId(), user);
        }
        return success(sysSystemMessage);
    }

    private void read(Integer messageId, User user) {
        Integer userId = user.getUserId();
        String _key = CommonUtil.stringJoiner("system_message",messageId.toString(),userId.toString());
        RLock lock = redissonClient.getLock(_key);
        try {
            // 拿锁失败时会不停的重试
            // 没有Watch Dog ，获取锁后10s自动释放,防止死锁
            lock.lock(10, TimeUnit.SECONDS);
            Long aLong = sysSystemMessageReadService.count(new LambdaQueryWrapper<SysSystemMessageRead>()
                    .eq(SysSystemMessageRead::getSystemMessageId, messageId)
                    .eq(SysSystemMessageRead::getUserId, userId));
            if(aLong == 0){
                SysSystemMessageRead sysSystemMessageRead = new SysSystemMessageRead();
                sysSystemMessageRead.setSystemMessageId(messageId);
                sysSystemMessageRead.setUserId(userId);
                sysSystemMessageReadService.save(sysSystemMessageRead);
                //删除缓存
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemMessagePrefix(),userId.toString()));
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