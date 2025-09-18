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
import com.struggle.common.system.entity.SysSystemAnnouncement;
import com.struggle.common.system.entity.SysSystemAnnouncementRead;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.param.SysSystemAnnouncementReceiveParam;
import com.struggle.common.system.service.ISysSystemAnnouncementReadService;
import com.struggle.common.system.service.ISysSystemAnnouncementReceiveService;
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
 * @ClassName: ComSystemAnnouncementController
 * @Description: 系统通知公告-controller层
 * @author xsk
 */
@Tag(name = "COM系统通知公告",description = "ComSystemAnnouncementController")
@RestController
@RequestMapping("/api/system/system_announcement")
public class ComSystemAnnouncementController extends BaseController{
    @Resource
    private ISysSystemAnnouncementReceiveService sysSystemAnnouncementReceiveService;
    @Resource
    private ISysSystemAnnouncementReadService sysSystemAnnouncementReadService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedissonClient redissonClient;

    @OperationLog
    @Operation(method="page",description= "分页查询公告")
    @GetMapping("/page")
    public ApiResult<PageResult<SysSystemAnnouncement>> page(SysSystemAnnouncementReceiveParam param) {
        User user = ThreadLocalContextHolder.getUser();
        param.setUserId(user.getUserId());
        param.setUserCreateTime(user.getCreateTime());
        param.setDetail(true);
        return success(sysSystemAnnouncementReceiveService.pageRel(param));
    }

    @OperationLog
    @Operation(method="detail",description= "查看公告")
    @GetMapping("/{id}")
    public ApiResult<SysSystemAnnouncement> detail(@PathVariable("id") Integer id) {
        User user = ThreadLocalContextHolder.getUser();
        SysSystemAnnouncementReceiveParam param = new SysSystemAnnouncementReceiveParam();
        param.setSystemAnnouncementId(id);
        param.setUserId(user.getUserId());
        param.setUserCreateTime(user.getCreateTime());
        SysSystemAnnouncement sysSystemAnnouncement = sysSystemAnnouncementReceiveService.get(param);
        if(sysSystemAnnouncement != null){
            this.read(sysSystemAnnouncement.getSystemAnnouncementId(), user);
        }
        return success(sysSystemAnnouncement);
    }

    private void read(Integer announcementId, User user) {
        Integer userId = user.getUserId();
        String _key = CommonUtil.stringJoiner("system_announcement",announcementId.toString(),userId.toString());
        RLock lock = redissonClient.getLock(_key);
        try {
            // 拿锁失败时会不停的重试
            // 没有Watch Dog ，获取锁后10s自动释放,防止死锁
            lock.lock(10, TimeUnit.SECONDS);
            Long aLong = sysSystemAnnouncementReadService.count(new LambdaQueryWrapper<SysSystemAnnouncementRead>().eq(SysSystemAnnouncementRead::getSystemAnnouncementId, announcementId).eq(SysSystemAnnouncementRead::getUserId, userId));
            if(aLong == 0){
                SysSystemAnnouncementRead sysSystemAnnouncementRead = new SysSystemAnnouncementRead();
                sysSystemAnnouncementRead.setSystemAnnouncementId(announcementId);
                sysSystemAnnouncementRead.setUserId(userId);
                sysSystemAnnouncementReadService.save(sysSystemAnnouncementRead);
                //删除缓存
                redisUtil.del(CommonUtil.stringJoiner(configProperties.getSystemAnnouncementPrefix(),userId.toString()));
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