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
 * @ClassName: SysMessageReceiveParam
 * @Description: 消息接收对象-Param对象
 * @author xsk
 */
@Data
@Schema(description = "消息接收对象Param对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysMessageReceiveParam extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID")
    private Integer  messageId;

    @Schema(description = "消息名称")
    @TableField(exist = false)
    private String  title;

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