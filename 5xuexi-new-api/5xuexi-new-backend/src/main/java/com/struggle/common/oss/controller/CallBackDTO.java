package com.struggle.common.oss.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="CallBackDTO", description = "CallBackDTO对象")
public class CallBackDTO {

    @Schema(description="课程id")
    private Integer courseId;

    @Schema(description="章节课时id")
    private Integer coursewareId;

    @Schema(description="文件名称")
    private String fileName;

    @Schema(description="文件大小")
    private Long fileSize;

    @Schema(description="时间秒")
    private Long fileDuration;

    @Schema(description="路径")
    private String url;

    @Schema(description="文件后缀名（png、doc、pdf等）")
    private String suffix;
}