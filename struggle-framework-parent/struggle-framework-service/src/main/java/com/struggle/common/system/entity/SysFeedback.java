package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysFeedback
 * @Description: 意见反馈-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysFeedback", description = "意见反馈")
@TableName("sys_feedback")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysFeedback implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description = "用户id")
    private Integer  userId;

    @Schema(description = "反馈标题")
    @Length(max = 200, message = "反馈标题长度不能超过{max}")
    @NotBlank(message = "反馈标题不能为空")
    private String  title;

    @Schema(description = "反馈描述")
    @NotBlank(message = "反馈描述不能为空")
    private String  comments;

    @Schema(description = "反馈图片")
    @Length(max = 2000, message = "反馈图片长度不能超过{max}")
    private String  pictures;

    @Schema(description = "手机号")
    @Length(max = 20, message = "手机号长度不能超过{max}")
    private String  phone;

    @Schema(description = "反馈时间")
    private  Date  time;

    @Schema(description = "回复描述")
    private String  replyComments;

    @Schema(description = "回复图片")
    @Length(max = 2000, message = "回复图片长度不能超过{max}")
    private String  replyPictures;

    @Schema(description = "回复时间")
    private  Date   replyTime;

    @Schema(description = "回复者id")
    private Integer  replyUserId;

    @Schema(description="状态, 0未解决, 1待验证 2：已解决")
    private Integer status;

    @Schema(description = "创建时间")
    private  Date  createTime;

    @Schema(description = "修改时间")
    private  Date  updateTime;


    @Schema(description = "用户")
    @TableField(exist = false)
    private  User user;

    @Schema(description = "回复人")
    @TableField(exist = false)
    private  User  replyUser;
}