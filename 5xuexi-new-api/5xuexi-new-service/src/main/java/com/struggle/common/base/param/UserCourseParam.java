package com.struggle.common.base.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户课程查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserCourseParam", description = "用户课程查询参数")
public class UserCourseParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户课程id")
    private Integer id;

    @Schema(description="用户id")
    @QueryField(type = QueryType.EQ)
    private Integer userId;

    @Schema(description="专业id")
    @QueryField(type = QueryType.EQ)
    private Integer majorId;

    @Schema(description="课程id")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="机构Id")
    @TableField(exist = false)
    private Integer orgId;

    @Schema(description="机构Ids")
    @TableField(exist = false)
    private List<Integer> orgIds;

    @Schema(description="专业年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="学年")
    @TableField(exist = false)
    private String academicYear;

    @Schema(description="上/下学年")
    @TableField(exist = false)
    private String academicSemester;

    @Schema(description="学期")
    @TableField(exist = false)
    private String semester;

    @Schema(description="专业Ids")
    @TableField(exist = false)
    private List<Integer> majorIds;

    @Schema(description="用户Ids")
    @TableField(exist = false)
    private List<Integer> userIds;

    @Schema(description="课程Ids")
    @TableField(exist = false)
    private List<Integer> courseIds;

    @Schema(description="专业年份List")
    @TableField(exist = false)
    private List<String> majorYears;

    @Schema(description="用户专业Ids")
    @TableField(exist = false)
    private List<Integer> userMajorIds;

    @Schema(description="总成绩")
    @TableField(exist = false)
    private Integer totalScore;
}
