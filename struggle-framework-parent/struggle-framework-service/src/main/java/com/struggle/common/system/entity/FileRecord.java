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
 * 文件上传记录
 *
 * @author EleAdmin
 * @since 2021-08-29 12:36:32
 */
@Data
@Schema(name = "FileRecord", description = "文件上传记录")
@TableName("sys_file_record")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="主键id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description="文件名称")
    @NotBlank(message = "文件名称不能为空")
    @Length(max = 200, message = "文件名称最大长度不能超过{max}")
    private String name;

    @Schema(description="文件存储路径")
    @NotBlank(message = "文件存储路径不能为空")
    @Length(max = 400, message = "文件存储路径最大长度不能超过{max}")
    private String path;

    @Schema(description="文件大小")
    @NotNull(message = "文件大小不能为空")
    private Long length;

    @Schema(description="上传类型")
    @NotBlank(message = "上传类型不能为空")
    @Length(max = 80, message = "文件类型最大长度不能超过{max}")
    private String uploadType;

    @Schema(description="文件类型")
    @NotBlank(message = "文件类型不能为空")
    @Length(max = 80, message = "文件类型最大长度不能超过{max}")
    private String contentType;

    @Schema(description="备注")
    @Length(max = 400, message = "备注最大长度不能超过{max}")
    private String comments;

    @Schema(description="创建人")
    private Integer createUserId;

    @Schema(description="是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;

    @Schema(description="创建人账号")
    @TableField(exist = false)
    private String createUsername;

    @Schema(description="创建人名称")
    @TableField(exist = false)
    private String createRealname;

}
