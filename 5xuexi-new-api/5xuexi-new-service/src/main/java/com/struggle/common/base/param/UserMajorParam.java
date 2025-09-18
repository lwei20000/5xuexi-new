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
 * 用户与专业关系查询参数
 *
 * @author EleAdmin
 * @since 2022-10-20 09:26:22
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name ="UserMajorParam", description = "用户与专业关系查询参数")
public class UserMajorParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description="用户与专业关系id")
    @QueryField(type = QueryType.EQ)
    private Integer id;

    @Schema(description="用户id")
    @QueryField(type = QueryType.EQ)
    private Integer userId;

    @Schema(description="专业id")
    @QueryField(type = QueryType.EQ)
    private Integer majorId;

    @Schema(description="学号")
    private String userNumber;

    @Schema(description="录取照片")
    private String userPicture;

    @Schema(description="机构Id")
    @QueryField(type = QueryType.EQ)
    private Integer orgId;

    @Schema(description="机构Ids")
    @TableField(exist = false)
    private List<Integer> orgIds;

    @Schema(description="是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="专业年份")
    @TableField(exist = false)
    private String majorYear;

    @Schema(description="专业年份List")
    @TableField(exist = false)
    private List<String> majorYears;

    @Schema(description="学年")
    @TableField(exist = false)
    private String academicYear;

    @Schema(description="专业Ids")
    @TableField(exist = false)
    private List<Integer> majorIds;

    @Schema(description="用户Ids")
    @TableField(exist = false)
    private List<Integer> userIds;
}
