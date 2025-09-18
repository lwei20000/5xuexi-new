package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典查询参数
 *
 * @author EleAdmin
 * @since 2021-08-28 22:12:01
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DictionaryParam", description = "字典查询参数")
public class DictionaryParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @QueryField(type = QueryType.EQ)
    @Schema(description = "字典id")
    private Integer dictId;

    @Schema(description = "字典标识")
    private String dictCode;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "字典数据代码或字典数据名称")
    @TableField(exist = false)
    private String keywords;

}
