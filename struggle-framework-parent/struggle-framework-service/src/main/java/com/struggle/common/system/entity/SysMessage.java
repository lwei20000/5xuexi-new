package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: SysMessage
 * @Description: 通知公告-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysMessage", description = "通知公告")
@TableName("sys_message")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysMessage implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "消息id")
    @TableId(type = IdType.AUTO)
    private Integer  messageId;

    @Schema(description = "消息名称")
    @Length(max = 200, message = "消息名称长度不能超过{max}")
    @NotBlank(message = "消息名称不能为空")
    private String  title;

    @Schema(description = "消息图标")
    @Length(max = 200, message = "消息图标长度不能超过{max}")
    private String  messagePicture;

    @Schema(description = "消息内容")
    private String  content;

    @Schema(description = "消息跳转地址")
    private String  targetUrl;

    @Schema(description = "是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除, 0否, 1是取值范围为0-1")
    @TableLogic
    private Integer  deleted;

    @Schema(description = "创建时间")
    private  Date  createTime;

    @Schema(description = "修改时间")
    private  Date  updateTime;

    @Schema(description = "是否已读 1:是 0：否")
    @TableField(exist = false)
    private  Integer  hasRead;

    @Schema(description = "已读数/总数")
    @TableField(exist = false)
    private String readTotal;

    @Schema(description = "接收对象List")
    @TableField(exist = false)
    private List<SysMessageReceive> sysMessageReceiveList;
}