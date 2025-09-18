package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: SysSystemMessageParam
 * @Description: 系统消息-Param对象
 * @author xsk
 */
@Schema(description = "系统消息Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysSystemMessageParam extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "系统消息名称")
    private String  title;

}