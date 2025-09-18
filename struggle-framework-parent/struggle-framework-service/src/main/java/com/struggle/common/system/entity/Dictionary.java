package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典
 *
 * @author EleAdmin
 * @since 2020-03-14 11:29:03
 */
@Data
@Schema(name = "Dictionary", description = "字典")
@TableName("sys_dictionary")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dictionary implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "字典id")
    @TableId(type = IdType.AUTO)
    private Integer dictId;

    @Schema(description = "字典标识")
    @NotBlank(message = "字典标识不能为空")
    @Length(max = 100, message = "字典标识最大长度不能超过{max}")
    private String dictCode;

    @Schema(description = "字典名称")
    @NotBlank(message = "字典名称不能为空")
    @Length(max = 200, message = "字典名称最大长度不能超过{max}")
    private String dictName;

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

}
