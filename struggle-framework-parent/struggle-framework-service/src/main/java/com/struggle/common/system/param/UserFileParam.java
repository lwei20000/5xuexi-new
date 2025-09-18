package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户文件查询参数
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "用户文件查询参数")
public class UserFileParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @QueryField(type = QueryType.EQ)
    private Integer id;

    @Schema(description = "用户id")
    @QueryField(type = QueryType.EQ)
    private Integer userId;

    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "是否是文件夹, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer isDirectory;

    @Schema(description="上传类型")
    private String uploadType;

    @Schema(description="文件类型")
    private String contentType;

    @Schema(description = "上级id")
    @QueryField(type = QueryType.EQ)
    private Integer parentId;

    @Schema(description = "是否删除, 0否, 1是")
    @QueryField(type = QueryType.EQ)
    private Integer deleted;

    @Schema(description="搜索关键字")
    @TableField(exist = false)
    private String keywords;
}
