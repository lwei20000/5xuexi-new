package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 发送手机或者邮箱验证码
 *
 * @author EleAdmin
 * @since 2021-08-30 17:35:16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "发送验证码")
public class SendCaptchaParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="类型 1:邮箱 2:手机号")
    private String type;

    @Schema(description="手机号")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="图形验证码")
    private String code;

    @Schema(description="图形验证码UUID")
    private String uuid;

}
