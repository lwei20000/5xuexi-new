package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典数据
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:04
 */
@Data
@Schema(name = "DictionaryData", description = "字典数据")
@TableName("sys_dictionary_data")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DictionaryData implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "字典数据id")
    @TableId(type = IdType.AUTO)
    private Integer dictDataId;

    @Schema(description="上级id")
    private Integer parentId;

    @Schema(description = "字典id")
    @NotNull(message = "字典标识不能为空")
    private Integer dictId;

    @Schema(description = "字典数据标识")
    @NotBlank(message = "字典数据标识不能为空")
    @Length(max = 100, message = "字典数据标识最大长度不能超过{max}")
    private String dictDataCode;

    @Schema(description = "字典数据名称")
    @NotBlank(message = "字典数据名称不能为空")
    @Length(max = 200, message = "字典数据名称最大长度不能超过{max}")
    private String dictDataName;

    @Schema(description = "排序号")
    private Integer sortNumber;

    @Schema(description = "备注")
    @Length(max = 400, message = "备注最大长度不能超过{max}")
    private String comments;

    @Schema(description = "是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

    @Schema(description = "字典代码")
    @TableField(exist = false)
    private String dictCode;

    @Schema(description = "字典名称")
    @TableField(exist = false)
    private String dictName;

    @Schema(description = "上级字典代码")
    @TableField(exist = false)
    private String parentDictCode;

    @Schema(description = "上级字典名称")
    @TableField(exist = false)
    private String parentDictName;

    @Schema(description = "上级字典数据代码")
    @TableField(exist = false)
    private String parentDictDataCode;

    @Schema(description = "上级字典数据名称")
    @TableField(exist = false)
    private String parentDictDataName;
}
