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
 * 课程信息查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="CourseParam", description = "课程信息查询参数")
public class CourseParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="课程id")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;

    @Schema(description="课程id")
    @QueryField(value="course_id",type = QueryType.IN)
    private List<Integer> courseIds;

    @Schema(description="课程代码")
    @QueryField(value="course_code",type = QueryType.IN)
    private List<String> courseCodeList;

    @Schema(description="课程代码")
    private String courseCode;

    @Schema(description="课程名称")
    @QueryField(value="course_name",type = QueryType.IN)
    private List<String> courseNameList;

    @Schema(description="课程名称")
    private String courseName;

    @Schema(description="课程图片")
    private String coursePicture;

    @Schema(description="排序号")
    @QueryField(type = QueryType.EQ)
    private Integer sortNumber;

    @Schema(description="备注")
    private String comments;

    @Schema(description="租户id")
    @QueryField(type = QueryType.EQ)
    private Integer tenantId;

    @Schema(description="租户id-别名表的")
    @TableField(exist = false)
    private Integer tenantIdAlias;

    @Schema(description="租户ids")
    @QueryField(value="tenant_id",type = QueryType.IN)
    private List<Integer> tenantIds;

    @Schema(description="是否只查询自己的")
    @TableField(exist = false)
    private boolean bySelf;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="搜索关键字")
    @TableField(exist = false)
    private String keywords;
}
