package com.struggle.common.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程信息
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="ScoreRule", description = "成绩规则信息")
@TableName("t_score_rule")
public class ScoreRule implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="成绩规则id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description="学习行为成绩占比")
    private Integer learningScoreProportions;

    @Schema(description="考试成绩占比")
    private Integer examScoreProportions;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}
