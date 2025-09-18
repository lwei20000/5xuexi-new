package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: SysMessageParam
 * @Description: 消息-Param对象
 * @author xsk
 */
@Schema(description = "消息Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysMessageParam extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息名称")
    private String  title;

}