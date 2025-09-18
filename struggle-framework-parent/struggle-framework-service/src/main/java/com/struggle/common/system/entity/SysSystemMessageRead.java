package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysSystemMessageRead
 * @Description: 系统消息已读记录-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysSystemMessageRead", description = "系统消息已读记录")
@TableName("sys_system_message_read")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysSystemMessageRead implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description = "系统消息ID")
    @NotNull(message = "系统消息ID不能为空")
    private Integer  systemMessageId;

    @Schema(description = "用户ID")
    @NotNull(message = "用户ID不能为空")
    private Integer  userId;

    @Schema(description = "是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除, 0否, 1是取值范围为0-1")
    @TableLogic
    private Integer  deleted;

    @Schema(description = "创建时间")
    private  Date  createTime;

    @Schema(description = "修改时间")
    private  Date  updateTime;

    @Schema(description="用户名")
    @TableField(exist = false)
    private String username;

    @Schema(description="姓名")
    @TableField(exist = false)
    private String realname;
}