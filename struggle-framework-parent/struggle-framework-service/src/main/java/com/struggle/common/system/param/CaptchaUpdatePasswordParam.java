package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 手机号或者邮箱找回密码
 *
 * @author EleAdmin
 * @since 2021-08-30 17:35:16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaptchaUpdatePasswordParam", description = "找回密码")
public class CaptchaUpdatePasswordParam extends SendCaptchaParam implements Serializable {

    @Schema(description="新密码")
    private String password;
}
