package com.struggle.common.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="FrontCourse", description = "前端课程对象")
public class FrontCourse extends Course {
    //其他扩展字段
    @Schema(description="学期")
    private String semester;

    @Schema(description="专业ID")
    private Integer majorId;

    @Schema(description="学习进度")
    private Integer learingProgress;

    @Schema(description="学习行为成绩")
    private Integer learingScore;

    @Schema(description="考试成绩")
    private Integer examScore;

    @Schema(description="总成绩")
    private Integer totalScore;

    @Schema(description="考试状态 1：没有考试 2：正常考试 3：补考 4:待批改")
    private Integer hasExam;

    @Schema(description="考试开始时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm")
    private Date startTime;

    @Schema(description="考试结束时间")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm")
    private Date endTime;

    @Schema(description="用户考试记录")
    private List<FrontUserMajorCourseExam> userMajorCourseExamList;

    @Schema(description="考试状态 0：无未批改试卷 1：有未批改试卷")
    private Integer hasExamCorrect;
}
