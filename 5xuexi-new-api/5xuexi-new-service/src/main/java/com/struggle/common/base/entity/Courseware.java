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
 * 章节课时信息
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="Courseware", description = "章节课时信息")
@TableName("t_courseware")
public class Courseware implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="章节课时id")
    @TableId(value = "courseware_id", type = IdType.AUTO)
    private Integer coursewareId;

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="章节课时名称")
    private String coursewareName;

    @Schema(description="排序号")
    private Integer sortNumber;

    @Schema(description="文件时长")
    private Integer duration;

    @Schema(description="文件大小")
    private Integer size;

    @Schema(description="文件类型(pdf、mp4)")
    private String fileType;

    @Schema(description="文件地址")
    private String fileUrl;

    @Schema(description="上级id")
    private Integer parentId;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

}
