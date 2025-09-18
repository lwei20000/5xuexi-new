package com.struggle.common.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * CAS服务
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Data
@Schema(name = "Cas", description = "CAS服务")
@TableName("sys_cas")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cas implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "服务id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "服务名称")
    private String name;

    @Schema(description = "Cas类型")
    private String casType;

    @Schema(description = "服务键值")
    private String appKey;

    @Schema(description = "服务密钥")
    private String appSecret;

    @Schema(description="服务标识")
    private String serviceId;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private int sort;

    @Schema(description="返回的额外参数")
    private String allowReturnAttr;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}
