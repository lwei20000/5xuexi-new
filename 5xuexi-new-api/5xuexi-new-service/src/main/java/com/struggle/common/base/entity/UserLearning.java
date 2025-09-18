package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 学习记录信息
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserLearning", description = "学习记录信息")
@TableName("t_user_learning")
public class UserLearning implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="学习记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="用户id")
    private Integer userId;

    @Schema(description="专业id")
    private Integer majorId;

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="章节课时ID")
    private Integer coursewareId;

    @Schema(description="播放进度")
    private Integer platProgress;

    @Schema(description="当前播放时间")
    private Integer currentPlayTime;

    @Schema(description="总播放时间（秒)")
    private Integer totalPalyTime;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="记录时长（秒）")
    @TableField(exist = false)
    private Integer time;
}
