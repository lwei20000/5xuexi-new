package com.struggle.common.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.struggle.common.system.entity.EmailRecord;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Map;

/**
 * 邮件发送记录Service
 *
 * @author EleAdmin
 * @since 2019-06-19 04:07:02
 */
public interface EmailRecordService extends IService<EmailRecord> {

    /**
     * 发送普通邮件
     *
     * @param title    标题
     * @param content  内容
     * @param toEmails 收件人
     */
    void sendTextEmail(String title, String content, String[] toEmails);

    /**
     * 发送富文本邮件
     *
     * @param title    标题
     * @param html     富文本
     * @param toEmails 收件人
     * @throws MessagingException MessagingException
     */
    void sendFullTextEmail(String title, String html, String[] toEmails) throws MessagingException;

}
