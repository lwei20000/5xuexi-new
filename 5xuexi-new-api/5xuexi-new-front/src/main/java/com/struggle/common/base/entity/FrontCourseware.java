package com.struggle.common.base.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="FrontCourseware", description = "前端Courseware对象")
public class FrontCourseware extends Courseware{
    //其他扩展字段
    @Schema(description="进度")
    private Integer progress;

    @Schema(description="当前播放时间")
    private Integer currentPlayTime;

    @Schema(description="总播放时间（秒)")
    private Integer totalPalyTime;

    @Schema(description="最后播放")
    private boolean lastPlay;
}
