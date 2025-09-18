package com.struggle.common.system.controller;

import com.struggle.common.core.annotation.AuthorityAnnotation;
import com.struggle.common.core.annotation.OperationLog;
import com.struggle.common.core.utils.ValidatorUtil;
import com.struggle.common.core.web.ApiResult;
import com.struggle.common.core.web.BaseController;
import com.struggle.common.system.entity.EmailRecord;
import com.struggle.common.system.service.EmailRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.MessagingException;

/**
 * 邮件功能控制器
 *
 * @author EleAdmin
 * @since 2020-03-21 00:37:11
 */
@Tag(name = "邮件功能",description = "EmailController")
@RestController
@RequestMapping("/api/system/email")
public class EmailController extends BaseController {
    @Resource
    private EmailRecordService emailRecordService;

    @AuthorityAnnotation("sys:email:send:29bb0cd599a64660b4a79b42027263d6")
    @OperationLog
    @Operation(method = "send",description="发送邮件")
    @PostMapping()
    public ApiResult<?> send(@RequestBody EmailRecord emailRecord) {
        ValidatorUtil._validBean(emailRecord);
        try {
            emailRecordService.sendFullTextEmail(emailRecord.getTitle(), emailRecord.getContent(), emailRecord.getReceiver().split(","));
            emailRecord.setCreateUserId(getLoginUserId());
            emailRecordService.save(emailRecord);
            return success("发送成功");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return fail("发送失败");
    }

}
