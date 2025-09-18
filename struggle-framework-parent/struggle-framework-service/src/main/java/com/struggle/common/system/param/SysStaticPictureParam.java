package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: SysStaticPictureParam
 * @Description: 静态图片-Param对象
 * @author xsk
 */
@Data
@Schema(description = "静态图片Param对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysStaticPictureParam  extends BaseParam  implements Serializable{

    @Schema(description = "图片key")
    private String  url;

    @Schema(description = "图片名称")
    private String  name;
}