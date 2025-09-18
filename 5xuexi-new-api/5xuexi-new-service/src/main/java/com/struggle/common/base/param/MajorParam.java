package com.struggle.common.base.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 招生专业查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="MajorParam", description = "招生专业查询参数")
public class MajorParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="专业id")
    @QueryField(type = QueryType.EQ)
    private Integer majorId;

    @Schema(description="专业年份")
    private String majorYear;

    @Schema(description="专业代码")
    private String majorCode;

    @Schema(description="专业名称")
    private String majorName;

    @Schema(description="专业图片")
    private String majorPicture;

    @Schema(description="专业教育类别")
    private String majorType;

    @Schema(description="专业层次")
    private String majorGradation;

    @Schema(description="专业学习形式")
    private String majorForms;

    @Schema(description="学制")
    private String majorLength;

    @Schema(description="排序号")
    @QueryField(type = QueryType.EQ)
    private Integer sortNumber;

    @Schema(description="备注")
    private String comments;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="学年")
    @TableField(exist = false)
    private String academicYear;

    @Schema(description="搜索关键字")
    @TableField(exist = false)
    private String keywords;
}
