package com.struggle.common.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author dxf
 * @ClassName: TSysUserProblem
 * @Description: 安全问题-实体对象
 */
@Data
@Schema(name = "SysUserProblem", description = "安全问题")
@TableName("sys_user_problem")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysUserProblem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableId(type = IdType.AUTO)
    private Integer id;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "安全问题")
    @NotBlank(message = "安全问题不能为空")
    @Length(max = 255, message = "安全问题最大长度不能超过{max}")
    private String problem;

    @Schema(description = "答案")
    @NotBlank(message = "答案不能为空")
    @Length(max = 255, message = "答案最大长度不能超过{max}")
    private String answer;

    @Schema(description = "排序号")
    private Integer sortNumber;

    @Schema(description = "注册时间", hidden = true)
    private Date createTime;

    @Schema(description = "修改时间", hidden = true)
    private Date updateTime;
}