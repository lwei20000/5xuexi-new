package com.struggle.common.system.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: SysAnnouncementParam
 * @Description: 通知公告-Param对象
 * @author xsk
 */
@Data
@Schema(description = "通知公告Param对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAnnouncementParam  extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告名称")
    private String  title;

    @Schema(description = "公告类型")
    private String  announcementType;

}