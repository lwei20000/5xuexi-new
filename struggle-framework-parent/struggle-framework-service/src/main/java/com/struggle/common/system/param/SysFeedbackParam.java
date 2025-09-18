package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: SysFeedbackParam
 * @Description: 意见反馈-Param对象
 * @author xsk
 */
@Data
@Schema(description = "意见反馈Param对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysFeedbackParam extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Integer  userId;

    @Schema(description = "反馈标题")
    private String  title;

    @Schema(description="状态, 0未解决, 1待验证 2：已解决")
    private Integer status;
}