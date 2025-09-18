package com.struggle.common.core;

import lombok.Getter;

/**
 * 系统常量
 * Created by EleAdmin on 2019-10-29 15:55
 */
public class Constants {
    /**
     * 默认成功码
     */
    public static final int RESULT_OK_CODE = 0;

    /**
     * 默认失败码
     */
    public static final int RESULT_ERROR_CODE = 1;

    /**
     * 默认成功信息
     */
    public static final String RESULT_OK_MSG = "操作成功";

    /**
     * 默认失败信息
     */
    public static final String RESULT_ERROR_MSG = "操作失败，请联系管理员";

    /**
     * 无权限错误码
     */
    public static final int UNAUTHORIZED_CODE = 403;

    /**
     * 无权限提示信息
     */
    public static final String UNAUTHORIZED_MSG = "没有访问权限";

    /**
     * 无数据权限错误码
     */
    public static final int UNAUTHORIZED_DATA_CODE = 403;

    /**
     * 无数据权限提示信息
     */
    public static final String UNAUTHORIZED_DATA_MSG = "没有数据访问权限";

    /**
     * 未认证错误码
     */
    public static final int UNAUTHENTICATED_CODE = 401;

    /**
     * 未认证提示信息
     */
    public static final String UNAUTHENTICATED_MSG = "请先登录";

    /**
     * 登录过期错误码
     */
    public static final int TOKEN_EXPIRED_CODE = 401;

    /**
     * 登录过期提示信息
     */
    public static final String TOKEN_EXPIRED_MSG = "登录已过期";

    /**
     * 非法token错误码
     */
    public static final int BAD_CREDENTIALS_CODE = 401;

    /**
     * 非法token提示信息
     */
    public static final String BAD_CREDENTIALS_MSG = "请退出重新登录";

    /**
     * 表示升序的值
     */
    public static final String ORDER_ASC_VALUE = "asc";

    /**
     * 表示降序的值
     */
    public static final String ORDER_DESC_VALUE = "desc";

    /**
     * token通过header传递的名称
     */
    public static final String TOKEN_HEADER_NAME = "Authorization";

    /**
     * token通过参数传递的名称
     */
    public static final String TOKEN_PARAM_NAME = "access_token";

    /**
     * token认证类型
     */
    public static final String TOKEN_TYPE = "Bearer";


    /**
     * SCOPE_TYPE 数据权限
     */
    public enum SCOPE_TYPE {
        SCOPE_TYPE_1("1", "无数据权限"),
        SCOPE_TYPE_2("2", "全部"),
        SCOPE_TYPE_3("3", "所在机构"),
        SCOPE_TYPE_4("4", "所在机构和下级机构"),
        SCOPE_TYPE_5("5", "主管机构"),
        SCOPE_TYPE_6("6", "主管机构和下级机构"),
        SCOPE_TYPE_7("7", "指定机构"),
        SCOPE_TYPE_8("8", "指定机构和下级机构");

        @Getter
        private String code;
        @Getter
        private String name;

        SCOPE_TYPE(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
