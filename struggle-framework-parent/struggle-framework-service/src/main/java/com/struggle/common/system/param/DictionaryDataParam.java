package com.struggle.common.system.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.web.BaseParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典数据查询参数
 *
 * @author EleAdmin
 * @since 2021-08-28 22:12:02
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "DictionaryDataParam", description = "字典数据查询参数")
public class DictionaryDataParam extends BaseParam {
    private static final long serialVersionUID = 1L;

    @Schema(description = "字典数据id")
    @QueryField(type = QueryType.EQ)
    private Integer dictDataId;

    @Schema(description="上级id")
    @QueryField(type = QueryType.EQ)
    private Integer parentId;

    @Schema(description = "字典id")
    @QueryField(type = QueryType.EQ)
    private Integer dictId;

    @Schema(description = "字典数据标识")
    private String dictDataCode;

    @Schema(description = "字典数据名称")
    private String dictDataName;

    @Schema(description = "备注")
    private String comments;

    /**
     * dictCode字典代码 + parentDictDataCode父字典代码 可以联动查询下级字典
     */
    @Schema(description = "上级字典代码")
    @TableField(exist = false)
    private String parentDictDataCode;

    @Schema(description = "字典代码")
    @TableField(exist = false)
    private String dictCode;

    @Schema(description = "字典名称")
    @TableField(exist = false)
    private String dictName;

    @Schema(description = "字典数据代码或字典数据名称")
    @TableField(exist = false)
    private String keywords;

}
