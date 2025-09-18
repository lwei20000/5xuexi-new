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
 * 用户文件
 *
 * @author EleAdmin
 * @since 2022-07-21 14:34:40
 */
@Data
@Schema(name = "UserFile", description = "用户文件")
@TableName("sys_user_file")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "文件名称")
    @NotBlank(message = "文件名称不能为空")
    @Length(max = 200, message = "文件名称最大长度不能超过{max}")
    private String name;

    @Schema(description = "是否是文件夹, 0否, 1是")
    private Integer isDirectory;

    @Schema(description = "上级id")
    private Integer parentId;

    @Schema(description = "文件路径")
    private String path;

    @Schema(description = "文件大小")
    private Integer length;

    @Schema(description="上传类型")
    private String uploadType;

    @Schema(description="文件类型")
    private String contentType;

    @Schema(description = "是否删除, 0否, 1是")
    @TableLogic
    private Integer deleted;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "修改时间")
    private Date updateTime;

}
