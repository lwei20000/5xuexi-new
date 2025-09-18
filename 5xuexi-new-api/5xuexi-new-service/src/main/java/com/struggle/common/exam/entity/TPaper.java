package com.struggle.common.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.base.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: TPaper
 * @Description: 试卷-实体对象
 * @author xsk
 */
@Schema(name= "TPaper", description= "试卷")
@Data
@TableName("t_paper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TPaper implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="试卷ID")
    @TableId(type = IdType.AUTO)
    private Integer  paperId;

    @Schema(description="课程ID")
    @NotNull(message = "课程不能为空")
    private Integer  courseId;

    @Schema(description="试卷名称")
    @Length(max = 200, message = "试卷名称长度不能超过{max}")
    @NotBlank(message = "试卷名称不能为空")
    private String  paperName;

    @Schema(description="试卷类型 1：文件试卷 2：题目试卷")
    @Range(min = 1, max = 2, message = "试卷类型 1：文件试卷 2：题目试卷")
    @NotNull(message = "试卷类型不能为空")
    private Integer  paperType;

    @Schema(description="文件地址")
    @Length(max = 300, message = "文件地址长度不能超过{max}")
    private String  paperFile;

    @Schema(description="默认试卷：0：否  1：是")
    @Range(min = 0, max = 1, message = "默认试卷：0：否  1：是")
    @NotNull(message = "默认试卷不能为空")
    private Integer  paperUsage;

    @Schema(description="是否删除, 0否, 1是")
    @Range(min = 0, max = 1, message = "是否删除, 0否, 1是")
    @TableLogic
    private Integer  deleted;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

    @Schema(description="课程")
    @TableField(exist = false)
    private Course course;

    @Schema(description="题目")
    @TableField(exist = false)
    private List<TPaperQuestion> paperQuestionList;

    @Schema(description="作答")
    @TableField(exist = false)
    private TUserMajorCourseExam userMajorCourseExam;

}