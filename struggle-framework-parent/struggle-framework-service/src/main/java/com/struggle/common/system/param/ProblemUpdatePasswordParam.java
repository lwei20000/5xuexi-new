package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 安全问题 修改密码参数
 *
 * @author EleAdmin
 * @since 2021-08-30 17:35:16
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ProblemUpdatePasswordParam", description = "安全问题修改密码参数")
public class ProblemUpdatePasswordParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="安全问题答案")
    private List<String> answers;

    @Schema(description="用户名")
    private String username;

    @Schema(description="新密码")
    private String password;
}
