package com.struggle.common.exam.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @ClassName: TPaperParam
 * @Description: 试卷-Param对象
 * @author xsk
 */
@Schema(name = "TPaperParam",description= "试卷Param对象")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TPaperParam  extends BaseParam  implements Serializable{

private static final long serialVersionUID = 1L;
    @Schema(description="试卷ID")
    @QueryField(type = QueryType.EQ)
    private Integer  paperId;

    @Schema(description="课程ID")
    @QueryField(type = QueryType.EQ)
    private Integer  courseId;

    @Schema(description="是否有默认试卷 0：否 1：是")
    @QueryField(type = QueryType.EQ)
    private Integer  defaultPaper;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;


    @Schema(description="专业ID")
    @TableField(exist = false)
    private Integer  majorId;

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
}