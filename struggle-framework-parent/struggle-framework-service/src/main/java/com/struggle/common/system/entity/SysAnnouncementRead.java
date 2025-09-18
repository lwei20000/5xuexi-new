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
 * @ClassName: SysAnnouncementRead
 * @Description: 通知公告已读记录-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysAnnouncementRead", description = "通知公告已读记录")
@TableName("sys_announcement_read")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysAnnouncementRead implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description = "公告ID")
    @NotNull(message = "公告ID不能为空")
    private Integer  announcementId;

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