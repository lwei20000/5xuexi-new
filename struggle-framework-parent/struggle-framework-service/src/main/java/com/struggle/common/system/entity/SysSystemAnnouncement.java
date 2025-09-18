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
 * @ClassName: SysSystemAnnouncement
 * @Description: 系统通知公告-实体对象
 * @author xsk
 */

@Data
@Schema(name = "SysSystemAnnouncement", description = "系统通知公告")
@TableName("sys_system_announcement")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysSystemAnnouncement implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "系统公告id")
    @TableId(type = IdType.AUTO)
    private Integer  systemAnnouncementId;

    @Schema(description = "系统公告名称")
    @Length(max = 200, message = "系统公告名称长度不能超过{max}")
    @NotBlank(message = "系统公告名称不能为空")
    private String  title;

    @Schema(description = "系统公告封面")
    @Length(max = 200, message = "系统公告封面长度不能超过{max}")
    private String  systemAnnouncementPicture;

    @Schema(description = "系统公告类型")
    @Length(max = 100, message = "系统公告类型长度不能超过{max}")
    private String  systemAnnouncementType;

    @Schema(description = "系统公告置顶")
    private Long  topTimestamp;

    @Schema(description = "系统公告内容")
    private String  content;

    @Schema(description = "系统公告内容文本")
    private String  contentText;

    @Schema(description = "系统公告附件")
    @Length(max = 1000, message = "系统公告附件长度不能超过{max}")
    private String  systemAnnouncementAttachment;

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
    private List<SysSystemAnnouncementReceive> sysSystemAnnouncementReceiveList;
}