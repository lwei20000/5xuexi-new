package com.struggle.common.core.web;

import com.baomidou.mybatisplus.annotation.TableField;
import com.struggle.common.core.annotation.QueryField;
import com.struggle.common.core.annotation.QueryType;
import com.struggle.common.core.utils.CommonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询参数基本字段
 *
 * @author EleAdmin
 * @since 2021-08-26 22:14:43
 */
@Data
public class BaseParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    @Schema(description="分页查询页码")
    private Long page;

    @TableField(exist = false)
    @Schema(description="分页查询每页数量")
    private Long limit;

    @TableField(exist = false)
    @Schema(description = "排序字段，排序字段或sql, 如果是sql则order字段无用, 如: `id asc, name desc`")
    private String sort;

    @TableField(exist = false)
    @Schema(description = "排序方式，sort是字段名称时对应的排序方式, asc升序, desc降序")
    private String order;

    @QueryField(value = "create_time", type = QueryType.GE)
    @TableField(exist = false)
    @Schema(description="创建时间起始值")
    private String createTimeStart;

    @QueryField(value = "create_time", type = QueryType.LE)
    @TableField(exist = false)
    @Schema(description="创建时间结束值")
    private String createTimeEnd;

    /**
     * 获取集合中的第一条数据
     *
     * @param records 集合
     * @return 第一条数据
     */
    public <T> T getOne(List<T> records) {
        return CommonUtil.listGetOne(records);
    }

}
