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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 文档
 *
 * @author EleAdmin
 * @since 2018-12-24 16:10:01
 */
@Data
@Schema(name = "Doc", description = "文档")
@TableName("sys_doc")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Doc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="文档id")
    @TableId(type = IdType.AUTO)
    private Integer docId;

    @Schema(description="上级id, 0是顶级")
    private Integer parentId;

    @Schema(description="默认角色Id")
    @NotNull(message = "默认角色不能为空")
    private Integer defaultRoleId;

    @Schema(description="文档类型")
    @NotBlank(message = "文档类型不能为空")
    @Length(max = 100, message = "文档类型最大长度不能超过{max}")
    private String docType;

    @Schema(description = "排序号")
    private Integer sortNumber;

    @Schema(description="文档名称")
    @NotBlank(message = "文档名称不能为空")
    @Length(max = 200, message = "文档名称最大长度不能超过{max}")
    private String name;

    @Schema(description="文档内容")
    @Length(max = 65535, message = "文档内容最大长度不能超过{max}")
    private String content;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}