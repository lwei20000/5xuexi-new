package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: SysStaticPicture
 * @Description: 静态图片-实体对象
 * @author xsk
 */
@Data
@Schema(name = "SysStaticPicture", description = "静态图片")
@TableName("sys_static_picture")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysStaticPicture implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description = "主键Id")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description = "图片key")
    @Length(max = 100, message = "图片key长度不能超过{max}")
    @NotBlank(message = "图片key不能为空")
    private String  url;

    @Schema(description = "图片名称")
    @Length(max = 100, message = "图片名称长度不能超过{max}")
    @NotBlank(message = "图片名称不能为空")
    private String  name;

    @Schema(description = "图片地址")
    @Length(max = 300, message = "图片地址长度不能超过{max}")
    @NotBlank(message = "图片地址不能为空")
    private String  picture;

    @Schema(description = "是否删除, 0否, 1是")
    @TableLogic
    private Integer  deleted;

    @Schema(description = "创建时间")
    private  Date  createTime;

    @Schema(description = "修改时间")
    private  Date  updateTime;

}