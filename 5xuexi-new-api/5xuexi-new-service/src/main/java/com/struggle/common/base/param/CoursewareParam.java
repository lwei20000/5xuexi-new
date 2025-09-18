package com.struggle.common.base.param;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 章节课时信息查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="CoursewareParam", description = "章节课时信息查询参数")
public class CoursewareParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="章节课时id")
    @QueryField(type = QueryType.EQ)
    private Integer coursewareId;

    @Schema(description="课程id")
    @QueryField(type = QueryType.EQ)
    private Integer courseId;

    @Schema(description="章节课时名称")
    private String coursewareName;

    @Schema(description="排序号")
    @QueryField(type = QueryType.EQ)
    private Integer sortNumber;

    @Schema(description="文件时长")
    @QueryField(type = QueryType.EQ)
    private Integer duration;

    @Schema(description="文件大小")
    @QueryField(type = QueryType.EQ)
    private Integer size;

    @Schema(description="文件类型(PDF、VIDEO)")
    private String fileType;

    @Schema(description="文件地址")
    private String fileUrl;

    @Schema(description="上级id")
    @QueryField(type = QueryType.EQ)
    private Integer parentId;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="租户ids")
    @QueryField(value="tenant_id",type = QueryType.IN)
    private List<Integer> tenantIds;

}
