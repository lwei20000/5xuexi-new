package com.struggle.common.system.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.struggle.common.system.entity.EmailRecord;

/**
 * 邮件记录Mapper
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@DS("system")
public interface EmailRecordMapper extends BaseMapper<EmailRecord> {

}
