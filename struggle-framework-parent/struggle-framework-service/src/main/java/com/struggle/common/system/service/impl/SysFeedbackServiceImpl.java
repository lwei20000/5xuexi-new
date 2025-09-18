package com.struggle.common.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.Org;
import com.struggle.common.system.entity.SysAnnouncement;
import com.struggle.common.system.entity.SysFeedback;
import com.struggle.common.system.entity.User;
import com.struggle.common.system.mapper.SysFeedbackMapper;
import com.struggle.common.system.param.SysAnnouncementParam;
import com.struggle.common.system.param.SysFeedbackParam;
import com.struggle.common.system.service.ISysFeedbackService;
import com.struggle.common.system.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName: SysFeedbackService
 * @Description:  意见反馈-ServiceImpl层
 * @author xsk
 */
@Service
public class SysFeedbackServiceImpl extends ServiceImpl<SysFeedbackMapper, SysFeedback> implements  ISysFeedbackService {

    @Resource
    private UserService userService;

    @Override
    public PageResult<SysFeedback> pageRel(SysFeedbackParam param,boolean detail) {
        PageParam<SysFeedback, SysFeedbackParam> page = new PageParam<>(param);
        page.setDefaultOrder("update_time desc");
        IPage<SysFeedback> pageList = baseMapper.selectPage(page, page.getWrapper());
        if(detail){
            this.initFeedback(pageList.getRecords());
        }
        return new PageResult<>(pageList.getRecords(), page.getTotal());
    }

    private void initFeedback(List<SysFeedback> sysFeedbacks) {
        if (sysFeedbacks != null && sysFeedbacks.size() > 0) {
            List<Integer> userIds = new ArrayList<>(sysFeedbacks.size());
            for(SysFeedback sysFeedback:sysFeedbacks){
                userIds.add(sysFeedback.getUserId());
                if(sysFeedback.getReplyUserId() !=null){
                    userIds.add(sysFeedback.getReplyUserId());
                }
            }
            Map<Integer, User> userMap = new HashMap<>();
            List<User> users = userService.list(new LambdaQueryWrapper<User>()
                    .select(User::getUserId,User::getRealname,User::getUsername).in(User::getUserId,userIds));
            if(!CollectionUtils.isEmpty(users)){
                for(User user:users){
                    userMap.put(user.getUserId(),user);
                }
            }
            for(SysFeedback sysFeedback:sysFeedbacks){
                sysFeedback.setUser(userMap.get(sysFeedback.getUserId()));
                if(sysFeedback.getReplyUserId() !=null){
                    sysFeedback.setReplyUser(userMap.get(sysFeedback.getReplyUserId()));
                }
            }
        }
    }
}