package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysmessageReceive
 * @Description: 消息接收对象-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysMessageReceive", description = "消息接收对象")
@TableName("sys_message_receive")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysMessageReceive implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description = "消息ID")
    @NotNull(message = "消息ID不能为空")
    private Integer  messageId;

    @Schema(description = "接收类型：1、全员  2、机构  3、角色  4、用户  5、分组")
    @Length(max = 10, message = "接收类型长度不能超过{max}")
    @NotBlank(message = "接收类型不能为空")
    private String  receiveType;

    @Schema(description = "接收类型ID，全员为空")
    private Integer  receiveId;

    @Schema(description = "新加入的可见, 0否, 1是")
    @Range(min = 0, max = 1, message = "新加入的可见, 0否, 1是取值范围为0-1")
    private Integer  addNewly;

    @Schema(description = "是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除, 0否, 1是取值范围为0-1")
    @TableLogic
    private Integer  deleted;

    @Schema(description = "创建时间")
    private  Date  createTime;

    @Schema(description = "修改时间")
    private  Date  updateTime;

    @Schema(description = "接收类型名称")
    @TableField(exist = false)
    private String  receiveName;
}