package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @ClassName: SysMessageReadParam
 * @Description: 消息已读记录-Param对象
 * @author xsk
 */
@Schema(description = "消息已读记录Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysMessageReadParam extends BaseParam  implements Serializable{

    private static final long serialVersionUID = 1L;

    @Schema(description = "消息ID")
    private Integer  messageId;

    @Schema(description = "用户ID")
    private Integer  userId;

    @Schema(description = "用户ID")
    @TableField(exist = false)
    private String  username;

    @Schema(description = "用户ID")
    @TableField(exist = false)
    private String  realname;

    @Schema(description = "是否已读 1:是 0：否")
    @TableField(exist = false)
    private  Integer  hasRead;

    @Schema(description="消息Ids")
    @TableField(exist = false)
    private List<Integer> messageIds;
}