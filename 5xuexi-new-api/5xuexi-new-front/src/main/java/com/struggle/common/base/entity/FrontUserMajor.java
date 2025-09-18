package com.struggle.common.base.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="FrontUserMajor", description = "前端用户专业对象")
public class FrontUserMajor extends UserMajor{

    @Schema(description="专业")
    private FrontMajor frontMajor;

    @Schema(description="课程")
    private List<FrontCourse> frongCourseList;

    @Schema(description="成绩比例规则")
    private ScoreRule scoreRule;
}
