package com.struggle.common.core.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * 数据权限参数基本字段
 *
 * @author EleAdmin
 * @since 2021-08-26 22:14:43
 */
@Data
public class DataPermissionParam implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description="数据权限类型 1：无数据权限 2：全部 3：机构范围权限，并且有机构 4、机构范围权限，并且没有机构")
    private String permissionType;

    @Schema(description="机构Ids")
    private List<Integer> orgIds;

    /**
     * PERMISSION_TYPE 数据权限
     */
    public enum PERMISSION_TYPE {
        PERMISSION_TYPE_1("1", "无数据权限"),
        PERMISSION_TYPE_2("2", "全部"),
        PERMISSION_TYPE_3("3", "机构范围权限，并且有机构"),
        PERMISSION_TYPE_4("4", "机构范围权限，并且无机构");
        @Getter
        private String code;
        @Getter
        private String name;

        PERMISSION_TYPE(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
