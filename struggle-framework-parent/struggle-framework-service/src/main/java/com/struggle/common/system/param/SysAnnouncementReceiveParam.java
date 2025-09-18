package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysAnnouncementReceiveParam
 * @Description: 通知公告接收对象-Param对象
 * @author xsk
 */
@Data
@Schema(description = "通知公告接收对象Param对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAnnouncementReceiveParam  extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "公告ID")
    private Integer  announcementId;

    @Schema(description = "公告名称")
    @TableField(exist = false)
    private String  title;

    @Schema(description = "公告类型")
    @TableField(exist = false)
    private String  announcementType;

    @Schema(description = "用户Id")
    @TableField(exist = false)
    private Integer userId;

    @Schema(description = "用户加入租户时间")
    @TableField(exist = false)
    private Date userTenantCreateTime;

    @Schema(description = "是否已读 1:是 0：否")
    @TableField(exist = false)
    private  Integer  hasRead;

    @Schema(description = "是否获取详细信息")
    @TableField(exist = false)
    private  boolean  detail;
}