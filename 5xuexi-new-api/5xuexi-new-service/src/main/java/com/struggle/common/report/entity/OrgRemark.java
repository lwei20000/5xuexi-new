package com.struggle.common.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.struggle.common.system.entity.Org;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @ClassName: OrgRemark
 * @Description: 站点付费备注-实体对象
 * @author xsk
 */
@Schema(name = "OrgRemark",description= "站点付费备注")
@Data
@TableName("t_org_remark")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrgRemark implements Serializable{

private static final long serialVersionUID = 1L;

    @Schema(description="主键ID")
    @TableId(type = IdType.AUTO)
    private Integer  id;

    @Schema(description="年级")
    @NotBlank(message = "年级不能为空")
    private String  year;

    @Schema(description="站点ID")
    @NotNull(message = "站点不能为空")
    private Integer  orgId;

    @Schema(description="站点沟通记录")
    private String  remark;

    @Schema(description="是否付费（0否，1是）")
    @NotNull(message = "是否付费不能为空")
    private Integer  payFlag;

    @Schema(description="创建时间")
    private  Date  createTime;

    @Schema(description="修改时间")
    private  Date  updateTime;

    @Schema(description="学生人数")
    @TableField(exist = false)
    private Integer studentNum;

    @Schema(description="站点")
    @TableField(exist = false)
    private Org org;
}