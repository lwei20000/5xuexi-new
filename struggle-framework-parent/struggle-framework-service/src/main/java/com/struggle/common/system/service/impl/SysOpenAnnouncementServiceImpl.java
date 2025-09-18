package com.struggle.common.system.service.impl;

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
import com.struggle.common.system.entity.SysOpenAnnouncement;
import com.struggle.common.system.mapper.SysOpenAnnouncementMapper;
import com.struggle.common.system.param.SysOpenAnnouncementParam;
import com.struggle.common.system.service.ISysOpenAnnouncementService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @ClassName: SysOpenAnnouncementService
 * @Description:  通知公告-ServiceImpl层
 * @author xsk
 */
@Service
public class SysOpenAnnouncementServiceImpl extends ServiceImpl<SysOpenAnnouncementMapper, SysOpenAnnouncement> implements  ISysOpenAnnouncementService {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private ConfigProperties configProperties;


    @Override
    public PageResult<SysOpenAnnouncement> pageRel(SysOpenAnnouncementParam param) {
        PageParam<SysOpenAnnouncement, SysOpenAnnouncementParam> page = new PageParam<>(param);
        page.setDefaultOrder("top_timestamp desc,update_time desc");
        QueryWrapper<SysOpenAnnouncement> wrapper = page.getWrapper();
        wrapper.select("open_announcement_id","title","open_announcement_type","top_timestamp","create_time","update_time");
        IPage<SysOpenAnnouncement> pageList = baseMapper.selectPage(page, wrapper);
        List<SysOpenAnnouncement> records = pageList.getRecords();
        if(!CollectionUtils.isEmpty(records)){
            for(SysOpenAnnouncement sysOpenAnnouncement:records){
                if(sysOpenAnnouncement.getTopTimestamp()>0){
                    sysOpenAnnouncement.setTopTimestamp(1L);
                }
            }
        }
        return new PageResult<>(records, page.getTotal());
    }

    @Override
    public SysOpenAnnouncement getDetailById(Integer id) {
        SysOpenAnnouncement sysOpenAnnouncement = baseMapper.selectById(id);
        if(sysOpenAnnouncement != null){
            if(sysOpenAnnouncement.getTopTimestamp()>0){
                sysOpenAnnouncement.setTopTimestamp(1L);
            }
        }
        return sysOpenAnnouncement;
    }

    @Override
    public void saveAnnouncement(SysOpenAnnouncement sysAnnouncement) {
        int insert = baseMapper.insert(sysAnnouncement);
        if(insert>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void updateAnnouncement(SysOpenAnnouncement sysAnnouncement) {
        if(sysAnnouncement.getOpenAnnouncementId() == null){
            throw new BusinessException("主键ID不能为空");
        }
        sysAnnouncement.setTopTimestamp(null);
        int insert =baseMapper.update(sysAnnouncement, new LambdaUpdateWrapper<SysOpenAnnouncement>()
                .set(SysOpenAnnouncement::getOpenAnnouncementAttachment, sysAnnouncement.getOpenAnnouncementAttachment())
                .set(SysOpenAnnouncement::getOpenAnnouncementType, sysAnnouncement.getOpenAnnouncementType())
                .set(SysOpenAnnouncement::getOpenAnnouncementPicture, sysAnnouncement.getOpenAnnouncementPicture())
                .eq(SysOpenAnnouncement::getOpenAnnouncementId, sysAnnouncement.getOpenAnnouncementId()));
        if(insert>0){
            //删除缓存
            this.delRedis();
        }
    }

    @Override
    public void deleteAnnouncement(List<Integer> idList) {
         int insert = baseMapper.deleteBatchIds(idList);
        if(insert>0){
            //删除缓存
          this.delRedis();
        }
    }

    private void delRedis(){
        redisUtil.dels(CommonUtil.stringJoiner(configProperties.getOpenAnnouncementPrefix(),ThreadLocalContextHolder.getTenant().toString()),false);
    }
}