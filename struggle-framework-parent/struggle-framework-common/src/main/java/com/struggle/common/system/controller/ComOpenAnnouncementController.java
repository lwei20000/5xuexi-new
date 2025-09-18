package com.struggle.common.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.struggle.common.core.ThreadLocalContextHolder;
import com.struggle.common.core.annotation.NoLoginCheck;
import com.struggle.common.core.config.ConfigProperties;
import com.struggle.common.core.utils.CommonUtil;
import com.struggle.common.core.utils.JSONUtil;
import com.struggle.common.core.utils.RedisUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.core.web.PageParam;
import com.struggle.common.core.web.PageResult;
import com.struggle.common.system.entity.SysOpenAnnouncement;
import com.struggle.common.system.param.SysOpenAnnouncementParam;
import com.struggle.common.system.service.ISysOpenAnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 *
 * @ClassName: SysOpenAnnouncementController
 * @Description: 公开通知公告-controller层
 * @author xsk
 */
@Tag(name = "COM公开通知公告",description = "ComOpenAnnouncementController")
@RestController
@RequestMapping("/api/system/open-announcement")
public class ComOpenAnnouncementController extends BaseController {
    @Resource
    private ISysOpenAnnouncementService sysOpenAnnouncementService;
    @Resource
    private ConfigProperties configProperties;
    @Resource
    private RedisUtil redisUtil;

    @Operation(method="page",description="用户端_分页查询公开公告")
    @GetMapping("/page")
    @NoLoginCheck
    public ApiResult<PageResult<SysOpenAnnouncement>> page(SysOpenAnnouncementParam param) throws JsonProcessingException {
        if(param.getTenantId() == null){
            ThreadLocalContextHolder.setTenant(configProperties.getDefaultTenant());
        }else{
            ThreadLocalContextHolder.setTenant(param.getTenantId());
        }

        String open_notice_key = CommonUtil.stringJoiner(configProperties.getOpenAnnouncementPrefix(),ThreadLocalContextHolder.getTenant().toString(),param.getPage().toString(),param.getLimit().toString(),param.getOpenAnnouncementType());
        String open_notice_msg = redisUtil.get(open_notice_key);
        if(StringUtils.hasText(open_notice_msg)){
            PageResult<SysOpenAnnouncement> pageList = JSONUtil.getObjectMapper().readValue(open_notice_msg,new TypeReference<PageResult<SysOpenAnnouncement>>() {});
            return success(pageList);
        }

        PageParam<SysOpenAnnouncement, SysOpenAnnouncementParam> page = new PageParam<>(param);
        page.setDefaultOrder("top_timestamp desc,update_time desc");
        QueryWrapper<SysOpenAnnouncement> wrapper = page.getWrapper();
        wrapper.select("open_announcement_id,title,open_announcement_type,top_timestamp,create_time,update_time");
        IPage<SysOpenAnnouncement> pageList = sysOpenAnnouncementService.page(page, wrapper);
        PageResult<SysOpenAnnouncement>  sysOpenAnnouncementPageResult = new PageResult<>(pageList.getRecords(), page.getTotal());
        open_notice_msg = JSONUtil.toJSONString(sysOpenAnnouncementPageResult);
        //加入缓存
        redisUtil.set(open_notice_key,open_notice_msg,configProperties.getTokenExpireTime());

        return success(sysOpenAnnouncementPageResult);
    }

    @Operation(method="detail",description= "用户端_查看公开公告")
    @GetMapping(value = "/{id}")
    @NoLoginCheck
    public ApiResult<SysOpenAnnouncement> detail(@PathVariable("id") Integer id) {
        ThreadLocalContextHolder.setCloseTenant(true);
        return success(sysOpenAnnouncementService.getById(id));
    }
}