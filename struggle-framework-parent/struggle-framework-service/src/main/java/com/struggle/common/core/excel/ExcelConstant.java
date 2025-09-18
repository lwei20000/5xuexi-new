package com.struggle.common.core.excel;

import lombok.Getter;

/**
 * Excle常量类
 *
 * @Auther: dejun.mei
 * @Date: 2019-10-18
 * @version: 1.0
 */
public class ExcelConstant {
    /**
     * EXCEL 相关常量表
     */
    public enum EXCEL {
        COLLEGE_EXCEL_PATH("/template"),
        COLLEGE_EXCEL_FINAL_POSION(".xlsx"),
        /**
         * 空白模板
         */
        COLLEGE_EXCEL_EXPORT_DATA("/DTBTDCMB.xlsx");

        @Getter
        private String code;

        EXCEL(String code) {
            this.code = code;
        }
    }

}
