package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传记录查询参数
 *
 * @author EleAdmin
 * @since 2021-08-30 11:29:31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "FileRecordParam", description = "文件上传记录查询参数")
public class FileRecordParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @QueryField(type = QueryType.EQ)
    @Schema(description="主键id")
    private Integer id;

    @Schema(description="文件名称")
    private String name;

    @Schema(description="文件存储路径")
    private String path;

    @QueryField(type = QueryType.EQ)
    @Schema(description="创建人")
    private Integer createUserId;

    @Schema(description="备注")
    private String comments;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建人账号")
    @TableField(exist = false)
    private String createUsername;

    @Schema(description="创建人名称")
    @TableField(exist = false)
    private String createRealname;

}
