package com.struggle.common.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 租户创建表
 *
 * @author EleAdmin
 * @since 2022-10-16 11:59:06
 */
@Data
@Schema(name = "TenantTable", description = "租户创建表对象")
@TableName("sys_tenant_table")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantTable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "数据源")
    private String dataSource;

    @Schema(description = "创建sql")
    private String createSql;

    @Schema(description="创建时间")
    private Date createTime;

    @Schema(description="修改时间")
    private Date updateTime;
}
